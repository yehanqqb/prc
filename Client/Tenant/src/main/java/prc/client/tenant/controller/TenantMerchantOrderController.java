package prc.client.tenant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prc.client.service.model.dto.IdsDto;
import prc.client.service.service.MerchantOrderService;
import prc.client.tenant.service.TenantMerchantOrderService;
import prc.service.common.page.PageReq;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;

import prc.service.model.entity.IUMerchantOrder;

@RestController
@RequestMapping("/tenant/merchant-order")
public class TenantMerchantOrderController {
    @Autowired
    private TenantMerchantOrderService merchantOrderService;
    @Autowired
    private MerchantOrderService managerMerchantOrderService;

    @PostMapping("/page")
    @PreAuthorize("@pk.hasPk('tenant')")
    public RetResult getPageList(@RequestBody PageReq<IUMerchantOrder> page) {
        return RetResponse.makeOKRsp(merchantOrderService.page(page));
    }

    @PostMapping("/title")
    @PreAuthorize("@pk.hasPk('tenant')")
    public RetResult title(@RequestBody PageReq<IUMerchantOrder> page) {
        return RetResponse.makeOKRsp(merchantOrderService.getTitle(page));
    }

    @PostMapping("/reissue")
    @PreAuthorize("@pk.hasPk('tenant')")
    public RetResult reissue(@RequestBody IdsDto idsDto) {
        return RetResponse.makeOKRsp(managerMerchantOrderService.reissue(idsDto.getIds()));
    }
}
