package prc.client.management.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prc.service.common.page.PageReq;
import prc.service.common.page.PageRes;
import prc.service.dao.ISAisleDao;
import prc.service.dao.ISMenuDao;
import prc.service.model.entity.ISAisle;
import prc.service.model.entity.ISMenu;
import prc.service.model.entity.ISRole;

@Service
public class ManagerMenuService {
    @Autowired
    private ISMenuDao isMenuDao;

    public PageRes<ISMenu> page(PageReq<ISMenu> pageReq) {
        Page<ISMenu> pageRes = isMenuDao.getBaseMapper().selectPage(pageReq.createPage(), pageReq.createMapper());
        return new PageRes<ISMenu>().trans(pageRes);
    }

    public boolean saveOrUpdate(ISMenu isMenu) {
        return isMenuDao.saveOrUpdate(isMenu);
    }


}
