package prc.client.tenant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import prc.client.service.model.dto.IdsDto;
import prc.client.service.service.PaymentOrderService;
import prc.client.tenant.service.TenantPaymentService;
import prc.service.common.page.PageReq;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;
import prc.service.model.entity.IUMerchantOrder;
import prc.service.model.entity.IUPayment;

@RestController
@RequestMapping("/tenant/payment")
public class TenantPaymentController {
    @Autowired
    private TenantPaymentService tenantPaymentService;

    @Autowired
    private PaymentOrderService paymentOrderService;

    @PostMapping("/page")
    @PreAuthorize("@pk.hasPk('tenant')")
    public RetResult getPageList(@RequestBody PageReq<IUPayment> page) {
        return RetResponse.makeOKRsp(tenantPaymentService.page(page));
    }

    @PostMapping("/title")
    @PreAuthorize("@pk.hasPk('tenant')")
    public RetResult title(@RequestBody PageReq<IUPayment> page) {
        return RetResponse.makeOKRsp(tenantPaymentService.getTitle(page));
    }

    @PostMapping("/reissue")
    @PreAuthorize("@pk.hasPk('tenant')")
    public RetResult reissue(@RequestBody IdsDto idsDto) {
        return RetResponse.makeOKRsp(paymentOrderService.reissue(idsDto.getIds()));
    }

    @PostMapping("/look/{id}")
    @PreAuthorize("@pk.hasPk('tenant')")
    public RetResult look(@PathVariable("id") Integer id) {
        return RetResponse.makeOKRsp(paymentOrderService.look(id));
    }
}
