package prc.api.merchant.controller;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import prc.api.service.dto.PayMerchantDto;
import prc.api.service.service.ApiMerchantOrderService;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;

import javax.validation.Valid;

@RestController
@RequestMapping("api/merchant")
@Slf4j
public class ApiMerchantController {
    @Autowired
    private ApiMerchantOrderService apiMerchantOrderService;

    @PostMapping("/trade/order")
    public RetResult order(@Valid @RequestBody PayMerchantDto payMerchantDto) {
        return apiMerchantOrderService.createTemporaryOrder(payMerchantDto);
    }

    @PostMapping("/trade/pay/{tradeId}")
    public RetResult pay(@PathVariable String tradeId, String userKey) {
        return RetResponse.makeOKRsp(apiMerchantOrderService.tradePay(tradeId, userKey));
    }

    @PostMapping("/trade/before/{tradeId}")
    public RetResult before(@PathVariable String tradeId, String userKey) {
        return RetResponse.makeOKRsp(apiMerchantOrderService.before(tradeId, userKey));
    }

    @PostMapping("/trade/after/{tradeId}")
    public RetResult after(@PathVariable String tradeId, String userKey) {
        return RetResponse.makeOKRsp(apiMerchantOrderService.after(tradeId, userKey));
    }

    @PostMapping("/query/{orderId}")
    public RetResult query(@PathVariable String orderId) {
        return RetResponse.makeOKRsp(apiMerchantOrderService.queryStatusByOrderId(orderId));
    }

    @PostMapping("/notify")
    public RetResult notify(@RequestBody JSONObject jsonObject) {
        log.info(jsonObject.toString());
        return RetResponse.makeOKRsp();
    }

    @GetMapping("/pay/type/{tenantId}")
    public RetResult notify(@PathVariable("tenantId") Integer tenantId) {
        return RetResponse.makeOKRsp(apiMerchantOrderService.queryPayTypeByTenantId(tenantId));
    }
}
