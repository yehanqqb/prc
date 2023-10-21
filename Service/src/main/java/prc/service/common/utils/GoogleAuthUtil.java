package prc.service.common.utils;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

public class GoogleAuthUtil {
    /**
     * Generate a random secret key. This must be saved by the server and
     * associated with the users account to verify the code displayed by Google
     * Authenticator. The user must register this secret on their device.
     * 生成一个随机秘钥
     *
     * @return secret key
     */
    public static String generateSecretKey() {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        final GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    public static String getQr(String email, String secret) {
        return "https://chart.googleapis.com/chart?chs=200x200&chld=M|0&cht=qr&chl=otpauth://totp/" + email + "%3Fsecret%3D" + secret;
    }

    public static boolean checkCode(String secret, int code) {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        return gAuth.authorize(secret, code);
    }
}
