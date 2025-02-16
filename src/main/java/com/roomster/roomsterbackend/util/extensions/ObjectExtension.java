package com.roomster.roomsterbackend.util.extensions;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/*
 *  Extension to convert object to query string
 */
public class ObjectExtension {
    public static String toQueryString(Object obj) {
        try {
            Map<String, String> propertyMap = new HashMap<>();

            // Get all properties of the object
            for (java.lang.reflect.Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                // Filter out fields with null values
                if (field.get(obj) != null) {
                    // Create a map entry for each field name and its URL-encoded value
                    String encodedValue = URLEncoder.encode(field.get(obj).toString(), "UTF-8");
                    propertyMap.put(field.getName(), encodedValue);
                }
            }

            // Convert the map entries to a query string
            return propertyMap.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("&"));
        } catch (Exception e) {
            throw new RuntimeException("Error generating query string", e);
        }
    }
}
