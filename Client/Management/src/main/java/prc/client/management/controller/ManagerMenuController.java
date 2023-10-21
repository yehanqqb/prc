package prc.client.management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prc.client.management.service.ManagerAuthService;
import prc.client.management.service.ManagerMenuService;
import prc.service.common.page.PageReq;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;
import prc.service.model.entity.ISAuthority;
import prc.service.model.entity.ISMenu;

@RestController
@RequestMapping("/manager/menu")
public class ManagerMenuController {
    @Autowired
    private ManagerMenuService managerMenuService;

    @PostMapping("/page")
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult page(@RequestBody PageReq<ISMenu> page) {
        return RetResponse.makeOKRsp(managerMenuService.page(page));
    }

    @PostMapping
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult add(@RequestBody ISMenu isMenu) {
        return RetResponse.makeOKRsp(managerMenuService.saveOrUpdate(isMenu));
    }
}
