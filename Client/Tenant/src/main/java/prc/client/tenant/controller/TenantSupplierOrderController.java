package prc.client.tenant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prc.client.service.model.dto.IdsDto;
import prc.client.service.service.SupplierOrderService;
import prc.client.tenant.service.TenantSupplierOrderService;
import prc.service.common.page.PageReq;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;
import prc.service.model.entity.IUPayment;
import prc.service.model.entity.IUSupplierOrder;

@RestController
@RequestMapping("/tenant/supplier-order")
public class TenantSupplierOrderController {
    @Autowired
    private TenantSupplierOrderService tenantSupplierOrderService;

    @Autowired
    private SupplierOrderService supplierOrderService;

    @PostMapping("/title")
    @PreAuthorize("@pk.hasPk('tenant')")
    public RetResult title(@RequestBody PageReq<IUSupplierOrder> page) {
        return RetResponse.makeOKRsp(tenantSupplierOrderService.getTitle(page));
    }


    @PostMapping("/page")
    @PreAuthorize("@pk.hasPk('tenant')")
    public RetResult getPageList(@RequestBody PageReq<IUSupplierOrder> page) {
        return RetResponse.makeOKRsp(tenantSupplierOrderService.page(page));
    }

    @PostMapping("/reissue")
    @PreAuthorize("@pk.hasPk('tenant')")
    public RetResult reissue(@RequestBody IdsDto idsDto) {
        return RetResponse.makeOKRsp(supplierOrderService.reissue(idsDto.getIds()));
    }

    @PostMapping("/batchError")
    @PreAuthorize("@pk.hasPk('tenant')")
    public RetResult batchError(@RequestBody IdsDto idsDto) {
        return RetResponse.makeOKRsp(supplierOrderService.batchError(idsDto.getIds()));
    }

    @PostMapping("/batchErrorIng")
    @PreAuthorize("@pk.hasPk('tenant')")
    public RetResult batchErrorIng(@RequestBody IdsDto idsDto) {
        return RetResponse.makeOKRsp(supplierOrderService.batchErrorIng(idsDto.getIds()));
    }
}
