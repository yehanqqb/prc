package prc.client.tenant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prc.client.service.security.SecurityUtils;
import prc.client.tenant.dto.TenantAisleAddDto;
import prc.service.common.constant.Constants;
import prc.service.common.exception.BizException;
import prc.service.common.page.PageRes;
import prc.service.config.RedisCache;
import prc.service.dao.ISAisleDao;
import prc.service.dao.ITenantAisleDao;
import prc.service.dao.ITenantAisleSupplierDao;
import prc.service.model.dto.TenantAisleDto;
import prc.service.model.entity.ISAisle;
import prc.service.model.entity.ITenantAisle;
import prc.service.model.entity.ITenantAisleSupplier;

import java.util.Set;

@Service
public class TenantAisleService {
    @Autowired
    private ITenantAisleDao iTenantAisleDao;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private ITenantAisleSupplierDao iTenantAisleSupplierDao;
    @Autowired
    private ISAisleDao isAisleDao;

    public PageRes<TenantAisleDto> page() {
        Set<TenantAisleDto> dtoList = iTenantAisleDao.findAisleByTenantId(SecurityUtils.getLoginUser().getTenantId(), false);
        Page<TenantAisleDto> object = new Page<>();
        object.setSize(dtoList.size());
        object.setRecords(Lists.newArrayList(dtoList));
        return new PageRes<TenantAisleDto>().trans(object);
    }

    public Boolean saveOrUpdate(ITenantAisle tenantAisle) {
        tenantAisle.setTenantId(SecurityUtils.getLoginUser().getTenantId());
        iTenantAisleDao.saveOrUpdate(tenantAisle);
        redisCache.deleteObject(String.format(Constants.TENANT_AISLE, SecurityUtils.getLoginUser().getTenantId()));
        return true;
    }

    public Boolean saveSupplier(TenantAisleAddDto tenantAisleAddDto) {
        if (iTenantAisleSupplierDao.count(new LambdaQueryWrapper<ITenantAisleSupplier>()
                .eq(ITenantAisleSupplier::getSupplierId, tenantAisleAddDto.getSupplierId())
                .eq(ITenantAisleSupplier::getSlow, false)
        ) > 0) {
            throw new BizException("一个供货商只能绑定一个快充通道");
        }
        try {

            ISAisle aisle = isAisleDao.findById(tenantAisleAddDto.getAisleId());

            ITenantAisleSupplier iTenantAisleSupplier = new ITenantAisleSupplier();
            iTenantAisleSupplier.setAisleId(tenantAisleAddDto.getAisleId());
            iTenantAisleSupplier.setTenantId(SecurityUtils.getLoginUser().getTenantId());
            iTenantAisleSupplier.setRadio(tenantAisleAddDto.getRadio());
            iTenantAisleSupplier.setSupplierId(tenantAisleAddDto.getSupplierId());
            iTenantAisleSupplier.setId(tenantAisleAddDto.getId());
            iTenantAisleSupplier.setSlow(aisle.getSlow());
            iTenantAisleSupplierDao.saveOrUpdate(iTenantAisleSupplier);
            redisCache.deleteObject(String.format(Constants.TENANT_AISLE, SecurityUtils.getLoginUser().getTenantId()));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
