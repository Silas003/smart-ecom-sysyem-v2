package com.amalitech.demo.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class MonitoringAspect {

    private static final Logger log = LoggerFactory.getLogger(MonitoringAspect.class);
    private final MeterRegistry meterRegistry;

    public MonitoringAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Pointcut("within(com.amalitech.demo.services..*)")
    public void serviceLayer() {}

    @Around("serviceLayer()")
    public Object monitorService(ProceedingJoinPoint pjp) throws Throwable {
        Signature sig = pjp.getSignature();
        String metricTag = sig.getDeclaringTypeName() + "." + sig.getName();

        Timer.Sample sample = Timer.start(meterRegistry);
        Counter counter = meterRegistry.counter("service.invocations", "method", metricTag);
        counter.increment();
        try {
            Object result = pjp.proceed();
            sample.stop(meterRegistry.timer("service.execution.time", "method", metricTag));
            return result;
        } catch (Throwable t) {
            meterRegistry.counter("service.errors", "method", metricTag, "exception", t.getClass().getSimpleName()).increment();
            sample.stop(meterRegistry.timer("service.execution.time", "method", metricTag));
            throw t;
        }
    }
}
