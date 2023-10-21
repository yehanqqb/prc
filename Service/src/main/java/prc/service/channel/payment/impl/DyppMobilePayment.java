package prc.service.channel.payment.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prc.service.channel.payment.ChannelPaymentBefore;
import prc.service.channel.payment.dto.KakaBalanceDto;
import prc.service.common.constant.Constants;
import prc.service.common.utils.DateUtil;
import prc.service.common.utils.SignUtil;
import prc.service.common.utils.TimeUtil;
import prc.service.config.RedisCache;
import prc.service.dao.IMountLogDao;
import prc.service.dao.ISDictDao;
import prc.service.model.entity.IMountLog;
import prc.service.model.entity.IUPayment;
import prc.service.model.enumeration.Operator;
import prc.service.model.vo.ChannelMonitoringVo;
import prc.service.model.vo.ChannelPaymentVo;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service("DyppMobilePayment")
public class DyppMobilePayment extends ChannelPaymentBefore {
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
        redisCache.setCacheObject(String.format(Constants.PAYMENT_WAIT_BALANCE, iuPayment.getProductNo()), "ING", 60, TimeUnit.MINUTES);
        String proxyIp = null;
        try {
            vo.setPaymentNo(prc.service.common.utils.IdUtil.getId("0095", "", 10));
            KakaBalanceDto balanceVo = getBalance(iuPayment.getProductNo(), iuPayment.getPaymentId(), iuPayment.getOperator());
            if (balanceVo.getCode() == 0) {
                vo.setStatus(true);
                if (iuPayment.getAgent().contains("Android")) {
                    vo.setPayUrl("snssdk1128://webview?url=" + URLEncoder.encode("https://lf-webcast-gr-sourcecdn.bytegecko.com/obj/byte-gurd-source-gr/wallet/h5/webcast/caijing_h5_living_expense/template/pages/home/index.html") + "&from=webview&hide_nav_bar=1&refer=web");
                } else {
                    vo.setPayUrl("snssdk1128://webcast_lynxview?url=https%3A%2F%2Flf-webcast-gr-sourcecdn.bytegecko.com%2Fobj%2Fbyte-gurd-source-gr%2Fwallet%2Flynx%2Fwebcast%2Fcaijing_lynx_phone%2Fphone_index%2Ftemplate.js&amp;trans_status_bar=1&amp;hide_nav_bar=1&amp;loader_name=forest&amp;support_exchange_theme=1&amp;allow_exchange=0&amp;from=customer_service");
                }
                vo.setQuery(JSON.parseObject(JSON.toJSONString(balanceVo)));
                vo.setProductNo(iuPayment.getProductNo());
                vo.setProxyIp(proxyIp);
                return vo;
            } else {
                vo.setStatus(false);
                redisCache.deleteObject(String.format(Constants.PAYMENT_WAIT_BALANCE, iuPayment.getProductNo()));
                return vo;
            }
            /*vo.setStatus(true);
            if (iuPayment.getAgent().contains("Android")) {
                vo.setPayUrl("snssdk1128://webview?url=" + URLEncoder.encode("https://lf-webcast-gr-sourcecdn.bytegecko.com/obj/byte-gurd-source-gr/wallet/h5/webcast/caijing_h5_living_expense/template/pages/home/index.html") + "&from=webview&hide_nav_bar=1&refer=web");
            } else {
                vo.setPayUrl("snssdk1128://webcast_lynxview?url=https%3A%2F%2Flf-webcast-gr-sourcecdn.bytegecko.com%2Fobj%2Fbyte-gurd-source-gr%2Fwallet%2Flynx%2Fwebcast%2Fcaijing_lynx_phone%2Fphone_index%2Ftemplate.js&amp;trans_status_bar=1&amp;hide_nav_bar=1&amp;loader_name=forest&amp;support_exchange_theme=1&amp;allow_exchange=0&amp;from=customer_service");
            }
            vo.setQuery(new JSONObject());
            vo.setProductNo(iuPayment.getProductNo());
            vo.setProxyIp(proxyIp);
            return vo;*/
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
            Integer exist = redisCache.getCacheObject("DY:" + iuPayment.getProductNo());
            if (Objects.isNull(exist) || exist < 5) {
                if (Objects.isNull(exist)) {
                    redisCache.setCacheObject("DY:" + iuPayment.getProductNo(), 1, 15, TimeUnit.MINUTES);
                } else {
                    redisCache.setCacheObject("DY:" + iuPayment.getProductNo(), exist + 1, 15, TimeUnit.MINUTES);
                }
                JSONObject jrt = mount(iuPayment.getProductNo(), iuPayment.getPaymentId(), iuPayment.getPayTime());
                if (jrt.getString("code").equals("1")) {
                    List<String> jrtList = Arrays.asList(jrt.getString("amt").split(","));
                    jrtList.forEach(item -> {
                        if (iuPayment.getMoney().subtract(new BigDecimal(item)).abs().compareTo(new BigDecimal(3)) < 0) {
                            vo.setFinishStatus(true);
                            vo.setPayStatus(true);
                        }
                    });
                }
            } else {
                KakaBalanceDto before = JSON.parseObject(JSON.toJSONString(iuPayment.getQueryJson()), KakaBalanceDto.class);
                BigDecimal payFee = before.getTotalBalance().add(iuPayment.getMoney().subtract(new BigDecimal("3")));
                KakaBalanceDto balanceVo = getBalance(iuPayment.getProductNo(), iuPayment.getPaymentId(), iuPayment.getOperator());
                if (balanceVo.getTotalBalance().subtract(payFee).compareTo(BigDecimal.ZERO) >= 0) {
                    vo.setFinishStatus(true);
                    vo.setPayStatus(true);
                }
            }
            return vo;
        } catch (Exception e) {
            e.printStackTrace();
            return vo;
        }
    }

    @Override
    public void monitoringSuccess(IUPayment iuPayment) {
        super.monitoringSuccess(iuPayment);
        redisCache.deleteObject(String.format(Constants.PAYMENT_WAIT_BALANCE, iuPayment.getProductNo()));
    }

    @Override
    public void monitoringError(IUPayment iuPayment) {
        super.monitoringError(iuPayment);
    }

    @Override
    public String refreshUrl(IUPayment iuPayment) {
        return null;
    }

    @Override
    public JSONObject getBefore(IUPayment iuPayment) {
        return null;
    }

    public JSONObject mount(String phone, String paymentId, Date startTime) {
        JSONObject ret = new JSONObject();
        try {
            String key = "ypWAhCUMRZ3jxDCj2EWF4z4SpFCbHEak";
            JSONObject dto = new JSONObject();
            dto.put("merchantNo", "mer002");
            dto.put("telNo", phone);
            dto.put("orderTime", DateUtil.parseDateToStr(DateUtil.YYYY_MM_DD_HH_MM_SS, startTime));
            String signUrl = "merchantNo=mer002&orderTime=" + dto.getString("orderTime") + "&telNo=" + phone + "" + key;
            dto.put("sign", SecureUtil.md5(signUrl));
            IMountLog reqLog = new IMountLog();
            reqLog.setReq(dto);
            reqLog.setProductNo(phone);
            reqLog.setOrderId(paymentId);
            JSONObject post = JSONObject.parseObject(HttpUtil.post("http://202.95.23.10:51500/api/telInfo/get", dto.toJSONString()));
            reqLog.setRes(post);
            CompletableFuture.runAsync(() -> iMountLogDao.save(reqLog));
            if (post.getString("code").equals("1")) {
                ret.put("code", "1");
                ret.put("amt", post.getString("amt"));
                ret.put("orderTime", post.getString("orderTime").replaceAll(",", ""));
            } else {
                ret.put("code", "-1");
            }
            return ret;
        } catch (Exception e) {
            log.info("余额获取失败-{}", e.getMessage());
            e.printStackTrace();
            ret.put("code", "-1");
            return ret;
        }
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
