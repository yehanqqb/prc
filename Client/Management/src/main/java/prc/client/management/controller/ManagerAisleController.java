package prc.client.management.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prc.client.management.service.ManagerAisleService;
import prc.service.common.constant.Constants;
import prc.service.common.page.PageReq;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;
import prc.service.config.RedisCache;
import prc.service.dao.ISAisleDao;
import prc.service.dao.ISPayTypeDao;
import prc.service.model.entity.ISAisle;
import prc.service.model.entity.ISPayType;

import java.util.Objects;

@RestController
@RequestMapping("/manager/aisle")
public class ManagerAisleController {
    @Autowired
    private ManagerAisleService managerAisleService;

    @Autowired
    private ISAisleDao isAisleDao;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISPayTypeDao isPayTypeDao;

    @PostMapping("/page")
    @PreAuthorize("@pk.hasPk('public')")
    public RetResult page(@RequestBody PageReq<ISAisle> page) {
        return RetResponse.makeOKRsp(managerAisleService.page(page));
    }

    @PostMapping
    @PreAuthorize("@pk.hasPk('manager')")
    public RetResult save(@RequestBody ISAisle sysAisle) {
        isAisleDao.saveOrUpdate(sysAisle);
        ISPayType payType = new ISPayType();
        if (Objects.nonNull(sysAisle.getId())) {
            redisCache.getCacheObject(String.format(Constants.AISLE, sysAisle.getId()));
            ISAisle dbAisle = isAisleDao.findById(sysAisle.getId());
            payType.setStatus(false);
            isPayTypeDao.update(payType, new LambdaUpdateWrapper<ISPayType>().in(ISPayType::getPayKey, dbAisle.getPayType()));
        }
        payType.setStatus(true);
        isPayTypeDao.update(payType, new LambdaUpdateWrapper<ISPayType>().in(ISPayType::getPayKey, sysAisle.getPayType()));
        redisCache.deleteObject(String.format(Constants.AISLE, sysAisle.getId()));
        return RetResponse.makeOKRsp();
    }
}
