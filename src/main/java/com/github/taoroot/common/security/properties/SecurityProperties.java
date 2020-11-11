package com.github.taoroot.common.security.properties;

import com.github.taoroot.common.security.annotation.PermitAll;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author : zhiyi
 * Date: 2020/2/17
 */
@Log4j2
@Data
@ConfigurationProperties(prefix = "security")
public class SecurityProperties implements InitializingBean {
    /**
     * 开放接口
     */
    private List<String> permitAllUrls = new ArrayList<>();

    /**
     * token参数
     */
    private Map<String, TokenConfig> token = new HashMap<>();

    private Boolean captcha = false;

    @Resource
    private WebApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() {
        // 一些默认开放的接口
        permitAllUrls.addAll(Arrays.asList(
                "/swagger-ui.html",
                "/swagger-ui/",
                "/v2/**",
                "/swagger-resources/**",
                "/webjars/**",
                "/resources/**",
                "/**/*.html",
                "/**/*.htm",
                "/**/*.js",
                "/**/*.png",
                "/**/*.jpeg",
                "/**/*.jpg",
                "/**/*.ttf",
                "/**/*.woff2",
                "/**/*.woff",
                "/actuator/health",
                "/**/*.css")
        );
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

        // 收集 PermitAll 注解的接口
        for (RequestMappingInfo info : map.keySet()) {
            HandlerMethod handlerMethod = map.get(info);
            PermitAll annotation = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), PermitAll.class);
            if (annotation == null) {
                annotation = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), PermitAll.class);
            }
            if (annotation != null) {
                for (RequestMethod method : info.getMethodsCondition().getMethods()) {
                    String[] urls = info.getPatternsCondition().getPatterns().toArray(new String[0]);
                    for (String url : urls) {
                        permitAllUrls.add(url + ":" + method.name());
                    }
                }
            }
        }

        log.info("permit all urls: {}", permitAllUrls);
    }

    /**
     * Token 配置
     */
    @Data
    public static class TokenConfig {
        /**
         * 盐
         */
        private String secret = "secretsecretsecretsecretsecretsecret";
        /**
         * 过期时常
         */
        private Long expire = (long) (1000 * 60 * 60 * 24);
    }
}
