package com.zzhl.util;

/**
 * *********************************************
 * MD5 算法的Java Bean
 *
 * @author:ZHL Last Modified:10,Mar,2008
 * ***********************************************
 */

import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

    public static char[] num_chars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
            'E', 'F'};

    private MD5() {
    }

    public static String md5(String input) {
        return toLowMD5String(input);
    }

    public static String toLowMD5String(String input) {
        return toMD5String(input).toLowerCase();
    }

    public static String toMD5String(String input) {
        if (StringUtils.isEmpty(input)) {
            return null;
        }
        return toMD5String(input.getBytes());
    }

    public static String toMD5String(byte[] input) {
        final char[] output = new char[32];
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] by = md.digest(input);
            for (int i = 0; i < by.length; i++) {
                output[2 * i] = num_chars[(by[i] & 0xf0) >> 4];
                output[2 * i + 1] = num_chars[by[i] & 0xf];
            }
        } catch (NoSuchAlgorithmException e) {
        }
        return new String(output);
    }


    public static String getFileMD5(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return null;
        }
        File file = new File(filePath);
        if (!file.isFile()) {
            return null;
        }
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                md.update(buffer, 0, len);
            }

            byte[] by = md.digest();
            final char[] output = new char[32];
            for (int i = 0; i < by.length; i++) {
                output[2 * i] = num_chars[(by[i] & 0xf0) >> 4];
                output[2 * i + 1] = num_chars[by[i] & 0xf];
            }
            return new String(output);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
            }
        }
        return null;
    }
}
