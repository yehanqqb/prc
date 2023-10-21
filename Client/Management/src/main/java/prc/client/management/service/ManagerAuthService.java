package prc.client.management.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prc.service.common.page.PageReq;
import prc.service.common.page.PageRes;
import prc.service.dao.ISAuthorityDao;
import prc.service.model.entity.ISAisle;
import prc.service.model.entity.ISAuthority;

@Service
public class ManagerAuthService {
    @Autowired
    private ISAuthorityDao isAuthorityDao;

    public PageRes<ISAuthority> page(PageReq<ISAuthority> pageReq) {
        Page<ISAuthority> pageRes = isAuthorityDao.getBaseMapper().selectPage(pageReq.createPage(), pageReq.createMapper());
        return new PageRes<ISAuthority>().trans(pageRes);
    }

    public boolean saveOrUpdate(ISAuthority isAuthority) {
        return isAuthorityDao.saveOrUpdate(isAuthority);
    }
}
