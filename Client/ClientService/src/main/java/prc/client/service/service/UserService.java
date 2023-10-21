package prc.client.service.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prc.client.app.model.vo.UserInfoVo;
import prc.client.service.model.dto.LoginCacheDto;
import prc.client.service.security.SecurityUtils;
import prc.service.common.exception.BizException;
import prc.service.common.utils.BeanUtil;
import prc.service.dao.ISMenuDao;
import prc.service.dao.ISUserDao;
import prc.service.model.entity.ISMenu;
import prc.service.model.entity.ISUser;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private ISMenuDao isMenuDao;

    @Autowired
    private ISUserDao isUserDao;

    public UserInfoVo getUserInfo() {
        LoginCacheDto user = SecurityUtils.getLoginUser();
        if (user.isGoogle()) {
            ISUser dbUser = isUserDao.getOne(new LambdaQueryWrapper<ISUser>().eq(ISUser::getUsername, user.getUsername()));
            if (Objects.isNull(dbUser.getGoogleKey())) {
                throw new BizException("请绑定谷歌验证码");
            }
        }
        List<ISMenu> menus = isMenuDao.getBaseMapper().selectList(new LambdaQueryWrapper<ISMenu>()
                .in(ISMenu::getAuthority, user.getAuthority())
                .eq(ISMenu::getHide, false)
                .orderByAsc(ISMenu::getSortNumber)
        );
        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setRoles(Lists.newArrayList(new UserInfoVo.Role(user.getRoleKey())));
        userInfoVo.setAvatar(user.getAvatar());
        userInfoVo.setAuthorities(menus.stream().map(u -> {
            UserInfoVo.Authorities up = BeanUtil.convertToBean(u, UserInfoVo.Authorities.class);
            up.setMenuId(u.getId());
            return up;
        }).collect(Collectors.toList()));
        userInfoVo.setNickname(user.getUsername());
        userInfoVo.setUserId(user.getId());
        if (Objects.nonNull(user.getTenantId())) {
            userInfoVo.setTenantId(user.getTenantId());
        }
        return userInfoVo;
    }
}
