package com.roomster.roomsterbackend.util.helpers;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import java.util.Random;
import java.util.UUID;

public class HashHelper {
    //String ID
    public static String generateEntityId() {
        // Generate a UUID (Universally Unique Identifier)
        UUID entityId = UUID.randomUUID();

        // Convert UUID to a string without hyphens
        String codeString = entityId.toString().replace("-", "");

        return codeString;
    }

    //Int ID
    public static int generateRandomNumbers() {
        Random rnd = new Random();
        return 100000 + rnd.nextInt(900000);
    }
    public static String hmacSHA512(final String key, final String data) {
        try {

            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }
}
