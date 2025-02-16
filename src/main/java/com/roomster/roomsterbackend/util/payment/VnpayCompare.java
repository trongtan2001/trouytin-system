package com.roomster.roomsterbackend.util.payment;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
/**
 * To sort data
 * **/
public class VnpayCompare implements Comparator<String> {

    @Override
    public int compare(String x, String y) {
        if (x == y) return 0;
        if (x == null) return -1;
        if (y == null) return 1;

        // Use Collator for locale-sensitive string comparison
        Collator collator = Collator.getInstance(Locale.US);
        return collator.compare(x, y);
    }
}