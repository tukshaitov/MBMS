package com.bankofbaku.util;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import java.io.IOException;

public class GridProductRecordDeserializer extends JsonDeserializer<GridProductRecord> {

    @Override
    public GridProductRecord deserialize(JsonParser jp, DeserializationContext context) throws IOException, JsonProcessingException {

        GridProductRecord gridProductRecord = new GridProductRecord();

        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String fieldname = jp.getCurrentName();
            switch (fieldname) {
                case "id":
                    jp.nextToken();
                    gridProductRecord.setId(Integer.parseInt(jp.getText()));
                    break;
                case "pid":
                    jp.nextToken();
                    gridProductRecord.setParentId(Integer.parseInt(jp.getText()));
                    break;
                case "name":
                    jp.nextToken();
                    gridProductRecord.setName(jp.getText());
                    break;
                case "activated":
                    jp.nextToken();
                    gridProductRecord.setActivated(Formater.deserializeDate(jp.getText()));
                    break;

            }
        }

        return gridProductRecord;
    }
}