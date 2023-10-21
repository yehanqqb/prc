package prc.client.management.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import prc.client.service.model.dto.DateDto;
import prc.client.service.service.HomeService;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;
import prc.service.config.RedisCache;


@RestController
@RequestMapping("/manager/home")
public class ManagerHomeController {
    @Autowired
    private HomeService homeService;

    @Autowired
    private RedisCache redisCache;

    @GetMapping("/repertory/{slow}")
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult getPageList(@PathVariable boolean slow) {
        return RetResponse.makeOKRsp(homeService.managerFindAllRepertory(slow));
    }

    @PostMapping("/money")
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult getMoney(@RequestBody DateDto dateDto) {
        return RetResponse.makeOKRsp(homeService.finTenantMoney(dateDto.getStart(), dateDto.getEnd()));
    }

    @PostMapping("/stage")
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult stage(@RequestBody DateDto dateDto) {
        return RetResponse.makeOKRsp(homeService.findStageAll(dateDto.getStart()));
    }

    @GetMapping("/repertory/{tenantId}/{slow}")
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult getPageListTenantId(@PathVariable Integer tenantId, @PathVariable boolean slow) {
        return RetResponse.makeOKRsp(homeService.findRotationByTenantId(tenantId, slow));
    }

    @PostMapping("/money/{tenantId}")
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult getMoneyTenantId(@PathVariable Integer tenantId, @RequestBody DateDto statistcsDto) {
        return RetResponse.makeOKRsp(homeService.findAllMoneyByTimeTenant(tenantId, statistcsDto.getStart()));
    }

    @PostMapping("/stage/{tenantId}")
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult stageTenantId(@PathVariable Integer tenantId, @RequestBody DateDto statistcsDto) {
        return RetResponse.makeOKRsp(homeService.findStageAll(statistcsDto.getStart(),tenantId));
    }
}
