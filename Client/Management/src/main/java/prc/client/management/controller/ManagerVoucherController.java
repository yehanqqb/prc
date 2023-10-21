package prc.client.management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prc.client.management.service.ManagerVoucherService;
import prc.service.common.page.PageReq;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;
import prc.service.dao.ISVoucherDao;
import prc.service.model.entity.ISVoucher;

@RestController
@RequestMapping("/manager/voucher")
public class ManagerVoucherController {
    @Autowired
    private ManagerVoucherService managerVoucherService;
    @Autowired
    private ISVoucherDao isVoucherDao;

    @PostMapping("/page")
    public RetResult page(@RequestBody PageReq<ISVoucher> page) {
        return RetResponse.makeOKRsp(managerVoucherService.page(page));
    }

    @PostMapping("/saveOrUpdate")
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult saveOrUpdate(@RequestBody ISVoucher isVoucher) {
        return RetResponse.makeOKRsp(isVoucherDao.saveOrUpdate(isVoucher));
    }

}
