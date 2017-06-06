package com.example.lucija.p2pchatapp;

import android.util.Base64;
import android.widget.Toast;

import java.security.SecureRandom;
import java.security.Timestamp;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtil {

    private SecretKey myKey;
    private SecretKey contactKey;

    public CryptoUtil(String myKey, String contactKey) {
        this.myKey = new SecretKeySpec(Base64.decode(myKey, Base64.DEFAULT), "AES");
        this.contactKey = new SecretKeySpec(Base64.decode(contactKey, Base64.DEFAULT), "AES");
    }

    public String encrypt(String cleartext) throws Exception {
        //Encrypt with own key
        byte[] result = encrypt(myKey, cleartext.getBytes());
        return toHex(result);
    }

    public String decrypt(String encrypted) throws Exception {
        //Decrypt with contact key
        byte[] enc = toByte(encrypted);
        byte[] result = decrypt(contactKey, enc);
        return new String(result);
    }

    public static byte[] getRawKey() throws Exception {
        Long tsLong = System.currentTimeMillis();
        byte[] seed = tsLong.toString().getBytes();
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG","Crypto");
        sr.setSeed(seed);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        return skey.getEncoded();
    }

    private static byte[] encrypt(SecretKey key, byte[] clear) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(clear);
    }

    private static byte[] decrypt(SecretKey key, byte[] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encrypted);
    }

    public static String toHex(String txt) {
        return toHex(txt.getBytes());
    }
    public static String fromHex(String hex) {
        return new String(toByte(hex));
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length()/2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        return result;
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2*buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }
    private final static String HEX = "0123456789ABCDEF";
    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
    }
}
