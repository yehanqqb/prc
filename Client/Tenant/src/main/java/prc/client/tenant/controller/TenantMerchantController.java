package prc.client.tenant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prc.client.tenant.dto.MerchantUpdateDto;
import prc.client.tenant.service.TenantMerchantOrderService;
import prc.client.tenant.service.TenantMerchantService;
import prc.service.common.page.PageReq;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;
import prc.service.model.entity.ISUser;
import prc.service.model.entity.ITenantMerchant;
import prc.service.model.entity.IUMerchantOrder;

import javax.validation.Valid;

@RestController
@RequestMapping("/tenant/merchant")
public class TenantMerchantController {
    @Autowired
    private TenantMerchantService tenantMerchantService;

    @PostMapping("/page")
    @PreAuthorize("@pk.hasPk('tenant')")
    public RetResult getPageList(@RequestBody PageReq<ITenantMerchant> page) {
        return RetResponse.makeOKRsp(tenantMerchantService.page(page));
    }

    @PostMapping("/save")
    @PreAuthorize("@pk.hasPk('tenant')")
    public RetResult save(@Valid @RequestBody MerchantUpdateDto merchantUpdateDto) {
        return RetResponse.makeOKRsp(tenantMerchantService.save(merchantUpdateDto));
    }
}
