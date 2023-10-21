package prc.client.management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prc.client.management.service.ManagerAuthService;
import prc.service.common.page.PageReq;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;
import prc.service.model.entity.ISAuthority;

@RestController
@RequestMapping("/manager/auth")
public class ManagerAuthController {
    @Autowired
    private ManagerAuthService managerAuthService;

    @PostMapping("/page")
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult page(@RequestBody PageReq<ISAuthority> page) {
        return RetResponse.makeOKRsp(managerAuthService.page(page));
    }

    @PostMapping
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult add(@RequestBody ISAuthority isAuthority) {
        return RetResponse.makeOKRsp(managerAuthService.saveOrUpdate(isAuthority));
    }
}
