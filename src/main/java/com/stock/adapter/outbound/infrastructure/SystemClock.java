package com.stock.adapter.outbound.infrastructure;

import com.stock.core.domain.port.ClockPort;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class SystemClock implements ClockPort {
    public Instant now() { return Instant.now(); }
}
