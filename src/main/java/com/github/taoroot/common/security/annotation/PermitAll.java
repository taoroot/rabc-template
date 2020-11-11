package com.github.taoroot.common.security.annotation;

import java.lang.annotation.*;

/**
 * 免鉴权
 *
 * @author zhiyi
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PermitAll {

    /**
     * 默认全局无需鉴权,  如果改成 false, 则代表只允许内部
     */
    boolean global() default true;
}
