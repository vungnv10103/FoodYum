package vungnv.com.foodyum.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptingPassword {
    public String encryptedPassword = "";

    public String EncryptPassword(String password) {
        try {
            /* MessageDigest instance for MD5. */
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");

            /* Add plain-text password bytes to digest using MD5 update() method. */
            messageDigest.update(password.getBytes());
            /* Convert the hash value into bytes */
            byte[] bytes = messageDigest.digest();

            /* The bytes array has bytes in decimal form. Converting it into hexadecimal format. */
            StringBuilder s = new StringBuilder();
            for (byte aByte : bytes) {
                s.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            /* Complete hashed password in hexadecimal format */
            encryptedPassword = s.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encryptedPassword;
    }


}
