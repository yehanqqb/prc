package prc.client.management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prc.client.management.service.ManagerAuthService;
import prc.client.management.service.ManagerRoleService;
import prc.service.common.page.PageReq;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;
import prc.service.model.entity.ISAuthority;
import prc.service.model.entity.ISRole;

@RestController
@RequestMapping("/manager/role")
public class ManagerRoleController {
    @Autowired
    private ManagerRoleService managerRoleService;

    @PostMapping("/page")
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult page(@RequestBody PageReq<ISRole> page) {
        return RetResponse.makeOKRsp(managerRoleService.page(page));
    }

    @PostMapping
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult add(@RequestBody ISRole isRole) {
        return RetResponse.makeOKRsp(managerRoleService.saveOrUpdate(isRole));
    }
}
