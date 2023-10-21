package prc.client.management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import prc.client.service.model.dto.IdsDto;
import prc.client.service.model.dto.PaymentOrderDto;
import prc.client.service.service.PaymentOrderService;
import prc.service.common.page.PageReq;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;

@RestController
@RequestMapping("/manager/payment-order")
public class ManagerPaymentOrderController {
    @Autowired
    private PaymentOrderService managerPaymentOrderService;

    @PostMapping("/page")
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult page(@RequestBody PageReq<PaymentOrderDto> pageReq) {
        return RetResponse.makeOKRsp(managerPaymentOrderService.page(pageReq));
    }

    @PostMapping("/title")
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult title(@RequestBody PageReq<PaymentOrderDto> pageReq) {
        return RetResponse.makeOKRsp(managerPaymentOrderService.getTitle(pageReq));
    }

    @PostMapping("/reissue")
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult reissue(@RequestBody IdsDto idsDto) {
        return RetResponse.makeOKRsp(managerPaymentOrderService.reissue(idsDto.getIds()));
    }

    @PostMapping("/look/{id}")
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult look(@PathVariable("id") Integer id) {
        return RetResponse.makeOKRsp(managerPaymentOrderService.look(id));
    }
}
