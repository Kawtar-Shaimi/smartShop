package com.demo.smartShop.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {

    private static final int BCRYPT_ROUNDS = 12;

    private PasswordUtil() {
    }

    public static String hash(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    public static boolean verify(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(password, hashedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
