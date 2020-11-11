package com.github.taoroot.common.core.datascope;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableScope {
    String scopeOwnName() default "user_id";

    DataScopeTypeEnum value() default DataScopeTypeEnum.ALL;
}
