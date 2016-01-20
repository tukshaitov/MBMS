package com.bankofbaku.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Formater {
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public static String firstToLowerCase(String s) {
        if (s != null && s.length() > 0)
            return Character.toLowerCase(s.charAt(0)) + (s.length() > 1 ? s.substring(1) : "");
        return null;
    }

    public static String firstToUpperCase(String s) {
        if (s != null && s.length() > 0)
            return Character.toUpperCase(s.charAt(0)) + (s.length() > 1 ? s.substring(1) : "");
        return null;
    }

    public static Date deserializeDate(String stringDate) {
        try {
            return dateFormatter.parse(stringDate);
        } catch (Exception e) {
            return null;
        }
    }

    public static String serializeDate(Date date) {
        return dateFormatter.format(date);
    }

}
