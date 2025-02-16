package com.roomster.roomsterbackend.util.extensions;

import java.util.Arrays;
import java.util.LinkedHashMap;

public class ConvertStringToArrayExtension {
    public static void convertStringToArray(LinkedHashMap<String, Object> map) {
        for (String key : map.keySet()) {
            if (key.equals("price")) {
                String priceRange = (String) map.get(key);
                int[] price = Arrays.stream(priceRange.split(",")).mapToInt(Integer::parseInt).toArray();
                map.put(key, price);
            } else if (key.equals("acreage")) {
                String acreageRange = (String) map.get(key);
                int[] acreage = Arrays.stream(acreageRange.split(",")).mapToInt(Integer::parseInt).toArray();
                map.put(key, acreage);
            }
        }
    }
}
