package prc.api.merchant.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prc.api.service.service.ApiAliService;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;

@RestController
@RequestMapping("api/ali")
@Slf4j
public class AliCodeController {
    @Autowired
    private ApiAliService apiAliService;

    @PostMapping("/code/check")
    public RetResult codeCheck(String slider, String xd5) {
        return RetResponse.makeOKRsp(apiAliService.checkCode(slider, xd5));
    }

    @GetMapping("/code/init")
    public RetResult codeInit() {
        return RetResponse.makeOKRsp(apiAliService.initCode());
    }
}
