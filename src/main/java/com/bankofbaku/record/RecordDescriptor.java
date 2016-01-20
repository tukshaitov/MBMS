package com.bankofbaku.record;

import com.bankofbaku.record.exception.RecordDescriptorCreationExcetption;
import net.sf.cglib.reflect.FastClass;

import java.util.*;

public class RecordDescriptor {

    private Map<String, FieldDescriptor> fieldDescriptors = new HashMap<String, FieldDescriptor>(10);
    private Map<String, RecordItemDescriptor> itemDescriptors = new HashMap<String, RecordItemDescriptor>(10);
    private FastClass recordClass;

    RecordDescriptor(Class<? extends Record> clazz) {
        super();
        if (clazz == null)
            throw new RecordDescriptorCreationExcetption();

        this.recordClass = FastClass.create(clazz);
    }

    RecordDescriptor(FastClass recordClass) {
        super();
        if (recordClass == null)
            throw new RecordDescriptorCreationExcetption();

        this.recordClass = recordClass;
    }

    public Record createRecord() {
        return null;
    }

    public RecordSet createRecordSet() {
        return createRecordSet(false);
    }

    public RecordSet createRecordSet(boolean isReadOnly) {
        return createRecordSet(isReadOnly, null);
    }

    public RecordSet createRecordSet(boolean isReadOnly, ICrud crud) {
        return new RecordSet(this, isReadOnly, crud);
    }

    public RecordSet createRecordSet(ICrud crud) {
        return createRecordSet(false, crud);
    }

    public Set<String> getAliases() {
        return fieldDescriptors.keySet();
    }

    public RecordItemDescriptor getByItemClassName(String className) {
        return itemDescriptors.get(className);
    }

    public FieldDescriptor getByItemFieldAlias(String alias) {
        return fieldDescriptors.get(alias);
    }

    FastClass getRecordClass() {
        return recordClass;
    }

    void setRecordItemDescriptors(Collection<RecordItemDescriptor> descriptors) {
        itemDescriptors = new HashMap<String, RecordItemDescriptor>(10);
        fieldDescriptors = new HashMap<String, FieldDescriptor>(10);
        for (RecordItemDescriptor itemDescriptor : descriptors) {
            itemDescriptors.put(itemDescriptor.getItemClass().getName(), itemDescriptor);
            Map<String, FieldDescriptor> map = itemDescriptor.getMapFieldDescriptors();

            for (String alias : map.keySet())
                if (fieldDescriptors.containsKey(alias))
                    throw new RuntimeException("Founded duplicate field alias : " + alias);
                else
                    fieldDescriptors.put(alias, map.get(alias));
        }
    }

    public List<RecordItemDescriptor> getItemDescriptors() {
        return new ArrayList<RecordItemDescriptor>(itemDescriptors.values());
    }

}
