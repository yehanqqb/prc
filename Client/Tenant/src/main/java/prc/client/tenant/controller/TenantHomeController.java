package prc.client.tenant.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import prc.client.service.model.dto.DateDto;
import prc.client.service.security.SecurityUtils;
import prc.client.service.service.HomeService;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;

@RestController
@RequestMapping("/tenant/home")
public class TenantHomeController {
    @Autowired
    private HomeService homeService;

    @GetMapping("/repertory/{slow}")
    @PreAuthorize("@pk.hasPk('tenant')")
    public RetResult getPageList(@PathVariable boolean slow) {
        return RetResponse.makeOKRsp(homeService.findRotationByTenantId(SecurityUtils.getLoginUser().getTenantId(), slow));
    }

    @PostMapping("/money")
    @PreAuthorize("@pk.hasPk('tenant')")
    public RetResult getMoney(@RequestBody DateDto dateDto) {
        return RetResponse.makeOKRsp(homeService.findAllMoneyByTimeTenant(SecurityUtils.getLoginUser().getTenantId(), dateDto.getStart()));
    }

    @PostMapping("/stage")
    @PreAuthorize("@pk.hasPk('tenant')")
    public RetResult stage(@RequestBody DateDto dateDto) {
        return RetResponse.makeOKRsp(homeService.findStageAll(dateDto.getStart(), SecurityUtils.getLoginUser().getTenantId()));
    }
}
