package prc.client.tenant.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import prc.client.service.security.SecurityUtils;
import prc.client.tenant.dto.MerchantUpdateDto;
import prc.client.tenant.dto.SupplierUpdateDto;
import prc.service.common.constant.Constants;
import prc.service.common.exception.BizException;
import prc.service.common.page.PageReq;
import prc.service.common.page.PageRes;
import prc.service.common.utils.BeanUtil;
import prc.service.config.RedisCache;
import prc.service.dao.ISUserDao;
import prc.service.dao.ITenantSupplierDao;
import prc.service.dao.IUMerchantOrderDao;
import prc.service.dao.IUSupplierOrderDao;
import prc.service.model.entity.*;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Set;

@Service
public class TenantSupplierService {
    @Autowired
    private ITenantSupplierDao iTenantSupplierDao;

    @Autowired
    private BCryptPasswordEncoder cryptPasswordEncoder;

    @Autowired
    private ISUserDao isUserDao;

    @Autowired
    private RedisCache redisCache;

    public PageRes<ITenantSupplier> page(PageReq<ITenantSupplier> pageReq) {
        pageReq.getCommon().setTenantId(SecurityUtils.getLoginUser().getTenantId());
        Page<ITenantSupplier> pageRes = iTenantSupplierDao.getBaseMapper().selectPage(pageReq.createPage(), pageReq.createMapper());
        return new PageRes<ITenantSupplier>().trans(pageRes);
    }

    @Transactional
    public boolean save(SupplierUpdateDto supplierUpdateDto) {
        try {
            ITenantSupplier iTenantSupplier = BeanUtil.convertToBean(supplierUpdateDto, ITenantSupplier.class);
            if (Objects.isNull(supplierUpdateDto.getUserId())) {
                ISUser isUser = new ISUser();
                isUser.setAuthority(Lists.newArrayList());
                isUser.setPassword(cryptPasswordEncoder.encode(supplierUpdateDto.getPassword()));
                isUser.setRoleId(3);
                isUser.setStatus(supplierUpdateDto.getStatus());
                isUser.setUsername(supplierUpdateDto.getUsername());
                isUserDao.save(isUser);
                iTenantSupplier.setUserId(isUser.getId());
                iTenantSupplier.setTenantId(SecurityUtils.getLoginUser().getTenantId());
                iTenantSupplier.setSecret(IdUtil.fastSimpleUUID());
                iTenantSupplier.setName(supplierUpdateDto.getName());
            }
            if (!StringUtils.isEmpty(supplierUpdateDto.getPassword())) {
                ISUser isUser = new ISUser();
                isUser.setId(supplierUpdateDto.getUserId());
                isUser.setPassword(cryptPasswordEncoder.encode(supplierUpdateDto.getPassword()));
                isUserDao.saveOrUpdate(isUser);
            }
            iTenantSupplier.setTenantId(SecurityUtils.getLoginUser().getTenantId());
            iTenantSupplierDao.saveOrUpdate(iTenantSupplier);
            redisCache.deleteObject(String.format(Constants.SUPPLIER, iTenantSupplier.getId()));
            return true;
        } catch (Exception e) {
            throw new BizException("username is exist");
        }
    }
}
