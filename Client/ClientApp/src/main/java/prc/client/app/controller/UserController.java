package prc.client.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prc.client.service.service.UserService;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;

@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService userService;

    @PreAuthorize("@pk.hasPk('public')")
    @GetMapping("/user")
    public RetResult getInfo() {
        return RetResponse.makeOKRsp(userService.getUserInfo());
    }
}
