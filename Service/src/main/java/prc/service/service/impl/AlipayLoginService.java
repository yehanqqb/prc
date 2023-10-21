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
    private static final String APP_ID_PROD = "2021003178676032";
    /**
     * 私钥
     */
    private static final String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCL0UMzIYYX5HG1UyrtsBFzffQdUfVDoxZSgInA1qY1+rxEgsKnHhigphaOO3q5Q4QADADdfsXSJ5ozrSigNe5xYQdOB2ZpTmG/vKyqCoWE8nxCiUBw5qgKJ1bchzrqBulxxU3L+8ASrvCDBX/cnki1tWbhK+t0QKyrgCEI95+K3rYuyaJpcYRuTRirHpqvm6VEwmUFR3Bszy9umFeavnswV7NlWFO5p37XgjZ6tsJHVSaJVPItUoX+lgmjR0ouPoY3uS3ZpRzuaLDkrHJhtxvpu4lnAO/r6mGUOR/xIzwPABy+kAvx2ZuLZl/4SvycxjRaMqyi0adbO0kw2dwbYR5HAgMBAAECggEABEIfVdNTji6699bMm+ic33Oal7oDSkgRSfn4OBNRhJtaWIvS+dwEy//C+Bn/ptzTjFzROe2+gKQMIj7H2flKhdF6s3muOM7WU0yfxZ4EawQnnK8pc0bTH1wLXTQRK0eCnKBxuiC0vNzvHk5wknuPo1kfBQSdazD9EUwZtXg1N7l9MGA7WduRqk8Nc7Q1DkmOcmUl8p/WZjlY3nsRwclLfoIbvtXONH9NpZqaSKF+KjqKdiAqPgG6a7/WT1uOGBoIsyl78ZEm3ugn9/AiRM69eiWJ+jocIGXmVeX4u4ADwatHCrdBTh8Aa64bujvHz0SezrD5MtvfIEDDr8eK1GwAoQKBgQDHzRnNpb1ZtPCssYDSwIvE8jDyKAEekdOZMaHxVpmj/WZu4u2b0nLTArapp9UARqs1nvluAbFgLX1uFsNLAhKeUPqp120Lsr5DHASHNWahjzqTjP8XEla3YYafUuHM+9y49VWCky0QB4ZXxo0VO5CFK71xEspWCQTrN6XzoeGQPQKBgQCzJPewlauBspmAp9UXIgeS3J/E+knHlkUQ35mC4spiQRoFSUWlpuJP4hoOt7hbbt+4ZajvoJpsbMQ1DAUX14F6eTCFq2CKTLhsvuUy4EHR952shNVsyvPDXLncQtdVNR8xxFEbZ95+28qPlt79DIFYkYAFNA9FgWzBbkWHhGvs0wKBgQCjY0T7l9KdPksmXc2ECvMBbjC6hh5MOmI4P483xWLE6R7QBQDb0SAXEHwBhv63SBMQOV2uKPI9PVX6JeE0QJCrKHujUkiZLWVLiejmDLRN38u+B6o+2r9RHQ6y0VtXfotEMVPpAKP0HMMumm6a82e+j0NO6VfPPXbpR0uNTU8g3QKBgFJA2AVZQ00ivv5mkR20/zs8V5NuxC+C3KVZtTd2gCxK7fKdaQAdNu4sx4AYdOpuZwRYkQ+tcxnzesdMhI4k4gtk15WYUZROk6NCpyQL+cy5X8kVmJUCPphl2+S5P/ucj30fQYpOz8b5jfEN+cbAVidfbyN0esqUGWZbBkupzpAHAoGAQq/GBaB93Wyrk/0c4fys512xXRbLq9j5BOiKL1rP2iEpxEwmtPzXsSOPzFrpM1q2xQwet+irXP85wHf+niKQ3Xt/u+GUhBFoiDuzhCeViLnCG2S6rDN5GTR2QGEC2Rq2aPQfhORPRawR6PGmWv3HLUeme2I7oONd8Gl6X0VDwcU=";
    /**
     * 公钥
     */
    private static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArT4wPjGjnR1brv15kzpjeISRWDvpiAT1HHLLvtIq9/nAT/vM80rX73+Rqb+SYSdQhLbWcRzdsHLRQFUIK40zIUZvBM4eic/40NPe56M40yxQLxjTr7PsJVBZvVyXWJ4TIKl2fK1ueC8SS6a6nW1DlQYnG2h4Q4xUZAVBM0Jfr8tB+F9L6dy4NNvJd/H4KTkXJrLUu/5oilx36tHQW48nEGLtju9Y7ZkI7mHaCzVVVFQVf1uaCKZo1dOJBmtlSi8HNw7qeG8nfi9LVrfUsXwJZSjjvYdQdYjjOZr4NgZMuh50MDphzb7veXuu5IMnEQ3KbOg5FD1aa0WtTcr0cr/aMwIDAQAB";

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