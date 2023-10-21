package prc.client.tenant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prc.client.service.security.SecurityUtils;
import prc.service.common.page.PageReq;
import prc.service.common.page.PageRes;
import prc.service.dao.ITenantMerchantDao;
import prc.service.dao.IUPaymentDao;
import prc.service.model.entity.ITenantMerchant;
import prc.service.model.entity.IUPayment;
import prc.service.model.entity.IUSupplierOrder;

import java.util.Map;

@Service
public class TenantPaymentService {
    @Autowired
    private IUPaymentDao iuPaymentDao;

    public PageRes<IUPayment> page(PageReq<IUPayment> pageReq) {
        pageReq.getCommon().setTenantId(SecurityUtils.getLoginUser().getTenantId());
        Page<IUPayment> pageRes = iuPaymentDao.getBaseMapper().selectPage(pageReq.createPage(), pageReq.createMapper());
        return new PageRes<IUPayment>().trans(pageRes);
    }

    public Map<String, Object> getTitle(PageReq<IUPayment> pageReq) {
        pageReq.getCommon().setTenantId(SecurityUtils.getLoginUser().getTenantId());
        return iuPaymentDao.getMap(pageReq.createTitle(pageReq.createMapper()));
    }

}
