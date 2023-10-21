package prc.client.management.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prc.service.common.constant.Constants;
import prc.service.common.page.PageReq;
import prc.service.common.page.PageRes;
import prc.service.config.RedisCache;
import prc.service.dao.ISPayTypeDao;
import prc.service.model.entity.ISPayType;

@Service
public class ManagerPayTypeService {
    @Autowired
    private ISPayTypeDao isPayTypeDao;
    @Autowired
    private RedisCache redisCache;

    public PageRes<ISPayType> page(PageReq<ISPayType> pageReq) {
        Page<ISPayType> pageRes = isPayTypeDao.getBaseMapper().selectPage(pageReq.createPage(), pageReq.createMapper());
        return new PageRes<ISPayType>().trans(pageRes);
    }

    public boolean saveOrUpdate(ISPayType isPayType) {
        redisCache.deleteObject(String.format(Constants.PAY_TYPE, isPayType.getPayKey()));
        return isPayTypeDao.saveOrUpdate(isPayType);
    }

}
