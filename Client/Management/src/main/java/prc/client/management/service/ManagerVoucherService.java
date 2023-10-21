package prc.client.management.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prc.service.common.page.PageReq;
import prc.service.common.page.PageRes;
import prc.service.dao.ISAisleDao;
import prc.service.dao.ISVoucherDao;
import prc.service.model.entity.ISAisle;
import prc.service.model.entity.ISVoucher;

@Service
public class ManagerVoucherService {
    @Autowired
    private ISVoucherDao isVoucherDao;

    public PageRes<ISVoucher> page(PageReq<ISVoucher> pageReq) {
        Page<ISVoucher> pageRes = isVoucherDao.getBaseMapper().selectPage(pageReq.createPage(), pageReq.createMapper());
        return new PageRes<ISVoucher>().trans(pageRes);
    }


}
