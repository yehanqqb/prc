package prc.client.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import prc.client.service.model.vo.LoginVo;
import prc.client.service.service.LoginService;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;

@RequestMapping("/login")
@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;

    @GetMapping("/kapt/{clientId}")
    public RetResult getKapt(@PathVariable String clientId) {
        return RetResponse.makeOKRsp(loginService.getKapt(clientId));
    }

    @PostMapping("/login")
    public RetResult login(@RequestBody LoginVo loginVo) {
        return RetResponse.makeOKRsp(loginService.login(loginVo));
    }

    @PostMapping("/bind")
    public RetResult bind(Integer code, String token) {
        return RetResponse.makeOKRsp(loginService.bind(code, token));
    }
}
