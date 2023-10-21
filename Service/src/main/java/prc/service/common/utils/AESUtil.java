package prc.service.common.utils;

import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

public class AESUtil {
    private static final SymmetricCrypto aes;

    static {
        // sgEsnN6QWq8W7j5H01020304即为密钥明文长24位，
        // 不够则会随机补足24位
        aes = new AES("CBC", "PKCS7Padding",
                // 密钥，可以自定义
                "0123456789ABHAEQ".getBytes(),
                // iv加盐，按照实际需求添加
                "DYgjCEIMVrj2W9xN".getBytes());
    }

    /**
     * des解密
     *
     * @param content 密文
     * @return 解密后的明文
     */
    public static String decrypt(String content) {
        return aes.decryptStr(content);
    }

    /**
     * des加密
     *
     * @param content 明文
     * @return 加密后的密文
     */
    public static String encryption(String content) {
        return aes.encryptHex(content);
    }


    public static void main(String[] args) {
        System.out.println(encryption("53580318-0e8e-49dc-84a9-4aaa5c77edad,1693745223003,ALIPAY_H5"));
    }
}
