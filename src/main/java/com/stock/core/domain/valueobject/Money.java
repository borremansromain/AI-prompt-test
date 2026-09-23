package com.stock.core.domain.valueobject;

import java.math.BigDecimal;

public record Money(BigDecimal amount, String currency) {

    public static final String DEFAULT_CURRENCY = "EUR";

    public Money {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Money amount must be > 0");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency is required");
        }
    }

    public static Money of(BigDecimal amount) {
        return new Money(amount, DEFAULT_CURRENCY);
    }
}
