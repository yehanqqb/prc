package prc.service.channel.payment.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import prc.service.channel.payment.ChannelPaymentBefore;
import prc.service.common.exception.BizException;
import prc.service.common.utils.HttpRequestUtil;
import prc.service.common.utils.RegexUtil;
import prc.service.dao.SDTaoAccountDao;
import prc.service.model.dto.ProxyDto;
import prc.service.model.entity.IUPayment;
import prc.service.model.entity.SDTaoAccount;
import prc.service.model.vo.ChannelMonitoringVo;
import prc.service.model.vo.ChannelPaymentVo;
import prc.service.service.ProxyService;
import prc.service.service.impl.AlipayLoginService;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service("TaoDaiPayment")
public class TaoDaiPayment extends ChannelPaymentBefore {
    @Autowired
    private SDTaoAccountDao sdTaoAccountDao;
    @Autowired
    private ProxyService QgProxyService;
    @Autowired
    private AlipayLoginService alipayLoginService;


    @Override
    public ChannelPaymentVo getChannelPayUrl(IUPayment iuPayment) {
        ChannelPaymentVo channelPaymentVo = new ChannelPaymentVo();
        SDTaoAccount sdTaoAccount = sdTaoAccountDao.getUse();
        if (Objects.isNull(sdTaoAccount)) {
            channelPaymentVo.setStatus(false);
            log.info("没有tao account");
            return channelPaymentVo;
        }
        try {
            // 下单
            ProxyDto proxyDto = QgProxyService.getRandomHttp();
            InetSocketAddress inetSocketAddress = null;
            Proxy proxy = null;
            if (!Objects.isNull(proxyDto)) {
                inetSocketAddress = new InetSocketAddress(proxyDto.getIp(), Integer.parseInt(proxyDto.getPort()));
                proxy = new Proxy(proxyDto.getProxyType(), inetSocketAddress); // http 代理
            }

            // 转换支付宝

            Map<String, String> header = new HashMap<String, String>();
            header.put("cookie", sdTaoAccount.getTaoCookie());
            String location = HttpRequestUtil.sendHttpsNoRedirectHeader("https://login.taobao.com/member/login.jhtml?tpl_redirect_url=https%3A%2F%2Fauthea179.alipay.com%3A443%2Flogin%2FtrustLoginResultDispatch.htm%3FredirectType%3D%26sign_from%3D3000%26goto%3Dhttps%253A%252F%252Febppprod.alipay.com%252Frecharge%252Frecharge.htm%253Fnull%253D&from_alipay=1", header, proxy).get("Location").get(0);

            Map<String, String> ck2 = HttpRequestUtil.sendHttpsBodyAndCookie(location, header, proxy, "");
            header.put("cookie", ck2.get("cookie"));

            JSONObject jsCk = HttpRequestUtil.sendHttpsNoRedirectCookie("https://ebppprod.alipay.com/recharge/recharge.htm?null=", header, proxy, "");
            if (Objects.isNull(jsCk) || StringUtils.isEmpty(jsCk.getString("cookie"))) {
                throw new BizException(1, "淘宝转换支付宝失败");
            }
            log.info("ali 转换-{}-{}", sdTaoAccount.getAccount(), jsCk.getString("cookie"));


            header.put("cookie", jsCk.getString("cookie"));
            String from_tokeHtml = HttpRequestUtil.sendHttpsBody("https://ebppprod.alipay.com/recharge/recharge.htm", header, proxy);
            String form_token = RegexUtil.regexExist(from_tokeHtml, "name=\"_form_token\" value=\"", "\"/>").replaceAll("name=\"_form_token\" value=\"", "");
            log.info("ali form_token-{}-{}", sdTaoAccount.getAccount(), form_token);

            header.put("Referer", "https://ebppprod.alipay.com/recharge/recharge.htm?null=");

            JSONObject saleIdJSON = JSON.parseObject(HttpRequestUtil.sendHttpsBody("https://ebppprod.alipay.com/recharge/queryPrice.json?mobileNo=" + iuPayment.getProductNo() + "&timeStamp=1697877202834&_input_charset=utf-8", header, proxy));

            log.info("ali saleId-{}-{}-{}", sdTaoAccount.getAccount(), iuPayment.getPaymentNo(), saleIdJSON);
            String saleId = "";
            if (JSON.parseObject(saleIdJSON.getString("rechargeIndexModel")).getString("success").contains("true")) {
                List<Object> prise = JSON.parseObject(saleIdJSON.getString("rechargeIndexModel")).getJSONArray("saleProducts")
                        .stream().filter(item -> {
                            JSONObject itemJson = (JSONObject) item;
                            if (itemJson.getJSONObject("marketPrice").getString("amount").equalsIgnoreCase(String.valueOf(iuPayment.getMoney().intValue()))) {
                                return true;
                            }
                            return false;
                        }).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(prise)) {
                    channelPaymentVo.setRemark("无货");
                    return channelPaymentVo;
                }
                saleId = ((JSONObject) prise.get(0)).getString("id");
            } else {
                channelPaymentVo.setRemark("无货");
                return channelPaymentVo;
            }

            String tradHtml = HttpRequestUtil.sendHttpsPostNoRedirectBodys("https://ebppprod.alipay.com/recharge/createConfirm.htm", header, proxy, "_form_token=" + form_token + "&messageNo=&isChangedMessageNo=1&saleId=" + saleId + "&payAmount=100&channelTyp=TAOBAO&clientInfo=&userHasApp=false&mobileNo=" + iuPayment.getProductNo() + "&marketPrice=100&otherValue=30").get("Location").get(0);

            String trade_no = RegexUtil.regexExist(tradHtml, "out_trade_no%3D", "%26").replaceAll("out_trade_no%3D", "");
            log.info("ali trade_no-{}-{}-{}", sdTaoAccount.getAccount(), iuPayment.getPaymentId(), trade_no);

            // daifu
            List<String> locMap = HttpRequestUtil.sendHttpNoRedirectHeader("https://shenghuo.alipay.com/peerpaycore/prePeerPayApply.htm?biz_no=" + trade_no + "&biz_type=TRADE", header, proxy).get("Location");

            if (CollectionUtils.isEmpty(locMap)) {
                throw new BizException(2, "下单失败");

            }
            String loc = locMap.get(0);

            String payerHtml = HttpRequestUtil.sendHttpNoRedirectHeader(loc, header, proxy).get("Location").get(0);

            String payerNo = RegexUtil.regexExist(payerHtml, "orderId=", "&").replaceAll("orderId=", "");
            log.info("ali payerNo-{}-{}-{}", sdTaoAccount.getAccount(), iuPayment.getPaymentId(), payerNo);


            // 转换
            String reg = HttpRequestUtil.sendHttpNoRedirectHeader("https://lab.alipay.com/consume/queryTradeDetail.htm?tradeNo=" + payerNo, header, proxy).get("Location").get(0);
            String tId = RegexUtil.regexExist(reg, "TradeNo%253D", "%2526forwardAction").replaceAll("TradeNo%253D", "TradeNo%253D").replaceAll("TradeNo%253D", "");

            JSONObject query = new JSONObject();
            sdTaoAccount.setAliCookie(jsCk.getString("cookie"));
            query.put("cookie", sdTaoAccount);
            query.put("aliOrder", payerNo);
            query.put("taoOrder", tId);
            channelPaymentVo.setPaymentNo(tId);
            channelPaymentVo.setStatus(true);
            String uri = "https://pingdtr.xyz/505340822c7e43de836f750d5f1261b9/mount/ali?tradeId=" + iuPayment.getPaymentId();
            channelPaymentVo.setPayUrl("alipays://platformapi/startapp?appId=20000987&url=" + URLEncoder.encode(uri));
            return channelPaymentVo;
        } catch (BizException e) {
            e.printStackTrace();
            if (e.getErrorCode() == 1) {
                sdTaoAccount.setInit(false);
                sdTaoAccount.setRemark(e.getMessage());
                sdTaoAccountDao.saveOrUpdate(sdTaoAccount);
            }
            if (e.getErrorCode() == 2) {
                sdTaoAccount.setStatus(false);
                sdTaoAccount.setRemark(e.getMessage());
                sdTaoAccountDao.saveOrUpdate(sdTaoAccount);
            }
            return channelPaymentVo;
        }
    }

    @Override
    public ChannelMonitoringVo monitoringChannel(IUPayment iuPayment) {
        ChannelMonitoringVo channelMonitoringVo = new ChannelMonitoringVo();
        try {
            SDTaoAccount sdTaoAccount = JSON.parseObject(iuPayment.getQueryJson().getString("cookie"), SDTaoAccount.class);

            Map<String, String> header = new HashMap<String, String>();
            header.put("cookie", sdTaoAccount.getTaoCookie());
            ProxyDto proxyDto = QgProxyService.getRandomHttp();
            InetSocketAddress inetSocketAddress = null;
            Proxy proxy = null;
            if (!Objects.isNull(proxyDto)) {
                inetSocketAddress = new InetSocketAddress(proxyDto.getIp(), Integer.parseInt(proxyDto.getPort()));
                proxy = new Proxy(proxyDto.getProxyType(), inetSocketAddress); // http 代理
            }


            String html = HttpRequestUtil.sendHttpsBody("https://trade.taobao.com/trade/detail/trade_order_detail.htm?biz_order_id=" + iuPayment.getQueryJson().getString("taoOrder"), header, proxy);
            String jsj = new String(RegexUtil.regexExist(html, "\"statusInfo\\\":{\\\"text\\\":\\\"", "\\\",")
                    .replace("\"statusInfo\\\":{\\\"text\\\":\\\"", "").getBytes("UTF-8"), "UTF-8");
            log.info("检控-{}-{}", iuPayment.getPaymentId(), jsj);
            if (jsj.contains("交易成功")) {
                channelMonitoringVo.setFinishStatus(true);
                channelMonitoringVo.setPayStatus(true);
                return channelMonitoringVo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channelMonitoringVo;
    }

    @Override
    public String refreshUrl(IUPayment iuPayment) {
        return null;
    }

    @Override
    public JSONObject getBefore(IUPayment iuPayment) {


        return null;
    }

    @Override
    public JSONObject getAfter(IUPayment iuPayment) {
        JSONObject jsonObject = new JSONObject();

        try {
            String toUserId = alipayLoginService.getAccessTokenToUserId(iuPayment.getUserKey());
            SDTaoAccount sdTaoAccount = JSON.parseObject(iuPayment.getQueryJson().getString("cookie"), SDTaoAccount.class);
            Map<String, String> header = new HashMap<>();
            header.put("cookie", sdTaoAccount.getAliCookie());
            String payerNo = iuPayment.getQueryJson().getString("aliOrder");
            // 下单
            ProxyDto proxyDto = QgProxyService.getRandomHttp();
            InetSocketAddress inetSocketAddress = null;
            Proxy proxy = null;
            if (!Objects.isNull(proxyDto)) {
                inetSocketAddress = new InetSocketAddress(proxyDto.getIp(), Integer.parseInt(proxyDto.getPort()));
                proxy = new Proxy(proxyDto.getProxyType(), inetSocketAddress); // http 代理
            }
            String pastseHtml = HttpRequestUtil.sendHttpsBody("https://shenghuo.alipay.com/peerpaycore/choosePeerPayer.htm?orderId=" + payerNo + "&peerpayType=NEW_PPAY&peerPayerCardNo=" + toUserId + "&message=", header, proxy);
            String patse = RegexUtil.regexExist(pastseHtml, "https://shenghuo.alipay.com:443/peerpaycore/confirmPeerPay.htm", "\"");
            System.out.println(patse);
            jsonObject.put("retUrl", patse);
            jsonObject.put("code", 0);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("code", -1);
            return jsonObject;
        }
    }
}
