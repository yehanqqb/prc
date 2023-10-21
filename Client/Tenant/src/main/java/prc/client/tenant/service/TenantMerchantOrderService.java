package prc.client.tenant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prc.client.service.model.dto.MerchantOrderDto;
import prc.client.service.security.SecurityUtils;
import prc.service.common.page.PageReq;
import prc.service.common.page.PageRes;
import prc.service.common.utils.BeanUtil;
import prc.service.dao.ISAisleDao;
import prc.service.dao.IUMerchantOrderDao;
import prc.service.model.entity.ISAisle;
import prc.service.model.entity.IUMerchantOrder;

import java.util.Map;

@Service
public class TenantMerchantOrderService {
    @Autowired
    private IUMerchantOrderDao iuMerchantOrderDao;

    public PageRes<IUMerchantOrder> page(PageReq<IUMerchantOrder> pageReq) {
        pageReq.getCommon().setTenantId(SecurityUtils.getLoginUser().getTenantId());
        Page<IUMerchantOrder> pageRes = iuMerchantOrderDao.getBaseMapper().selectPage(pageReq.createPage(), pageReq.createMapper());
        return new PageRes<IUMerchantOrder>().trans(pageRes);
    }


    public Map<String, Object> getTitle(PageReq<IUMerchantOrder> pageReq) {
        pageReq.getCommon().setTenantId(SecurityUtils.getLoginUser().getTenantId());
        return iuMerchantOrderDao.getMap(pageReq.createTitle(pageReq.createMapper()));
    }
}
