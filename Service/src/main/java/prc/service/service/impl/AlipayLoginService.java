package prc.service.service.impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * 支付宝第三方登录
 *
 * @author minghui.y
 * @create 2018-05-22 13:10
 **/
@Service
@Slf4j
public class AlipayLoginService implements InitializingBean {


    /**
     * Alipay客户端
     */
    private AlipayClient alipayClient;

    /**
     * 支付宝网关
     */
    private static final String ALIPAY_BORDER_DEV = "https://openapi.alipaydev.com/gateway.do";
    private static final String ALIPAY_BORDER_PROD = "https://openapi.alipay.com/gateway.do";
    /**
     * appID
     **/
    private static final String APP_ID_DEV = "xxxxxx";
    private static final String APP_ID_PROD = "2021004121681299";
    /**
     * 私钥
     */
    private static final String APP_PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCw1yC8hOI5i+1pOcLA+/v5QbkhqayKUhsiLoMFkls86FS9rAP0hIxuKkXHKLUYuQ9HHx3Gs+9ta1bzhGrhxbYzLyXN8dSOAtcZcVTmR7qX6Dp4lCvfzfqK/aP6f7bF7j7oW3iEg1vBNxHMY/JURaUzsYYMv+CiKImGgW/5OoMSQCRJ0z9gbSoRjOYOpriztZlb6TjRWVmNLBU4gztxHVNkJW1stccQQT+E78SBaYtuuU61CwDtDFhVWwFq9uGK46P1bgoHJhmzMLoKGcHNm2REw2rjInfzivs8qFL050A9uoGKVSXldF7PaKR8oHEdpQgPBNn2I9cJ5oi52WasUqsJAgMBAAECggEADVO5INv23JViB/sVZUvYOnmshQ/vc+EqMFJHB4V3IMj1kfHDvoq6tpay+YZk138i2p5KtS3cTme3ftSs5WTsDUFxXOrrtOQb58v1tQEU29vhhDLOd6hjSFRDJl63nIUO8p3qIDpyEMagrcGegqFZCONJEe7n8/CQJLXehW7Wb+qLRN+KdwAKiFBUeWGsV0PqI6jnkN5jIWfj63ALTAtLVabc2I7AVqbq6wZWizuFGfsWEZ6cLFGnrqNIiHhf3OXubLTVhAVfSFAg4T3SNrYWfXxO93y1rto2/AY7QYVt2u3oeq2OAT8DThJnyvF9ogOJR+52XG7zor4oTm0ZTM/a7QKBgQDvy2K1dD1JD8DqIR8tWwAuKt6srani7IItm0HR9KUYt0ida1VsR+9dg2wD8BJlPZ2DiBRawLxZoBCfPWunDR9V8ThceVUtr/szvExl5HyG/KyBroo3PUJIUgzYjN4K5dKZ4b7eU8LfevBcnaY6BoF7AvQ46GvUhi2qgqujareXCwKBgQC8ypdX+uGVn9c+IfnmoGErkEFFS9uf5ZmCQ9i5dX5TZu4z0A2IKq80DzypHCToBNWBpAn+SrH1Qm6Nutf5lUHt/Wf+4u4+SWut773w8/AZ5U62AXP2GJ2BTOwQHG3Lo31eRb2wR+IWariPVGox7s314TaA3wBqgY5PpXT+m+PCuwKBgAKM3jLh1pkFUt7qlUNNOXZEPHQHsMvaChRkF1IdUPgvUtVhw9nP9gq/kBGGpxtuiVNRyi2g6R/m5OZcp91x9UhbtWa+X5qJyZkmQoW+5VgmLct/SLcHrsHIJZzT+rSQ9yltzjLdHzBzRUUm5BKv6Qae5GwKhMMQ3kF2/E5Tnzo7AoGAZDLmFlTaK451L83ak3PlfriGW+ACBaBXVBLsUKoyTNOhczBmLjHxYSEBIgQE9nuyUacdgrBkOPZ0NJbJD/cO0eAftOD+b2KKWvuWBO+DLEtO9jxZLnEsDzcjRv2/rEfuPv2myousTBeCMtgrsKq891X7lnRFBUITpOP87zEPtgcCgYBqzLX6VTVX082qL5kKbNmM3bbvz4DJT7KsbNPM3TYZwkxHeOExBjj1NUmqAPK7HEq4xmf2hQvMoqzMt98qBemrRkXezPIn2ei/ZRViFZGAo94ASp9qKL/+gXe3k0lZUgDBlpMRtp8rB9rRzpdwZn5eAImQOXXW0Ce5PbIdpjKEKA==";
    /**
     * 公钥
     */
    private static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqc4Z7weCTjImMytLx67K4vdmou6ksSTT41F4N/wO2pSz4CEF0BHTKz43U1GdusdrVlREXpBpQZO2eEiCs5OZRPKxMs8B0xuHaUQvEKH4r1rvwfCfNzNmrmjEKOLNjHxWYqHSDZ9F26lPd5fJBhXgNHXN/wpbDe0uYItM7lZQTGhvefNcyAloY0oVly7OFNR1Rr+wCwrxgRZ1i3cKQMvpJkQ5FL2igHuAl4ZOqmXx22bJ+Xa05u2y43ckIQ4nkkKq3y/BwXI7UF9En1D/nHgMtS501NE3U7OJn9zIC/E5q4KQN+aD6xMG0Uk2Lz6rXh21l3TPN01NTa/mA61RRhQf6wIDAQAB";

    @Override
    public void afterPropertiesSet() {
        alipayClient = new DefaultAlipayClient(ALIPAY_BORDER_PROD, APP_ID_PROD, APP_PRIVATE_KEY, "json", "GBK", ALIPAY_PUBLIC_KEY, "RSA2");
    }

    /**
     * 根据auth_code获取用户的user_id和access_token
     *
     * @param authCode
     * @return
     */
    public String getAccessToken(String authCode) {
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setCode(authCode);
        request.setGrantType("authorization_code");
        try {
            AlipaySystemOauthTokenResponse oauthTokenResponse = alipayClient.execute(request);
            return oauthTokenResponse.getAccessToken();
        } catch (Exception e) {
            log.error("使用authCode获取信息失败！", e);
            return null;
        }
    }

    /**
     * 根据auth_code获取用户的user_id和access_token
     *
     * @param authCode
     * @return
     */
    public String getAccessTokenToUserId(String authCode) {
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setCode(authCode);
        request.setGrantType("authorization_code");
        try {
            AlipaySystemOauthTokenResponse oauthTokenResponse = alipayClient.execute(request);
            return oauthTokenResponse.getUserId();
        } catch (Exception e) {
            log.error("使用authCode获取信息失败！", e);
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(new AlipayLoginService().getAccessTokenToUserId("0308f383bf264d3bbceeaa160b7bVX20"));
    }

    /**
     * 根据access_token获取用户信息
     *
     * @param token
     * @return
     */
    public AlipayUserInfoShareResponse getUserInfoByToken(String token) {
        AlipayUserInfoShareRequest request = new AlipayUserInfoShareRequest();
        try {
            AlipayUserInfoShareResponse response = alipayClient.execute(request, token);
            if (response.isSuccess()) {
                return response;
            }
            log.error("根据 access_token获取用户信息失败!");
            return null;
        } catch (Exception e) {
            log.error("根据 access_token获取用户信息抛出异常！", e);
            return null;
        }
    }
}