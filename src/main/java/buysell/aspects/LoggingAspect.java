package buysell.aspects;

import java.util.Arrays;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class LoggingAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* buysell.services.*.*(..))")
    public void allServiceMethods() {}

    @Pointcut("@annotation(buysell.aspects.AspectAnnotation)")
    public void annotatedMethods() {}

    @Before("allServiceMethods() || annotatedMethods()")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();

        if (shouldLog(methodName)) {
            LOGGER.info(">> {}()", methodName);
        }
    }

    private boolean shouldLog(String methodName) {
        return methodName.startsWith("update");
    }

    @AfterReturning(pointcut = "allServiceMethods() || annotatedMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        LOGGER.info("<< {}() - result: {}", methodName, result);
    }

    @AfterThrowing(pointcut = "allServiceMethods() || annotatedMethods()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        LOGGER.error("<< {}() - exception: {}", methodName, exception.getMessage());
    }
}