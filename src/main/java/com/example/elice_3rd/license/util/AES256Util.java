package com.example.elice_3rd.license.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
public class AES256Util {
    private final String encType = "AES/CBC/PKCS5PADDING";
    private final static String encKey = "YRhyjZ.xy.R.=v.bBx0e.=jUZZ!ZR+!P"; //32byte
    // 초기화 벡터
    private final static String encIv = "8eeIe0!0!xZBs2IZ"; //16byte

    public String encrypt(String id) {
        byte[] bytesKey = encKey.getBytes(StandardCharsets.UTF_8);
        byte[] bytesIv = encIv.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec keySpec = new SecretKeySpec(bytesKey, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(bytesIv);

        Cipher cipher;
        String b64EncryptedData = "";

        try {
            cipher = Cipher.getInstance(encType);

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);

            byte[] encrypted = cipher.doFinal(id.getBytes(StandardCharsets.UTF_8));
            b64EncryptedData = new String(Base64.getEncoder().encode(encrypted));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return b64EncryptedData;

    }

    public String decrypt(String id) {
        byte[] bytesKey = encKey.getBytes(StandardCharsets.UTF_8);
        byte[] bytesIv = encIv.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec keySpec = new SecretKeySpec(bytesKey, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(bytesIv);

        Cipher cipher;
        String decrypted = "";

        try {
            cipher = Cipher.getInstance(encType);

            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);

            byte[] bytesEncrypted = Base64.getDecoder().decode(id);
            decrypted = new String(cipher.doFinal(bytesEncrypted), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            e.getStackTrace();
        }

        return decrypted;
    }

    public SecretKeySpec getKeySpec(String encKey, String encSpec, int bitKeyType) {
        byte[] bytesKey = encKey.getBytes(StandardCharsets.UTF_8);

        return new SecretKeySpec(bytesKey, encType.split("/")[0]);
    }

    public IvParameterSpec getIvSpec(String encIv, int bytesIvLength) {
        byte[] bytesIv = encIv.getBytes(StandardCharsets.UTF_8);

        return new IvParameterSpec(bytesIv);
    }
}
