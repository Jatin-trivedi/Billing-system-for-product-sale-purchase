package com.retail.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Static utility and method overloading - syllabus Unit 1
public final class Utils {
    private Utils() {
    }

    public static String formatMoney(BigDecimal amount) {
        BigDecimal value = amount == null ? BigDecimal.ZERO : amount;
        return "Rs." + value.setScale(2, RoundingMode.HALF_UP);
    }
}
