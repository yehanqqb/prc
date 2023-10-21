package prc.service.channel.payment.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prc.service.channel.payment.ChannelPaymentBefore;
import prc.service.channel.payment.dto.KakaBalanceDto;
import prc.service.common.constant.Constants;
import prc.service.config.RedisCache;
import prc.service.dao.IMountLogDao;
import prc.service.dao.ISDictDao;
import prc.service.model.entity.IMountLog;
import prc.service.model.entity.IUPayment;
import prc.service.model.enumeration.Operator;
import prc.service.model.vo.ChannelMonitoringVo;
import prc.service.model.vo.ChannelPaymentVo;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service("KakaSpPayment")
public class KakaSpPayment extends ChannelPaymentBefore {
    @Autowired
    private ISDictDao isDictDao;
    @Autowired
    private IMountLogDao iMountLogDao;
    @Autowired
    private RedisCache redisCache;

    @Override
    public ChannelPaymentVo getChannelPayUrl(IUPayment iuPayment) {
        ChannelPaymentVo vo = new ChannelPaymentVo();
        if (Objects.nonNull(redisCache.getCacheObject(String.format(Constants.PAYMENT_WAIT_BALANCE, iuPayment.getProductNo())))) {
            vo.setStatus(false);
            vo.setRemark("product to one?");
            return vo;
        }
        redisCache.setCacheObject(String.format(Constants.PAYMENT_WAIT_BALANCE, iuPayment.getProductNo()), "ING", 6, TimeUnit.MINUTES);
        String proxyIp = null;
        try {
            vo.setPaymentNo(prc.service.common.utils.IdUtil.getId("ZF", "", 10));
            KakaBalanceDto balanceVo = getBalance(iuPayment.getProductNo(), iuPayment.getPaymentId(), iuPayment.getOperator());
            if (balanceVo.getCode() == 0) {
                vo.setStatus(true);
                vo.setPayUrl("alipays://platformapi/startapp?appId=2021001107610820&page=pages/top-up/home/index?mobile=" + iuPayment.getProductNo());
                vo.setQuery(JSON.parseObject(JSON.toJSONString(balanceVo)));
                vo.setProxyIp(proxyIp);
                return vo;
            } else {
                vo.setStatus(false);
                redisCache.deleteObject(String.format(Constants.PAYMENT_WAIT_BALANCE, iuPayment.getProductNo()));
                return vo;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("{}-产码失败", iuPayment.getId());
            vo.setStatus(false);
            vo.setRemark("产码失败");
            redisCache.deleteObject(String.format(Constants.PAYMENT_WAIT_BALANCE, iuPayment.getProductNo()));
            return vo;
        }
    }

    @Override
    public ChannelMonitoringVo monitoringChannel(IUPayment iuPayment) {
        ChannelMonitoringVo vo = new ChannelMonitoringVo();
        try {
            KakaBalanceDto before = JSON.parseObject(JSON.toJSONString(iuPayment.getQueryJson()), KakaBalanceDto.class);
            BigDecimal payFee = before.getTotalBalance().add(iuPayment.getMoney().subtract(new BigDecimal("3")));
            KakaBalanceDto balanceVo = getBalance(iuPayment.getProductNo(), iuPayment.getPaymentId(), iuPayment.getOperator());
            if (balanceVo.getTotalBalance().subtract(payFee).compareTo(BigDecimal.ZERO) >= 0) {
                vo.setFinishStatus(true);
                vo.setPayStatus(true);
                redisCache.deleteObject(String.format(Constants.PAYMENT_WAIT_BALANCE, iuPayment.getProductNo()));
            }
            return vo;
        } catch (Exception e) {
            e.printStackTrace();
            return vo;
        }
    }

    @Override
    public String refreshUrl(IUPayment iuPayment) {
        return null;
    }

    @Override
    public JSONObject getBefore(IUPayment iuPayment) {
        return null;
    }


    public KakaBalanceDto getBalance(String phone, String paymentId, Operator operator) {
        KakaBalanceDto dto = new KakaBalanceDto();
        JSONObject toCreate = JSONObject.parseObject(isDictDao.getByKey("kaka"));
        JSONObject reqBody = new JSONObject();
        reqBody.put("user_id", toCreate.getString("id"));
        reqBody.put("order_sn", IdUtil.randomUUID());
        reqBody.put("channel", operator.equals(Operator.TELECOM) ? 33 : 25);
        reqBody.put("payment", "WXPAY");
        reqBody.put("amount", 5000);
        reqBody.put("phone", phone);
        reqBody.put("prov", "431");
        reqBody.put("ip", "1.1.1.1");
        reqBody.put("ua", "iOS");
        reqBody.put("method", "");
        IMountLog reqLog = new IMountLog();
        reqLog.setReq(reqBody);
        reqLog.setProductNo(phone);
        reqLog.setOrderId(paymentId);
        try {
            JSONObject resBody = JSON.parseObject(HttpRequest.post(toCreate.getString("create_url")).
                    body(JSON.toJSONString(reqBody)).timeout(20000).execute().body());
            reqLog.setRes(resBody);
            CompletableFuture.runAsync(() -> iMountLogDao.save(reqLog));
            if (resBody.getInteger("code") == 10000) {
                dto.setTotalBalance(resBody.getJSONObject("data").getBigDecimal("curFee"));
                dto.setCode(0);
                return dto;
            }
            dto.setCode(-1);
            return dto;
        } catch (Exception e) {
            log.info("余额获取失败-{}", e.getMessage());
            e.printStackTrace();
            dto.setCode(-1);
            return dto;
        }
    }
}
