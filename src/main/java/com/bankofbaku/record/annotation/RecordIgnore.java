package com.bankofbaku.record.annotation;

import com.bankofbaku.record.Record;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
public @interface RecordIgnore {
    boolean value() default true;

    Class<? extends Record>[] classes() default {};
}
