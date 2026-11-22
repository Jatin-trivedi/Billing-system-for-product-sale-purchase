package com.retail.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Method overriding - syllabus Unit 2
public class CashPayment extends Payment {
    @Override
    public BigDecimal calculateCharge(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
