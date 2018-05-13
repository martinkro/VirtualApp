package io.virtualapp.login;

import android.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by sundayliu on 2017/7/10.
 */

public class AES {
    public static String base64iv;

    static {
        base64iv = "3k8YCZTFIWWBobRLLoscIw==";
    }

    public static String Encrypt(String sSrc, String sKey) throws Exception {
        byte[] ivKey = Base64.decode(base64iv, 0);
        if (sKey == null || sKey.length() != 16) {
            return null;
        }
        SecretKeySpec skeySpec = new SecretKeySpec(sKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");  // 算法 模式  补码方式
        cipher.init(1, skeySpec, new IvParameterSpec(ivKey)); // CBC 模式增加一个iv向量 可以增加加密强度
        byte[] srawt = sSrc.getBytes();
        int len = srawt.length;
        while (len % 16 != 0) {
            len++;
        }
        byte[] sraw = new byte[len];
        for (int i = 0; i < len; i++) {
            if (i < srawt.length) {
                sraw[i] = srawt[i];
            } else {
                sraw[i] = (byte) 0;
            }
        }
        return Base64.encodeToString(cipher.doFinal(sraw), 0);
    }

    public static String Decrypt(String sSrc, String sKey) throws Exception {
        String str = null;
        try {
            byte[] ivKey = Base64.decode(base64iv, 0);
            if (sKey != null && sKey.length() == 16) {
                SecretKeySpec skeySpec = new SecretKeySpec(sKey.getBytes("ASCII"), "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
                cipher.init(2, skeySpec, new IvParameterSpec(ivKey));
                try {
                    str = new String(cipher.doFinal(Base64.decode(sSrc, 0))).trim();
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        return str;
    }

}
