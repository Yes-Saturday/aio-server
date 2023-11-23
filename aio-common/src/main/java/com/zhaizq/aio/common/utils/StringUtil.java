package com.zhaizq.aio.common.utils;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class StringUtil {
    private final static Random random = new Random();

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String random(int length) {
        byte[] bytes = new byte[length / 2 + length % 2];
        random.nextBytes(bytes);
        return DigestUtil.asHex(bytes).substring(0, length);
    }

    public static boolean equals(String str1, String str2) {
        return Objects.equals(str1, str2);
    }

    public static boolean isEmpty(Object str) {
        return !(str instanceof String) || ((String) str).isEmpty();
    }

    public static boolean isNotEmpty(Object str) {
        return !isEmpty(str);
    }

    public static String upperCaseFirst(String str) {
        if (str == null)
            return null;
        if (str.isEmpty())
            return str;
        if (str.length() == 1)
            return str.toUpperCase();

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String leftPad(String str, int size, String filling) {
        StringBuilder builder = new StringBuilder();
        for (size = size - str.length(); size > 0; size--)
            builder.append(filling);
        return builder.append(str).toString();
    }
}