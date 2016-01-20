package com.bankofbaku.record;

import com.bankofbaku.util.CustomDateSerializer;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;
import java.util.Date;

public class RecordJsonSerializer extends JsonSerializer<Record> {
    private final static CustomDateSerializer CUSTOM_DATE_SERIALIZER = new CustomDateSerializer();

    @Override
    public void serialize(Record record, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {

        jgen.writeStartObject();
        try {
            jgen.writeObjectField("id", record.getRecordId());

            RecordDescriptor recordDescriptor = record.getRecordDescriptor();
            for (String alias : recordDescriptor.getAliases()) {
                Object value = record.getField(alias);
                jgen.writeFieldName(alias);
                if (value instanceof Date)
                    CUSTOM_DATE_SERIALIZER.serialize((Date) value, jgen, provider);
                else
                    jgen.writeObject(value);
            }

        } catch (Exception e) {

        }
        jgen.writeEndObject();
    }
}
