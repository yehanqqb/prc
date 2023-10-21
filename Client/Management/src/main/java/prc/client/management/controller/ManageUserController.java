package prc.client.management.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import prc.client.management.dto.UpdateUserDto;
import prc.client.management.service.ManagerUserService;
import prc.service.common.page.PageReq;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;
import prc.service.model.entity.ISUser;

@RestController
@RequestMapping("/manager/user")
public class ManageUserController {
    @Autowired
    private ManagerUserService managerUserService;

    @PostMapping("/page")
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult getPageList(@RequestBody PageReq<ISUser> page) {
        return RetResponse.makeOKRsp(managerUserService.page(page));
    }


    @PutMapping("/{userId}")
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult putUser(@PathVariable Integer userId, @RequestBody UpdateUserDto sysUserDto) {
        return RetResponse.makeOKRsp(managerUserService.updateUser(userId, sysUserDto));
    }
}
