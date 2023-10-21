package prc.client.management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prc.client.service.model.dto.SupplierOrderDto;
import prc.client.service.service.SupplierOrderService;
import prc.client.service.model.dto.IdsDto;
import prc.service.common.page.PageReq;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;

@RestController
@RequestMapping("/manager/supplier-order")
public class ManagerSupplierOrderController {
    @Autowired
    private SupplierOrderService managerSupplierOrderService;

    @PostMapping("/page")
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult page(@RequestBody PageReq<SupplierOrderDto> pageReq) {
        return RetResponse.makeOKRsp(managerSupplierOrderService.page(pageReq));
    }

    @PostMapping("/title")
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult title(@RequestBody PageReq<SupplierOrderDto> pageReq) {
        return RetResponse.makeOKRsp(managerSupplierOrderService.getTitle(pageReq));
    }

    @PostMapping("/reissue")
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult reissue(@RequestBody IdsDto idsDto) {
        return RetResponse.makeOKRsp(managerSupplierOrderService.reissue(idsDto.getIds()));
    }

    @PostMapping("/batchError")
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult batchError(@RequestBody IdsDto idsDto) {
        return RetResponse.makeOKRsp(managerSupplierOrderService.batchError(idsDto.getIds()));
    }

    @PostMapping("/batchErrorIng")
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult batchErrorIng(@RequestBody IdsDto idsDto) {
        return RetResponse.makeOKRsp(managerSupplierOrderService.batchErrorIng(idsDto.getIds()));
    }
}
