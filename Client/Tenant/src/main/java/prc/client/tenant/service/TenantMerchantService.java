package prc.client.tenant.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import prc.client.service.security.SecurityUtils;
import prc.client.tenant.dto.MerchantUpdateDto;
import prc.service.common.constant.Constants;
import prc.service.common.exception.BizException;
import prc.service.common.page.PageReq;
import prc.service.common.page.PageRes;
import prc.service.common.utils.BeanUtil;
import prc.service.config.RedisCache;
import prc.service.dao.ISUserDao;
import prc.service.dao.ITenantMerchantDao;
import prc.service.dao.IUMerchantOrderDao;
import prc.service.model.entity.ISUser;
import prc.service.model.entity.ITenantMerchant;
import prc.service.model.entity.IUMerchantOrder;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class TenantMerchantService {
    @Autowired
    private ITenantMerchantDao iTenantMerchantDao;

    @Autowired
    private BCryptPasswordEncoder cryptPasswordEncoder;

    @Autowired
    private ISUserDao isUserDao;

    @Autowired
    private RedisCache redisCache;

    public PageRes<ITenantMerchant> page(PageReq<ITenantMerchant> pageReq) {
        pageReq.getCommon().setTenantId(SecurityUtils.getLoginUser().getTenantId());
        Page<ITenantMerchant> pageRes = iTenantMerchantDao.getBaseMapper().selectPage(pageReq.createPage(), pageReq.createMapper());
        return new PageRes<ITenantMerchant>().trans(pageRes);
    }

    @Transactional
    public boolean save(MerchantUpdateDto merchantUpdateDto) {
        try {
            ITenantMerchant iTenantMerchant = new ITenantMerchant();

            if (Objects.isNull(merchantUpdateDto.getUserId())) {
                ISUser isUser = new ISUser();
                isUser.setAuthority(Lists.newArrayList());
                isUser.setPassword(cryptPasswordEncoder.encode(merchantUpdateDto.getPassword()));
                isUser.setRoleId(4);
                isUser.setStatus(merchantUpdateDto.getStatus());
                isUser.setUsername(merchantUpdateDto.getUsername());
                isUserDao.save(isUser);
                merchantUpdateDto.setUserId(isUser.getId());
                iTenantMerchant.setTenantId(SecurityUtils.getLoginUser().getTenantId());
                iTenantMerchant.setUserId(merchantUpdateDto.getUserId());
                iTenantMerchant.setSecret(IdUtil.fastSimpleUUID());
                iTenantMerchant.setName(merchantUpdateDto.getName());
            }

            if (!StringUtils.isEmpty(merchantUpdateDto.getPassword())) {
                ISUser isUser = new ISUser();
                isUser.setId(merchantUpdateDto.getUserId());
                isUser.setPassword(cryptPasswordEncoder.encode(merchantUpdateDto.getPassword()));
                isUserDao.saveOrUpdate(isUser);
            }
            iTenantMerchant.setId(merchantUpdateDto.getId());
            iTenantMerchant.setStatus(merchantUpdateDto.getStatus());
            iTenantMerchant.setWhiteIp(merchantUpdateDto.getWhiteIp());
            iTenantMerchantDao.saveOrUpdate(iTenantMerchant);
            redisCache.deleteObject(String.format(Constants.MERCHANT, iTenantMerchant.getId()));

            return true;
        } catch (Exception e) {
            throw new BizException("username is exist");
        }
    }
}
