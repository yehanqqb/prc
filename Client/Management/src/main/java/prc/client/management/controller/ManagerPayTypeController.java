package prc.client.management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prc.client.management.service.ManagerAisleService;
import prc.client.management.service.ManagerPayTypeService;
import prc.service.common.page.PageReq;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;
import prc.service.dao.ISAisleDao;
import prc.service.model.entity.ISAisle;
import prc.service.model.entity.ISPayType;

@RestController
@RequestMapping("/manager/payType")
public class ManagerPayTypeController {
    @Autowired
    private ManagerPayTypeService managerPayTypeService;

    @PostMapping("/page")
    @PreAuthorize("@pk.hasPk('public')")
    public RetResult page(@RequestBody PageReq<ISPayType> page) {
        return RetResponse.makeOKRsp(managerPayTypeService.page(page));
    }

    @PostMapping
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult save(@RequestBody ISPayType isPayType) {
        return RetResponse.makeOKRsp(managerPayTypeService.saveOrUpdate(isPayType));
    }

}
