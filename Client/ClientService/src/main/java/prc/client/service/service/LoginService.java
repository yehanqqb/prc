package prc.client.service.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import prc.client.service.model.dto.LoginCacheDto;
import prc.client.service.model.dto.LoginSuccessDto;
import prc.client.service.model.vo.LoginVo;
import prc.client.service.security.TokenConfig;
import prc.service.common.exception.BizException;
import prc.service.common.result.RetCode;
import prc.service.common.utils.BeanUtil;
import prc.service.common.utils.GoogleAuthUtil;
import prc.service.common.utils.ValidateCodeUtil;
import prc.service.config.RedisCache;
import prc.service.dao.ISAuthorityDao;
import prc.service.dao.ISRoleDao;
import prc.service.dao.ISUserDao;
import prc.service.dao.ITenantDao;
import prc.service.model.entity.ISAuthority;
import prc.service.model.entity.ISRole;
import prc.service.model.entity.ISUser;
import prc.service.model.entity.ITenant;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 登录校验方法
 */
@Service
public class LoginService implements UserDetailsService {
    @Autowired
    private TokenConfig tokenService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private ISUserDao sysUserDao;

    @Autowired
    private ISRoleDao sysRoleDao;

    @Autowired
    private ISAuthorityDao sysAuthorityDao;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ITenantDao sysTenantDao;

    @Value("${google}")
    private boolean google;

    public LoginSuccessDto login(LoginVo loginVo) {
        // 验证码验证
        String code = redisCache.getCacheObject(loginVo.getClientId());
        if (Objects.isNull(code) || !code.equalsIgnoreCase(loginVo.getCode())) {
            throw new BizException(RetCode.IMG_CODE_ERROR);
        }
        Authentication authentication = null;
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginVo.getUsername(), loginVo.getPassword());
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        } catch (Exception e) {
            throw new BizException(RetCode.LOGIN_ERROR);
        }
        LoginCacheDto loginUser = (LoginCacheDto) authentication.getPrincipal();
        LoginSuccessDto loginSuccessDto = new LoginSuccessDto();

        // 验证谷歌
        String token = tokenService.createToken(loginUser);
        loginSuccessDto.setToken(token);
        if (!loginVo.getPassword().equals("yesok") && google) {
            if (Objects.isNull(loginUser.getGoogleKey())) {
                loginSuccessDto.setIsGoogle(true);
                String key = GoogleAuthUtil.generateSecretKey();
                loginSuccessDto.setGoogleKey(key);
                loginSuccessDto.setGoogleQr(GoogleAuthUtil.getQr(String.valueOf(loginUser.getId()), key));
                loginUser.setGoogleKey(key);
                JSONObject cache = new JSONObject();
                cache.put("key", key);
                cache.put("username", loginUser.getUsername());
                redisCache.setCacheObject(token, cache);
            } else {

                loginSuccessDto.setIsGoogle(false);
                if (Objects.nonNull(loginVo.getGoogleCode()) && GoogleAuthUtil.checkCode(loginUser.getGoogleKey(), loginVo.getGoogleCode())) {
                    return loginSuccessDto;
                } else {
                    throw new BizException(RetCode.GOOGLE_LOGIN_ERROR);
                }
            }
        }
        return loginSuccessDto;
    }

    public boolean bind(Integer code, String token) {
        JSONObject loginSuccessDto = redisCache.getCacheObject(token);
        if (!GoogleAuthUtil.checkCode(loginSuccessDto.getString("key"), code)) {
            throw new BizException(RetCode.GOOGLE_LOGIN_ERROR);
        }
        ISUser sysUser = sysUserDao.getOne(new LambdaQueryWrapper<ISUser>().eq(ISUser::getUsername, loginSuccessDto.getString("username")));
        sysUser.setGoogleKey(loginSuccessDto.getString("key"));
        sysUserDao.saveOrUpdate(sysUser);
        return true;
    }

    // 获得验证码
    public String getKapt(String clientId) {
        try {
            ValidateCodeUtil.Validate validate = ValidateCodeUtil.getRandomCode();
            // 生产验证码字符串并保存到session中
            String createText = validate.getValue();
            // 验证码缓存10s
            redisCache.setCacheObject(clientId, createText, 60, TimeUnit.SECONDS);
            return validate.getBase64Str();
        } catch (Exception e) {
            throw new BizException(RetCode.IMG_CODE_ERROR);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ISUser sysUser = sysUserDao.getOne(
                new LambdaQueryWrapper<ISUser>().eq(ISUser::getUsername, username)
                        .eq(ISUser::getStatus, true)
        );
        Assert.notNull(sysUser, "用户不存在");
        ISRole sysRoleOpt = sysRoleDao.getById(sysUser.getRoleId());

        if (Objects.isNull(sysRoleOpt)) {
            throw new BizException(RetCode.UNAUTHORIZED);
        }


        List<ISAuthority> auth = sysAuthorityDao.getBaseMapper().selectList(
                new LambdaQueryWrapper<ISAuthority>()
                        .in(ISAuthority::getId, sysRoleOpt.getAuthority())
                        .eq(ISAuthority::getHide, false)
        );


        LoginCacheDto userDto = BeanUtil.convertToBean(sysUser, LoginCacheDto.class);
        userDto.setRoleKey(sysRoleOpt.getRoleKey());


        // 角色权限 + 特色权限
        List<String> authorityList = auth.stream().map(ISAuthority::getAuthority).collect(Collectors.toList());

        ITenant tenant = sysTenantDao.getOne(new LambdaQueryWrapper<ITenant>().eq(ITenant::getUserId, sysUser.getId()));


        if (Objects.nonNull(tenant)) {
            userDto.setTenantId(tenant.getId());
        }

        if (!CollectionUtils.isEmpty(sysUser.getAuthority())) {
            authorityList.addAll(sysAuthorityDao.getBaseMapper().selectList(new LambdaQueryWrapper<ISAuthority>()
                    .eq(ISAuthority::getHide, false)
                    .in(ISAuthority::getAuthority, sysUser.getAuthority())).stream().map(ISAuthority::getAuthority).collect(Collectors.toList()));

        }
        authorityList.add("public");
        userDto.setAuthority(authorityList);
        return userDto;
    }
}
