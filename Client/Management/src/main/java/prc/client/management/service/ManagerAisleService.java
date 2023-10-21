package prc.client.management.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prc.service.common.page.PageReq;
import prc.service.common.page.PageRes;
import prc.service.dao.ISAisleDao;
import prc.service.dao.ITenantDao;
import prc.service.model.entity.ISAisle;
import prc.service.model.entity.ITenant;

@Service
public class ManagerAisleService {
    @Autowired
    private ISAisleDao isAisleDao;

    public PageRes<ISAisle> page(PageReq<ISAisle> pageReq) {
        Page<ISAisle> pageRes = isAisleDao.getBaseMapper().selectPage(pageReq.createPage(), pageReq.createMapper());
        return new PageRes<ISAisle>().trans(pageRes);
    }


}
