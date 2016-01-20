package com.bankofbaku.record.annotation;

import com.bankofbaku.record.annotation.RecordPrefix.PrefixPolicy;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
public @interface RecordField {
    String name() default "";

    FieldRename rename() default @FieldRename(value = "", rename = false);

    RecordIgnore ignore() default @RecordIgnore(false);

    RecordPrefix prefix() default @RecordPrefix(policy = PrefixPolicy.INHERIT);
}
