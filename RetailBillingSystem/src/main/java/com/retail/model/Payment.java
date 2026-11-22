package com.retail.model;

import java.math.BigDecimal;

// Abstraction and polymorphism - syllabus Unit 2: abstract payment behaviour
public abstract class Payment {
    public abstract BigDecimal calculateCharge(BigDecimal amount);
}
