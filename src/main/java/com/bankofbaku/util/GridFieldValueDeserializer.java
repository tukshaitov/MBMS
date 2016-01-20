package com.bankofbaku.util;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import java.io.IOException;

public class GridFieldValueDeserializer extends JsonDeserializer<GridFieldValue> {

    @Override
    public GridFieldValue deserialize(JsonParser jp, DeserializationContext context) throws IOException, JsonProcessingException {

        GridFieldValue fieldValue = new GridFieldValue();

        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String fieldname = jp.getCurrentName();
            if (fieldname.equals("id")) {
                if (fieldValue.getId() == null) {
                    jp.nextToken();
                    fieldValue.setId(Integer.parseInt(jp.getText()));
                } else
                    throw new RuntimeException("Founded more than one id field.");
            } else {
                if (fieldValue.getFieldName() == null) {
                    jp.nextToken();
                    fieldValue.setFieldName(fieldname);
                    String value = jp.getText();
                    if (value.equals("null"))
                        value = null;
                    fieldValue.setValue(value);
                } else
                    throw new RuntimeException("Founded more than one record field.");
            }
        }

        return fieldValue;
    }
}