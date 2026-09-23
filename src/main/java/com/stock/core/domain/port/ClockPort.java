package com.stock.core.domain.port;

import java.time.Instant;

public interface ClockPort {
    Instant now();
}
