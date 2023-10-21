package prc.client.tenant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prc.client.tenant.dto.MerchantUpdateDto;
import prc.client.tenant.dto.SupplierUpdateDto;
import prc.client.tenant.service.TenantPaymentService;
import prc.client.tenant.service.TenantSupplierService;
import prc.service.common.page.PageReq;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;
import prc.service.model.entity.ITenantSupplier;

import javax.validation.Valid;

@RestController
@RequestMapping("/tenant/supplier")
public class TenantSupplierController {
    @Autowired
    private TenantSupplierService tenantSupplierService;

    @PostMapping("/page")
    @PreAuthorize("@pk.hasPk('tenant')")
    public RetResult getPageList(@RequestBody PageReq<ITenantSupplier> page) {
        return RetResponse.makeOKRsp(tenantSupplierService.page(page));
    }

    @PostMapping("/save")
    @PreAuthorize("@pk.hasPk('tenant')")
    public RetResult save(@Valid @RequestBody SupplierUpdateDto supplierUpdateDto) {
        return RetResponse.makeOKRsp(tenantSupplierService.save(supplierUpdateDto));
    }
}
