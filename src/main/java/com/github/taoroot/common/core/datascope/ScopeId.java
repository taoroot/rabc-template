package com.github.taoroot.common.core.datascope;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface ScopeId {

    ScopeType value() default ScopeType.OWN;

}
