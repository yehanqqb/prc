package prc.client.tenant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prc.client.service.security.SecurityUtils;
import prc.service.common.page.PageReq;
import prc.service.common.page.PageRes;
import prc.service.dao.IUSupplierOrderDao;
import prc.service.model.entity.IUMerchantOrder;
import prc.service.model.entity.IUSupplierOrder;

import java.util.Map;

@Service
public class TenantSupplierOrderService {
    @Autowired
    private IUSupplierOrderDao iuSupplierOrderDao;

    public PageRes<IUSupplierOrder> page(PageReq<IUSupplierOrder> pageReq) {
        pageReq.getCommon().setTenantId(SecurityUtils.getLoginUser().getTenantId());
        Page<IUSupplierOrder> pageRes = iuSupplierOrderDao.getBaseMapper().selectPage(pageReq.createPage(), pageReq.createMapper());
        return new PageRes<IUSupplierOrder>().trans(pageRes);
    }

    public Map<String, Object> getTitle(PageReq<IUSupplierOrder> pageReq) {
        pageReq.getCommon().setTenantId(SecurityUtils.getLoginUser().getTenantId());
        return iuSupplierOrderDao.getMap(pageReq.createTitle(pageReq.createMapper()));
    }
}
