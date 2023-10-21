package prc.api.service.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prc.service.common.utils.HttpRequestUtil;
import prc.service.common.utils.RegexUtil;
import prc.service.dao.SDTaoAccountDao;
import prc.service.model.dto.ProxyDto;
import prc.service.model.entity.SDTaoAccount;
import prc.service.service.ProxyService;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ApiAliService {
    @Autowired
    private ProxyService QgProxyService;

    @Autowired
    private SDTaoAccountDao sdTaoAccountDao;


    public String initCode() {
        /*ProxyDto proxyDto = QgProxyService.getRandomHttp();
        InetSocketAddress inetSocketAddress = null;
        Proxy proxy = null;
        if (!Objects.isNull(proxyDto)) {
            inetSocketAddress = new InetSocketAddress(proxyDto.getIp(), Integer.parseInt(proxyDto.getPort()));
            proxy = new Proxy(proxyDto.getProxyType(), inetSocketAddress); // http 代理
        }*/
        try {
            String by = HttpRequestUtil.sendHttpsPostBodys("https://login.taobao.com/newlogin/sms/send.do?appName=taobao&fromSite=0&_bx-v=2.5.3", new HashMap<>(), null, "phoneCode=86&countryCode=CN&codeLength=6&umidGetStatusVal=255&screenPixel=1366x768&navlanguage=zh-CN&navPlatform=Win32&appName=taobao&appEntrance=taobao_pc&bizParams=renderRefer%3Dhttps%253A%252F%252Fwww.taobao.com%252F&style=default&appkey=00000000&from=tbTop&isMobile=false&lang=zh_CN&returnUrl=https%3A%2F%2Fwww.taobao.com%2F&fromSite=0&umidTag=SERVER&weiBoMpBridge=");
            JSONObject byJSon = JSON.parseObject(by);
            String html = HttpRequest.get(byJSon.getJSONObject("data").getString("url")).execute().body();
            return JSON.toJSONString(JSON.parseObject(RegexUtil.regexExist(html, " window._config_ = ", "};").replaceAll(" window._config_ = ", "") + "}".replaceAll("/newlogin/sms/send.do/_____tmd_____/punish?", "")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Boolean checkCode(String slider, String xd5) {

        try {

            HttpResponse xd5Der = HttpRequest.get("https://reg.taobao.com/member/reg/fast/unify_process.do/_____tmd_____/slide?slidedata=" + URLEncoder.encode(slider) + "&x5secdata=" + xd5 + "&landscape=1&v=08193631568940978&t=" + new Date().getTime())
                    .execute();

/*            String sendPath = "https://reg.taobao.com/member/reg/fast/unify_process.do/_____tmd_____/slide?slidedata=" + URLEncoder.encode(slider) + "&x5secdata=" + xd5 + "&landscape=1&v=08193631568940978&t=" + new Date().getTime();
            Map<String, String> xd5Der = HttpRequestUtil.sendHttpsBodyAndCookie(sendPath, new HashMap<>(), proxy, "");*/
            JSONObject xd5Body = JSON.parseObject(xd5Der.body());
            if (!"0".equalsIgnoreCase(xd5Body.getJSONObject("result").getString("code"))) {
                return false;
            }
            CompletableFuture.runAsync(() -> {
                ProxyDto proxyDto = QgProxyService.getRandomHttp();
                InetSocketAddress inetSocketAddress = null;
                Proxy proxy = null;
                if (!Objects.isNull(proxyDto)) {
                    inetSocketAddress = new InetSocketAddress(proxyDto.getIp(), Integer.parseInt(proxyDto.getPort()));
                    proxy = new Proxy(proxyDto.getProxyType(), inetSocketAddress); // http 代理
                }
                SDTaoAccount sdTaoAccount = sdTaoAccountDao.getInit();
                if (Objects.isNull(sdTaoAccount)) {
                    return;
                }

                String xe5 = xd5Der.headers().get("Set-Cookie").get(0);
                JSONObject sliderJson = JSON.parseObject(slider);
                String sendSmsPath = "https://login.taobao.com/newlogin/sms/send.do?appName=taobao&fromSite=0&_bx-v=2.5.3&phoneCode=86&countryCode=CN&codeLength=6&umidGetStatusVal=255&screenPixel=1366x768&navlanguage=zh-CN&navPlatform=Win32&appName=taobao&appEntrance=taobao_pc&bizParams=renderRefer%3Dhttps%253A%252F%252Fwww.taobao.com%252F&style=default&appkey=00000000&from=tbTop&isMobile=false&lang=zh_CN&returnUrl=https%3A%2F%2Fwww.taobao.com%2F&fromSite=0&umidTag=SERVER&weiBoMpBridge=";
                String smsParam = "loginId=" + sdTaoAccount.getAccount() + "&countryCode=CN&codeLength=6&umidGetStatusVal=255&screenPixel=1366x768&navlanguage=zh-CN&navPlatform=Win32&appName=taobao&appEntrance=taobao_pc&bizParams=renderRefer%3Dhttps%253A%252F%252Fwww.taobao.com%252F&style=default&appkey=00000000&from=tbTop&isMobile=false&lang=zh_CN&returnUrl=https%3A%2F%2Fwww.taobao.com%2F&fromSite=0&umidTag=SERVER&weiBoMpBridge=&appName=taobao&fromSite=0&_bx-v=2.5.3&phoneCode=86&bx-ua=" + sliderJson.getString("n") + "&bx-umidtoken=" + JSON.parseObject(sliderJson.getString("p")).getString("umidToken");


                Map<String, String> header = new HashMap<>();
                header.put("cookie", xe5);
                String sendToken = HttpRequestUtil.sendHttpPostBodys(sendSmsPath, header, proxy, smsParam);


                JSONObject sendTokenJSon = JSON.parseObject(sendToken);
                log.info("send log json-{}", sendTokenJSon);

                String smsToken = sendTokenJSon.getJSONObject("content").getJSONObject("data").getString("smsToken");

                Date date = new Date();
                String smsCode = "";
                while (true) {
                    Date nowDate = new Date();
                    if (nowDate.getTime() - date.getTime() >= 300 * 1000) {
                        log.info("接受不到验证码-{}", sdTaoAccount.getAccount());
                        sdTaoAccount.setStatus(false);
                        sdTaoAccount.setSmsError(true);
                        sdTaoAccountDao.saveOrUpdate(sdTaoAccount);
                        return;
                    }
                    try {
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    String smsHtml = HttpRequest.get(sdTaoAccount.getSmsUrl()).execute().body();
                    log.info("验证吗-{}-{}", sdTaoAccount.getAccount(), smsHtml);
                    Pattern pattern = Pattern.compile("\\d{6}");
                    Matcher matcher = pattern.matcher(smsHtml);
                    if (matcher.find()) {
                        smsCode = matcher.group();
                    }

                    if (!StringUtils.isEmpty(smsCode)) {
                        break;
                    }
                }

                Map<String, String> newAndCookie = HttpRequestUtil.sendHttpsBodyAndCookie("https://login.taobao.com/newlogin/sms/login.do?appName=taobao&fromSite=0&_bx-v=2.5.3", header, proxy, "loginId=" + sdTaoAccount.getAccount() + "&phoneCode=86&countryCode=CN&smsCode=" + smsCode + "&smsToken=" + smsToken + "&keepLogin=false&umidGetStatusVal=255&screenPixel=1330x749&navlanguage=zh-CN&navPlatform=Win32&appName=taobao&appEntrance=taobao_pc&bizParams=&style=default&appkey=00000000&from=tb&isMobile=false&lang=zh_CN&fromSite=0&umidTag=SERVER&weiBoMpBridge=&pageTraceId=&bx-ua=" + sliderJson.getString("n") + "&bx-umidtoken=" + JSON.parseObject(sliderJson.getString("p")).getString("umidToken"));
                log.info("accountCookie-{}:{}", sdTaoAccount.getAccount(), newAndCookie.get("cookie"));
                if (StringUtils.isEmpty(newAndCookie.get("cookie"))) {
                    log.info("接吗失败-{}-{}", sdTaoAccount.getAccount(), JSON.toJSONString(newAndCookie));
                    sdTaoAccount.setStatus(false);
                    sdTaoAccountDao.saveOrUpdate(sdTaoAccount);
                } else {
                    log.info("接吗成功-{}", sdTaoAccount.getAccount());
                    sdTaoAccount.setTaoCookie(newAndCookie.get("cookie"));
                    sdTaoAccount.setInit(false);
                    sdTaoAccountDao.saveOrUpdate(sdTaoAccount);
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
