package com.retail.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Method overriding - syllabus Unit 2
public class CardPayment extends Payment {
    @Override
    public BigDecimal calculateCharge(BigDecimal amount) {
        BigDecimal fee = amount.multiply(new BigDecimal("0.02"));
        return amount.add(fee).setScale(2, RoundingMode.HALF_UP);
    }
}
