package prc.client.management.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import prc.client.management.dto.GatewayDto;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;
import prc.service.config.RedisCache;


@RestController
@RequestMapping("/manager/gateway")
public class ManagerGatewayController {
    @Autowired
    private RedisCache redisCache;

    @GetMapping
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult get() {
        return RetResponse.makeOKRsp(redisCache.getCacheObject("gateway"));
    }

    @PostMapping
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult put(@RequestBody GatewayDto gatewayDto) {
        redisCache.setCacheObject("gateway", JSON.parseArray(JSON.toJSONString(gatewayDto.getRoute())));
        return RetResponse.makeOKRsp();
    }
}
