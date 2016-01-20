package com.bankofbaku.record.annotation;

import com.bankofbaku.record.IRecordItem;
import com.bankofbaku.record.annotation.RecordPrefix.PrefixPolicy;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RecordItemPolicy {
    Class<? extends IRecordItem> value();

    RecordPrefix prefix() default @RecordPrefix(policy = PrefixPolicy.DEFAULT);

    RecordField[] fields() default {};

    RecordIgnore ignore() default @RecordIgnore(false);
}
