package com.bankofbaku.util;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import java.io.IOException;

public class GridIsProductDeserializer extends JsonDeserializer<GridIsProduct> {

    @Override
    public GridIsProduct deserialize(JsonParser jp, DeserializationContext context) throws IOException, JsonProcessingException {

        GridIsProduct gridIsProduct = new GridIsProduct(true);

        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String fieldname = jp.getCurrentName();
            if (fieldname.equals("isProduct")) {
                jp.nextToken();
                gridIsProduct.setProduct(Boolean.parseBoolean(jp.getText()));
            }
        }

        return gridIsProduct;
    }
}