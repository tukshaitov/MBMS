package com.bankofbaku.record.annotation;

import com.bankofbaku.record.Record;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
public @interface RecordPrefix {
    String value() default "";

    Class<? extends Record>[] classes() default {};

    PrefixPolicy policy() default PrefixPolicy.REWRITE;

    enum PrefixPolicy {
        DEFAULT, REWRITE, INHERIT
    }
}
