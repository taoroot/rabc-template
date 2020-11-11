package com.github.taoroot.common.security.log;

import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.common.core.vo.AuthUserInfo;
import com.github.taoroot.common.core.vo.LogInfo;
import com.github.taoroot.common.security.AuthUser;
import com.github.taoroot.common.security.SecurityUtils;
import com.github.taoroot.common.security.annotation.Log;
import cn.hutool.extra.servlet.ServletUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Objects;

@Aspect
public class LogAspect {

    private final ApplicationEventPublisher publisher;

    private static final Logger logger = LogManager.getLogger(LogAspect.class);

    public LogAspect(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @SneakyThrows
    @Around("@annotation(log)")
    public Object around(ProceedingJoinPoint point, Log log) {
        String strClassName = point.getTarget().getClass().getName();
        String strMethodName = point.getSignature().getName();
        logger.debug("[类名]:{},[方法]:{}", strClassName, strMethodName);

        long startTime = System.currentTimeMillis();
        Object obj;
        LogInfo logInfo = new LogInfo();
        try {
            if (log.saveParam()) {
                saveParam(point, logInfo);
            }
            obj = point.proceed();
            handleLog(logInfo, point, log, startTime, null, obj);
        } catch (Exception e) {
            handleLog(logInfo, point, log, startTime, e, null);
            throw e;
        }
        return obj;
    }

    protected void handleLog(LogInfo logInfo, JoinPoint joinPoint, Log logAnnotation, long startTime, Exception e, Object jsonResult) {
        ObjectMapper objectMapper = new ObjectMapper();
        HttpServletRequest request = ((ServletRequestAttributes) Objects
                .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        try {
            logInfo.setStatus(R.OK);
            logInfo.setIp(ServletUtil.getClientIP(request));
            logInfo.setUrl(request.getRequestURL().toString());
            logInfo.setTime(System.currentTimeMillis() - startTime);

            if (jsonResult instanceof R) {
                Object data = ((R<?>) jsonResult).getData();
                if (data instanceof AuthUserInfo && SecurityUtils.userId() == -1) {
                    AuthUserInfo userInfo = (AuthUserInfo) data;
                    logInfo.setUserId(Integer.valueOf(userInfo.getUsername()));
                    logInfo.setDeptId(userInfo.getDeptId());
                } else {
                    logInfo.setUserId(SecurityUtils.userId());
                    AuthUser authUser = SecurityUtils.userInfo();
                    if (authUser != null) {
                        logInfo.setDeptId(authUser.getDeptId());
                    }
                }
            }

            if (e != null) {
                logInfo.setStatus(R.ERROR);
                logInfo.setError(e.getMessage());
            }

            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            logInfo.setMethod(className + "." + methodName + "()");
            logInfo.setRequestMethod(request.getMethod());

            logInfo.setBusinessType(logAnnotation.businessType());
            logInfo.setTitle(logAnnotation.value());
            logInfo.setOperatorType(logAnnotation.operatorType());
            if (logAnnotation.saveResult()) {
                String result = objectMapper.writeValueAsString(jsonResult);
                logInfo.setResult(result.substring(0, Math.min(result.length(), 8000)));
            }
            publisher.publishEvent(logInfo);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }


    private void saveParam(JoinPoint joinPoint, LogInfo logInfo) throws Exception {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }

        StringBuilder params = new StringBuilder();
        Arrays.stream(args).filter(this::isFilterObject).forEach(o -> {
            try {
                String json = new ObjectMapper().writeValueAsString(o);
                params.append(json, 0, Math.min(json.length(), 300)).append(" ");
            } catch (Exception e) {
                // do nothing
            }
        });
        logInfo.setParam(params.toString());
    }

    public boolean isFilterObject(final Object o) {
        boolean b = !(o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse);
        return b;
    }
}
