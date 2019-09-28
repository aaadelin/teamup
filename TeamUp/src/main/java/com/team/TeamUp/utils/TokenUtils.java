package com.team.TeamUp.utils;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;

public class TokenUtils {

    /**
     * Returns the Base64 value of the given String
     * @param value String to be converted to base64
     * @return the Base64 representation
     */
    public static String getBase64TokenFromValue(String value){
        byte[] bytes = value.getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Returns the value of the given Base64 String
     * @param token String in base64
     * @return the Value in base 10
     */
    public static String getBase64ValueFromToken(String token){
        byte[] bytes = Base64.getDecoder().decode(token);
        return new String(bytes);
    }

    /**
     * Return the Base64 representation of the value
     * @param value value to be represented in base64
     * @return the Base64 representation
     */
    public static String getBase64TokenFromValue(int value){
        byte[] bytes = String.valueOf(value).getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Method to generate an MD5 token
     * It uses the current datetime
     * @return MD5 representation of the current time
     */
    public static String getMD5Token(){
        String string = LocalDateTime.now().toString();
        return getMD5Token(string);
    }

    /**
     * Method to convert from string to MD5 token.
     * @param string String to be converted
     * @return the MD5 representation of the original string
     */
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
