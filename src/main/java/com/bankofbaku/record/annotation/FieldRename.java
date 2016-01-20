package com.bankofbaku.record.annotation;

import com.bankofbaku.record.Record;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
public @interface FieldRename {
    String value();

    boolean rename() default true;

    boolean ignorePrefix() default false;

    Class<? extends Record>[] classes() default {};
}
