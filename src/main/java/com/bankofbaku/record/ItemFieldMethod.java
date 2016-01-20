package com.bankofbaku.record;

import java.lang.reflect.Method;

public final class ItemFieldMethod {
    public String fieldName;
    public Method method;

    public ItemFieldMethod() {
    }

    public ItemFieldMethod(String fieldName, Method method) {
        this.fieldName = fieldName;
        this.method = method;
    }
}