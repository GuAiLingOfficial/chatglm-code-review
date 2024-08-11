package com.rsl.middleware.sdk.types.utils;

import java.util.Random;

/**
 * @ description:
 * @ author: rsl
 * @ create: 2024-08-11 15:03
 **/
public class RandomStringUtils {
    public static String randomNumeric(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }
}
