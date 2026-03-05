package com.amalitech.demo.metrics;

import com.amalitech.demo.repository.OrdersRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OrderMetricsConfig {

    private final MeterRegistry meterRegistry;
    private final OrdersRepository ordersRepository;

    @PostConstruct
    public void registerOrderMetrics() {
        Gauge.builder("ecom_orders_total", this::getTotalOrders)
                .description("Total number of orders in the system")
                .register(meterRegistry);
    }

    private double getTotalOrders() {
        return ordersRepository.count();
    }
}
