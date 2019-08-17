package com.team.TeamUp.utils;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class UserUtils {

    public String decryptPassword(String password) {
        // TODO
        String key = "aesEncryptionKey";
//        String initVector = "encryptionIntVec";

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

            Key key1 =  new SecretKeySpec(key.getBytes(), "AES");
//            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes());
            byte[] initVector = new byte[cipher.getBlockSize()];
            IvParameterSpec iv = new IvParameterSpec(initVector);

            cipher.init(Cipher.DECRYPT_MODE, key1, iv);
            byte[] original = cipher.doFinal(Base64.decodeBase64(password));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
