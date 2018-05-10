package com.zzhl.interceptor;

import com.zzhl.annotation.RequestLockable;
import com.zzhl.service.RedisService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class RequestLockInterceptor {
    @Autowired
    private RedisService redisService;

    @Pointcut("execution(* com.zzhl..*(..)) && @annotation(com.zzhl.annotation.RequestLockable)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        String targetName = point.getTarget().getClass().getName();
        String methodName = point.getSignature().getName();
        Object[] arguments = point.getArgs();

        if (method != null && method.isAnnotationPresent(RequestLockable.class)) {
            RequestLockable requestLockable = method.getAnnotation(RequestLockable.class);

            String lockKey = getLockKey(method, targetName, methodName, requestLockable.key(), arguments);
            Long lockTime = redisService.lock(lockKey, requestLockable.expiredTime(), requestLockable.retryCount());
            if (lockTime != null) {
                try {
                    return point.proceed();
                } finally {
                    redisService.unLock(lockKey, lockTime);
                }
            } else {
                throw new RuntimeException("获取锁资源失败");
            }
        }

        return point.proceed();
    }

    private String getLockKey(Method method, String targetName, String methodName, String[] keys, Object[] arguments) {

        StringBuilder sb = new StringBuilder();
        sb.append("lock.").append(targetName).append(".").append(methodName);

        if (keys != null) {
            String keyStr = String.join(".", keys).trim();
            if (!"".equals(keyStr)) {
                LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
                String[] parameters = discoverer.getParameterNames(method);
                int length = parameters.length;
                if (length > 0) {
                    for (String key : keys) {
                        key = key.trim();
                        if (!"".equals(key)) {
                            for (int i = 0; i < length; i++) {
                                if (key.equalsIgnoreCase(parameters[i])) {
                                    sb.append("#").append(key).append("=").append(String.valueOf(arguments[i]));
                                }
                            }
                        }
                    }
                }
            }
        }
        return sb.toString();
    }
}
