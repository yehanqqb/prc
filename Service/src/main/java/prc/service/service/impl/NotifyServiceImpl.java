package prc.service.service.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import prc.service.common.constant.Constants;
import prc.service.common.utils.DateUtil;
import prc.service.common.utils.SignUtil;
import prc.service.config.RedisCache;
import prc.service.dao.*;
import prc.service.model.dto.MerchantNotifyDto;
import prc.service.model.dto.SupplierOrderNotifyDto;
import prc.service.model.entity.*;
import prc.service.model.enumeration.PayStatus;
import prc.service.service.NotifyService;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class NotifyServiceImpl implements NotifyService {
    @Autowired
    private IUSupplierOrderDao iuSupplierOrderDao;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private IUOrderErrorNotifyDao iuOrderErrorNotifyDao;
    @Autowired
    private IUSupplierOrderLogDao iuSupplierOrderLogDao;
    @Autowired
    private ThreadPoolTaskExecutor systemExecutor;
    @Autowired
    private IUMerchantOrderDao iuMerchantOrderDao;
    @Autowired
    private IUPaymentDao iuPaymentDao;

    @Override
    @Async("systemExecutor")
    @Transactional
    public CompletableFuture<Boolean> notifyMerchantSuccess(IUMerchantOrder iuMerchantOrder, ITenantMerchant iTenantMerchant) {
        IUMerchantOrder nowOrder = iuMerchantOrderDao.getById(iuMerchantOrder.getId());
        if (Objects.isNull(nowOrder)) {
            return CompletableFuture.completedFuture(true);
        }
        if (nowOrder.getNotify().equals(Boolean.TRUE)) {
            return CompletableFuture.completedFuture(true);
        }
        MerchantNotifyDto supplierOrderNotifyDto = new MerchantNotifyDto();
        supplierOrderNotifyDto.setMoney(iuMerchantOrder.getMoney().intValue());
        supplierOrderNotifyDto.setOrderId(iuMerchantOrder.getOrderId());
        supplierOrderNotifyDto.setStatus(0);
        supplierOrderNotifyDto.setRemark(iuMerchantOrder.getRemark());
        supplierOrderNotifyDto.setSign(SignUtil.getSign(supplierOrderNotifyDto, iTenantMerchant.getSecret(), Lists.newArrayList("sign")));

        try {
            SerializerFeature[] feature = {SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullNumberAsZero};

            String ret = HttpRequest.post(iuMerchantOrder.getNotifyUrl())
                    .body(JSON.toJSONString(supplierOrderNotifyDto, feature))
                    .timeout(20000)
                    .execute().body();
            log.info("通知地址:" + iuMerchantOrder.getNotifyUrl() + "-通知参数：" + JSON.toJSONString(supplierOrderNotifyDto) + "-返回值：" + ret);
            JSONObject rest = new JSONObject();
            rest.put("res", ret);
            iuMerchantOrder.setNotifyJson(rest);
            iuMerchantOrder.setNotify(true);
            iuMerchantOrderDao.saveOrUpdate(iuMerchantOrder);

            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            e.printStackTrace();
            IUOrderErrorNotify iuOrderErrorNotify = new IUOrderErrorNotify();
            iuOrderErrorNotify.setEnd(false);
            iuOrderErrorNotify.setSupplier(false);
            iuOrderErrorNotify.setOrderId(iuMerchantOrder.getOrderId());
            JSONObject res = new JSONObject();
            res.put("message", e.getMessage());
            iuOrderErrorNotify.setRes(res);
            iuOrderErrorNotifyDao.saveOrUpdate(iuOrderErrorNotify);

            return CompletableFuture.completedFuture(false);
        }
    }

    @Override
    @Async("systemExecutor")
    @Transactional
    public CompletableFuture<Boolean> notifySupplierSuccess(IUSupplierOrder supplierOrder, ITenantSupplier iTenantSupplier, String paymentNo) {
        IUSupplierOrder nowOrder = iuSupplierOrderDao.getById(supplierOrder.getId());
        if (nowOrder.getNotify().equals(Boolean.TRUE)) {
            return CompletableFuture.completedFuture(true);
        }

        SupplierOrderNotifyDto supplierOrderNotifyDto = new SupplierOrderNotifyDto();
        supplierOrderNotifyDto.setMoney(supplierOrder.getMoney().intValue());
        supplierOrderNotifyDto.setOrderId(supplierOrder.getOrderId());
        supplierOrderNotifyDto.setProductNo(supplierOrder.getProductNo());
        supplierOrderNotifyDto.setStatus(0);
        supplierOrderNotifyDto.setRemark(supplierOrder.getRemark());
        supplierOrderNotifyDto.setPaymentNo(paymentNo);
        supplierOrderNotifyDto.setFinishTime(DateUtil.parseDateToStr(DateUtil.YYYY_MM_DD_HH_MM_SS, supplierOrder.getFinishDate()));
        supplierOrderNotifyDto.setSign(SignUtil.getSign(supplierOrderNotifyDto, iTenantSupplier.getSecret(), Lists.newArrayList("sign")));

        try {
            SerializerFeature[] feature = {SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullNumberAsZero};

            String ret = HttpRequest.post(supplierOrder.getNotifyUrl())
                    .body(JSON.toJSONString(supplierOrderNotifyDto, feature))
                    .timeout(20000)
                    .execute().body();

            CompletableFuture.runAsync(() -> {
                IUSupplierOrderLog iuSupplierOrderLog = new IUSupplierOrderLog();
                iuSupplierOrderLog.setOrderId(supplierOrder.getOrderId());
                iuSupplierOrderLog.setProductNo(supplierOrder.getProductNo());
                iuSupplierOrderLog.setIdentityId(supplierOrder.getSupplierId());
                iuSupplierOrderLog.setTenantId(supplierOrder.getTenantId());
                iuSupplierOrderLog.setReq(JSON.parseObject(JSON.toJSONString(supplierOrderNotifyDto)));
                JSONObject rest = new JSONObject();
                rest.put("res", ret);
                iuSupplierOrderLog.setRes(rest);
                iuSupplierOrderLog.setType(IUSupplierOrderLog.TYPE.RES);
                iuSupplierOrderLogDao.save(iuSupplierOrderLog);
            }, systemExecutor);
            JSONObject retk = new JSONObject();
            retk.put("res", ret);
            supplierOrder.setNotifyJson(retk);
            supplierOrder.setNotify(true);
            iuSupplierOrderDao.saveOrUpdate(supplierOrder);

            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            e.printStackTrace();
            IUOrderErrorNotify iuOrderErrorNotify = new IUOrderErrorNotify();
            iuOrderErrorNotify.setEnd(false);
            iuOrderErrorNotify.setSupplier(true);
            iuOrderErrorNotify.setOrderId(supplierOrder.getOrderId());
            JSONObject res = new JSONObject();
            res.put("message", e.getMessage());
            iuOrderErrorNotify.setRes(res);
            iuOrderErrorNotifyDao.saveOrUpdate(iuOrderErrorNotify);
            return CompletableFuture.completedFuture(false);
        }


    }

    @Override
    @Async("systemExecutor")
    @Transactional
    public CompletableFuture<Boolean> notifySupplierError(IUSupplierOrder iuSupplierOrder, ITenantSupplier iTenantSupplier) {
        String key;
        if (Boolean.TRUE.equals(iuSupplierOrder.getSlow())) {
            key = String.format(Constants.SLOW_PAYMENT_TENANT_SUPPLIER_OPERATOR_MONEY_PROVINCE, iuSupplierOrder.getTenantId(), iuSupplierOrder.getSupplierId(), iuSupplierOrder.getOperator().getKey(), iuSupplierOrder.getMoney().intValue(), iuSupplierOrder.getProvinceId());
        } else {
            key = String.format(Constants.FAST_PAYMENT_TENANT_SUPPLIER_OPERATOR_MONEY_PROVINCE, iuSupplierOrder.getTenantId(), iuSupplierOrder.getSupplierId(), iuSupplierOrder.getOperator().getKey(), iuSupplierOrder.getMoney().intValue(), iuSupplierOrder.getProvinceId());
        }
        List<IUPayment> payments = redisCache.getCacheList(key);
        payments.forEach(item -> {
            if (item.getSupplierOrderId().equals(iuSupplierOrder.getOrderId())) {
                redisCache.removeList(key, item);
            }
        });

        IUSupplierOrder nowOrder = iuSupplierOrderDao.getById(iuSupplierOrder.getId());
        if (nowOrder.getNotify().equals(Boolean.TRUE)) {
            return CompletableFuture.completedFuture(true);
        }
        SupplierOrderNotifyDto supplierOrderNotifyDto = new SupplierOrderNotifyDto();
        supplierOrderNotifyDto.setMoney(iuSupplierOrder.getMoney().intValue());
        supplierOrderNotifyDto.setOrderId(iuSupplierOrder.getOrderId());
        supplierOrderNotifyDto.setProductNo(iuSupplierOrder.getProductNo());
        supplierOrderNotifyDto.setStatus(-1);
        supplierOrderNotifyDto.setRemark(iuSupplierOrder.getRemark());
        supplierOrderNotifyDto.setSign(SignUtil.getSign(supplierOrderNotifyDto, iTenantSupplier.getSecret(), Lists.newArrayList("sign")));

        try {
            SerializerFeature[] feature = {SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullNumberAsZero};

            String ret = HttpRequest.post(iuSupplierOrder.getNotifyUrl())
                    .body(JSON.toJSONString(supplierOrderNotifyDto, feature))
                    .timeout(20000)
                    .execute().body();

            CompletableFuture.runAsync(() -> {
                IUSupplierOrderLog iuSupplierOrderLog = new IUSupplierOrderLog();
                iuSupplierOrderLog.setOrderId(iuSupplierOrder.getOrderId());
                iuSupplierOrderLog.setProductNo(iuSupplierOrder.getProductNo());
                iuSupplierOrderLog.setIdentityId(iuSupplierOrder.getSupplierId());
                iuSupplierOrderLog.setTenantId(iuSupplierOrder.getTenantId());
                iuSupplierOrderLog.setReq(JSON.parseObject(JSON.toJSONString(supplierOrderNotifyDto)));
                JSONObject rest = new JSONObject();
                rest.put("res", ret);
                iuSupplierOrderLog.setRes(rest);
                iuSupplierOrderLog.setType(IUSupplierOrderLog.TYPE.RES);
                iuSupplierOrderLogDao.save(iuSupplierOrderLog);
            }, systemExecutor);

            if (!iuSupplierOrder.getPayStatus().equals(PayStatus.ERROR)
                    && !iuSupplierOrder.getPayStatus().equals(PayStatus.SUCCESS)
                    && !iuSupplierOrder.getPayStatus().equals(PayStatus.NOT_PAY)
            ) {
                iuSupplierOrderDao.removeById(iuSupplierOrder.getId());
                if (!StringUtils.isEmpty(iuSupplierOrder.getPaymentId())) {
                    iuPaymentDao.remove(new LambdaQueryWrapper<IUPayment>().eq(IUPayment::getPaymentId, iuSupplierOrder.getPaymentId()));
                }
            } else {
                iuSupplierOrder.setNotifyJson(JSON.parseObject(ret));
                iuSupplierOrder.setNotify(true);
                iuSupplierOrderDao.saveOrUpdate(iuSupplierOrder);
            }
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            e.printStackTrace();
            IUOrderErrorNotify iuOrderErrorNotify = new IUOrderErrorNotify();
            iuOrderErrorNotify.setEnd(false);
            iuOrderErrorNotify.setSupplier(true);
            iuOrderErrorNotify.setOrderId(iuSupplierOrder.getOrderId());
            JSONObject res = new JSONObject();
            res.put("message", e.getMessage());
            iuOrderErrorNotify.setRes(res);
            iuOrderErrorNotifyDao.saveOrUpdate(iuOrderErrorNotify);
            return CompletableFuture.completedFuture(false);
        }
    }
}
