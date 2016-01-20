package com.bankofbaku.record.annotation;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RecordPolicy {
    RecordItemPolicy[] itemsPolicies() default {};

    DefaultPrefix defaultPrefix() default DefaultPrefix.NONE;

    boolean ignoreResponse() default true;

    enum DefaultPrefix {
        ITEMCLASSNAME, ITEMNAME, NONE
    }

    ;
}
