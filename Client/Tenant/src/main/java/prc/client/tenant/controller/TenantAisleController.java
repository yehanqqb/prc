package prc.client.tenant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import prc.client.service.security.SecurityUtils;
import prc.client.tenant.dto.TenantAisleAddDto;
import prc.client.tenant.service.TenantAisleService;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;
import prc.service.dao.ITenantAisleSupplierDao;
import prc.service.dao.ITenantSupplierDao;
import prc.service.model.entity.ITenantAisle;

@RestController
@RequestMapping("/tenant/aisle")
public class TenantAisleController {
    @Autowired
    private TenantAisleService tenantAisleService;

    @Autowired
    private ITenantSupplierDao iTenantSupplierDao;
    @Autowired
    private ITenantAisleSupplierDao iTenantAisleSupplierDao;

    @PostMapping("/page")
    @PreAuthorize("@pk.hasPk('tenant')")
    public RetResult getPageList() {
        return RetResponse.makeOKRsp(tenantAisleService.page());
    }

    @PostMapping("/save")
    @PreAuthorize("@pk.hasPk('tenant')")
    public RetResult save(@RequestBody ITenantAisle tenantAisleListDto) {
        return RetResponse.makeOKRsp(tenantAisleService.saveOrUpdate(tenantAisleListDto));
    }

    @PostMapping("/save/supplier")
    @PreAuthorize("@pk.hasPk('tenant')")
    public RetResult saveSupplier(@RequestBody TenantAisleAddDto tenantAisleListDto) {
        return RetResponse.makeOKRsp(tenantAisleService.saveSupplier(tenantAisleListDto));
    }

    @PostMapping("/supplier/{aisleId}")
    @PreAuthorize("@pk.hasPk('tenant')")
    public RetResult supplierAisle(@PathVariable Integer aisleId) {
        return RetResponse.makeOKRsp(iTenantSupplierDao.getSupplierAisleByTenantIdAndAisleId(SecurityUtils.getLoginUser().getTenantId(), aisleId));
    }

    @DeleteMapping("/supplier/{id}")
    @PreAuthorize("@pk.hasPk('tenant')")
    public RetResult deletedSupplierAisle(@PathVariable Integer id) {
        return RetResponse.makeOKRsp(iTenantAisleSupplierDao.getBaseMapper().deleteById(id));
    }
}
