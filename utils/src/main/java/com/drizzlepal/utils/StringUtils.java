package com.drizzlepal.utils;

public class StringUtils {

    public static boolean isNotBlank(String str) {
        return str != null && str.trim().length() > 0;
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

}
