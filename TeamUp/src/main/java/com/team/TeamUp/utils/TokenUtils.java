package com.team.TeamUp.utils;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;

public class TokenUtils {

    public static String getBase64TokenFromValue(String value){
        byte[] bytes = value.getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String getBase64ValueFromToken(String token){
        byte[] bytes = Base64.getDecoder().decode(token);
        return new String(bytes);
    }

    public static String getBase64TokenFromValue(int value){
        byte[] bytes = String.valueOf(value).getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String getMD5Token(){
        String string = LocalDateTime.now().toString();
        return getMD5Token(string);
    }

    public static String getMD5Token(String string){
        String myHash = "";
        try {
            String algorithm = "MD5";
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.update(string.getBytes());

            myHash = DatatypeConverter.printHexBinary(messageDigest.digest()).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();

        }
        return myHash;
    }
}
