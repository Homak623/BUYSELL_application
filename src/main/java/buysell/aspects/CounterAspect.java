package buysell.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CounterAspect {
    private int requestCounter = 0;

    private static final Logger LOGGER = LoggerFactory.getLogger(CounterAspect.class);

    @Pointcut("@annotation(buysell.aspects.RequestCounterAnnotation)")
    public void annotatedMethods() {}

    @Before("annotatedMethods()")
    public void incrementCounter(JoinPoint joinPoint) {
        requestCounter++;
        String methodName = joinPoint.getSignature().getName();
        LOGGER.info("Request Counter: {} - {}", requestCounter, methodName);
    }
}