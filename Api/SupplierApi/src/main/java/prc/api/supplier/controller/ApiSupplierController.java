package prc.api.supplier.controller;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import prc.api.service.dto.TreadSupplierDto;
import prc.api.service.service.ApiSupplierOrderService;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;

import javax.validation.Valid;

@RestController
@RequestMapping("api/supplier")
@Slf4j
public class ApiSupplierController {
    @Autowired
    private ApiSupplierOrderService apiSupplierOrderService;

    @PostMapping("/trade/order")
    public RetResult order(@Valid @RequestBody TreadSupplierDto treadSupplierDto) {
        return apiSupplierOrderService.createTemporaryOrder(treadSupplierDto, true);
    }

    @PostMapping("/query/{orderId}")
    public RetResult query(@PathVariable String orderId) {
        return RetResponse.makeOKRsp(apiSupplierOrderService.queryStatusByOrderId(orderId));
    }


    @PostMapping("/notify")
    public RetResult notify(@RequestBody JSONObject jsonObject) {
        log.info(jsonObject.toString());
        return RetResponse.makeOKRsp();
    }
}
