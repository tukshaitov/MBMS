package com.bankofbaku.util;

import com.bankofbaku.bean.ApplicationContextProvider;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.io.IOException;

public class I18nSerializer extends JsonSerializer<String> {

    private ApplicationContext applicationContext;

    public I18nSerializer() {
        this.applicationContext = ApplicationContextProvider
                .getApplicationContext();
    }

    @Override
    public void serialize(String value, JsonGenerator jgen,
                          SerializerProvider provider) throws IOException,
            JsonProcessingException {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();

        jgen.writeString(applicationContext.getMessage(value, null, value,
                RequestContextUtils.getLocale(attributes.getRequest())));

    }
}
