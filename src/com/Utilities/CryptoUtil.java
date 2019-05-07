package com.Utilities;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class CryptoUtil {

    private static CryptoUtil INSTANCE;
    private static final String PASSKEY = "$0Y3LM1$M0H0MBR3";

    private CryptoUtil() {}

    public static CryptoUtil getInstance() {
        if (INSTANCE == null) {
            synchronized(CryptoUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CryptoUtil();
                }
            }
        }
        return INSTANCE;
    }

    public String encrypt(String plaintText) {
        try {
            Key aesKey = new SecretKeySpec(PASSKEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(plaintText.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null ;
    }

    public String decrypt(String cipheredText) {
        try {
            Key aesKey = new SecretKeySpec(PASSKEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(cipheredText)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
