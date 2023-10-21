package prc.client.management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import prc.client.service.model.dto.PaymentOrderDto;
import prc.client.service.service.MerchantOrderService;
import prc.client.service.model.dto.IdsDto;
import prc.client.service.model.dto.MerchantOrderDto;
import prc.service.common.page.PageReq;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;

@RestController
@RequestMapping("/manager/merchant-order")
public class ManagerMerchantOrderController {
    @Autowired
    private MerchantOrderService managerMerchantOrderService;

    @PostMapping("/page")
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult page(@RequestBody PageReq<MerchantOrderDto> pageReq) {
        return RetResponse.makeOKRsp(managerMerchantOrderService.page(pageReq));
    }

    @PostMapping("/title")
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult title(@RequestBody PageReq<MerchantOrderDto> pageReq) {
        return RetResponse.makeOKRsp(managerMerchantOrderService.getTitle(pageReq));
    }

    @PostMapping("/reissue")
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult reissue(@RequestBody IdsDto idsDto) {
        return RetResponse.makeOKRsp(managerMerchantOrderService.reissue(idsDto.getIds()));
    }
}
