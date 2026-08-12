package com.sprint.training.metrics.service;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    private final MeterRegistry meterRegistry;

    private final Counter crmSuccessCounter;
    private final Timer crmCallDurationTimer;

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        this.crmSuccessCounter = Counter.builder("crm.call.success")
                .description("Number of successful calls to CRM")
                .register(meterRegistry);

        this.crmCallDurationTimer = Timer.builder("crm.call.duration")
                .description("Time taken for CRM calls")
                .register(meterRegistry);
    }
}
