package com.bankofbaku.record;

import com.bankofbaku.record.annotation.*;
import com.bankofbaku.record.annotation.RecordPrefix.PrefixPolicy;
import com.bankofbaku.record.exception.RecordSetFactoryException;
import com.bankofbaku.util.Formater;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RecordSetFactory {

    private static final Logger logger = LoggerFactory.getLogger(RecordSetFactory.class);
    private RecordDescriptor recordDescriptor;

    public <T extends Record> RecordSetFactory(Class<T> clazz) throws RecordSetFactoryException {

        if (clazz == null)
            throw new RecordSetFactoryException("RecordPolicy annotation is not set for record.");

        RecordPolicy recordPolicy = clazz.getAnnotation(RecordPolicy.class);
        if (recordPolicy == null)
            throw new RecordSetFactoryException("RecordPolicy annotation is not set for record.");

        Map<String, RecordItemPolicy> itemPolicies = new HashMap<String, RecordItemPolicy>(10);

        for (RecordItemPolicy itemPolicy : recordPolicy.itemsPolicies()) {
            String itemClassName = itemPolicy.value().getName();

            if (isRecordIgnore(itemPolicy.ignore(), clazz))
                itemPolicies.put(itemClassName, null);
            else
                itemPolicies.put(itemClassName, itemPolicy);
        }
        RecordDescriptor recordDescriptor = new RecordDescriptor(clazz);

        Map<String, RecordItemDescriptor> recordItemDescriptors = new HashMap<String, RecordItemDescriptor>(10);

        for (Field field : clazz.getDeclaredFields()) {
            Class<?> itemClass = field.getType();
            if (IRecordItem.class.isAssignableFrom(itemClass)) {
                RecordItemDescriptor itemDescriptor = new RecordItemDescriptor();
                itemDescriptor.setItemClass(itemClass);
                itemDescriptor.setItemField(field);

                Table table = itemClass.getAnnotation(Table.class);
                if (table != null)
                    itemDescriptor.setTableName(table.name());

                String itemClassName = itemClass.getName();
                if (!recordItemDescriptors.containsKey(itemClassName) && (!itemPolicies.containsKey(itemClassName) || itemPolicies.get(itemClassName) != null)) {

                    String name = "get" + Formater.firstToUpperCase(field.getName());

                    Method recordMethod = null;

                    try {
                        recordMethod = clazz.getMethod(name, new Class<?>[]{});
                        if (recordMethod.getReturnType() != itemClass)
                            throw new Exception("Method return type is not correct.");

                        if (isRecordIgnore(recordMethod.getAnnotation(RecordIgnore.class), clazz))
                            continue;
                        else if (isRecordIgnore(field.getAnnotation(RecordIgnore.class), clazz))
                            continue;
                    } catch (Exception e) {
                        recordMethod = null;
                        logger.debug("Method " + itemClassName + " " + name + "() cannot be found.", e);
                        if (!isRecordIgnore(field.getAnnotation(RecordIgnore.class), clazz))
                            continue;
                    }

                    recordItemDescriptors.put(itemClassName, itemDescriptor);

                    RecordItemPolicy itemPolicy = itemPolicies.get(itemClassName);
                    Map<String, RecordField> fields = new HashMap<String, RecordField>();

                    String defaultPrefix = "";
                    if (itemPolicy != null) {
                        for (RecordField recordItemField : itemPolicy.fields()) {
                            String fieldName = recordItemField.name();
                            if (!fieldName.equals(""))
                                fields.put(recordItemField.name(), recordItemField);
                        }

                        RecordPrefix rootRecordPrefix = itemPolicy.prefix();

                        if (rootRecordPrefix.policy() == PrefixPolicy.DEFAULT)
                            defaultPrefix = getDefaultPrefix(recordPolicy.defaultPrefix(), itemClass, field);

                        else
                            defaultPrefix = getFieldPrefix("", itemPolicy.prefix(), clazz);
                    } else
                        defaultPrefix = getDefaultPrefix(recordPolicy.defaultPrefix(), itemClass, field);

                    Map<String, Field> itemFields = new HashMap<String, Field>();

                    for (Field itemField : itemClass.getDeclaredFields())
                        itemFields.put(itemField.getName(), itemField);

                    Map<String, FieldDescriptor> itemFieldDescriptors = new HashMap<String, FieldDescriptor>(10);
                    String idFieldName = null;

                    for (Method itemMethod : itemClass.getMethods()) {
                        if (Modifier.isPublic(itemMethod.getModifiers())) {
                            String imname = itemMethod.getName(), fieldName = null, mehtodPrefix = null;
                            FieldDescriptor fieldDescriptor = null;
                            if (imname.length() > 3 && (imname.startsWith("get") || imname.startsWith("set"))) {
                                fieldName = Formater.firstToLowerCase(imname.substring(3));
                                mehtodPrefix = imname.substring(0, 3);
                            } else if (imname.length() > 2 && imname.startsWith("is")) {
                                fieldName = Formater.firstToLowerCase(imname.substring(2));
                                mehtodPrefix = imname.substring(0, 2);
                            } else
                                continue;

                            Id id = itemMethod.getAnnotation(Id.class);
                            if (id != null)
                                idFieldName = fieldName;

                            fieldDescriptor = itemFieldDescriptors.get(fieldName);
                            if (fieldDescriptor == null) {
                                fieldDescriptor = new FieldDescriptor(itemDescriptor);
                                fieldDescriptor.setFieldName(fieldName);
                                itemFieldDescriptors.put(fieldName, fieldDescriptor);
                            }

                            if (mehtodPrefix.equals("set")) {
                                FastMethod fastGetMethod = fieldDescriptor.getGetMethod();
                                if (fastGetMethod == null || itemMethod.getParameterTypes()[0] == fastGetMethod.getReturnType()) {
                                    fieldDescriptor.setSetMethod(itemDescriptor.getItemClass().getMethod(itemMethod));
                                }

                                continue;
                            } else {

                                Class<?> fieldClass = itemMethod.getReturnType();
                                fieldDescriptor.setFieldClass(fieldClass);

                                FastMethod fastSetMethod = fieldDescriptor.getSetMethod();

                                if (fastSetMethod != null && fastSetMethod.getParameterTypes()[0] != fieldClass)
                                    continue;

                                FastMethod fastGetMethod = itemDescriptor.getItemClass().getMethod(itemMethod);

                                fieldDescriptor.setGetMethod(fastGetMethod);

                                fillFieldDescriptor(fieldDescriptor, itemMethod, clazz);

                                if (fieldDescriptor.isIgnore() == Boolean.TRUE)
                                    continue;
                                else {
                                    Field itemField = itemFields.get(fieldDescriptor.getFieldName());

                                    if (itemField != null) {
                                        fillFieldDescriptor(fieldDescriptor, itemField, clazz);
                                        if (fieldDescriptor.isIgnore() == Boolean.TRUE)
                                            continue;
                                    }

                                    fillFieldDescriptor(fieldDescriptor, recordMethod, clazz);
                                    if (fieldDescriptor.isIgnore() == Boolean.TRUE)
                                        continue;

                                    fillFieldDescriptor(fieldDescriptor, field, clazz);
                                    if (fieldDescriptor.isIgnore() == Boolean.TRUE)
                                        continue;

                                    RecordField recordField = fields.get(fieldDescriptor.getFieldName());

                                    if (recordField != null) {
                                        if (isRecordIgnore(recordField.ignore(), clazz)) {
                                            fieldDescriptor.setIgnore(true);
                                            continue;
                                        }

                                        fieldDescriptor.setPrefix(getFieldPrefix(null, recordField.prefix(), clazz));
                                        fieldDescriptor.setIgnorePrefix(recordField.rename().ignorePrefix());
                                        fieldDescriptor.setRename(recordField.rename().value());
                                    }

                                    fieldDescriptor.setPrefix(defaultPrefix);
                                    fieldDescriptor.setIgnorePrefix(false);
                                }

                                fieldDescriptor.setIgnore(false);
                            }
                        }
                    }

                    Map<String, FieldDescriptor> aliasFieldDescriptors = new HashMap<String, FieldDescriptor>(10);
                    for (String fieldName : itemFieldDescriptors.keySet()) {
                        FieldDescriptor fieldDescriptor = itemFieldDescriptors.get(fieldName);
                        if (fieldDescriptor.getGetMethod() != null && fieldDescriptor.getSetMethod() != null && fieldDescriptor.isIgnore() != Boolean.TRUE) {
                            String alias = fieldDescriptor.getAlias();
                            if (!aliasFieldDescriptors.containsKey(alias))
                                aliasFieldDescriptors.put(alias, fieldDescriptor);
                            else
                                throw new RecordSetFactoryException("Founded duplicate field alias.");
                        }
                    }

                    if (idFieldName != null)
                        itemDescriptor.setIdDescriptor(itemFieldDescriptors.get(idFieldName));

                    itemDescriptor.setMapFieldDescriptors(aliasFieldDescriptors);
                }
            }
        }

        try {
            recordDescriptor.setRecordItemDescriptors(recordItemDescriptors.values());
            this.recordDescriptor = recordDescriptor;
        } catch (Exception e) {
            this.recordDescriptor = null;
            throw new RecordSetFactoryException(e);
        }
    }

    public RecordSet create() {
        return create(false);
    }

    public RecordSet create(boolean isReadOnly) {
        return create(isReadOnly, null);
    }

    public RecordSet create(boolean isReadOnly, ICrud crud) {
        return new RecordSet(recordDescriptor, isReadOnly, crud);
    }

    public RecordSet create(ICrud crud) {
        return create(false, crud);
    }

    private void fillFieldDescriptor(FieldDescriptor descriptor, AnnotatedElement member, Class<? extends Record> recordClass) {

        RecordIgnore ignore = member.getAnnotation(RecordIgnore.class);

        if (ignore != null && descriptor.setIgnore(isRecordIgnore(ignore, recordClass)))
            return;

        RecordPrefix prefix = member.getAnnotation(RecordPrefix.class);

        if (prefix != null)
            descriptor.setPrefix(getFieldPrefix(null, prefix, recordClass));

        FieldRename fieldRename = member.getAnnotation(FieldRename.class);

        if (fieldRename != null) {
            String rename = rename(fieldRename, recordClass);
            if (rename != null) {
                descriptor.setRename(rename);
                if (descriptor.isIgnorePrefix() == null)
                    descriptor.setIgnorePrefix(fieldRename.ignorePrefix());
            }
        }

        RecordField recordField = (RecordField) member.getAnnotation(RecordField.class);

        if (recordField != null) {
            if (descriptor.isIgnore() == null && descriptor.setIgnore(isRecordIgnore(recordField.ignore(), recordClass)))
                return;

            if (descriptor.getPrefix() == null)
                descriptor.setPrefix(getFieldPrefix(null, recordField.prefix(), recordClass));

            if (descriptor.getRename() == null) {
                String rename = rename(recordField.rename(), recordClass);
                if (rename != null) {
                    descriptor.setRename(rename);
                    if (descriptor.isIgnorePrefix() == null)
                        descriptor.setIgnorePrefix(recordField.rename().ignorePrefix());
                }
            }
        }
    }

    public Set<String> getAliases() {
        return recordDescriptor.getAliases();
    }

    private String getDefaultPrefix(RecordPolicy.DefaultPrefix defaultPrefix, Class<?> itemClass, Field field) {
        switch (defaultPrefix) {
            case ITEMCLASSNAME:
                return itemClass.getSimpleName();
            case ITEMNAME:
                return Formater.firstToUpperCase(field.getName());
            default:
                return "";
        }
    }

    private String getFieldPrefix(String rootPrefix, RecordPrefix prefix, Class<? extends Record> iRecordClass) {
        PrefixPolicy prefixPolicy = prefix.policy();

        if (prefixPolicy == PrefixPolicy.INHERIT)
            return rootPrefix;
        else if (prefixPolicy == PrefixPolicy.REWRITE)
            if (prefix.classes().length != 0) {
                for (Class<? extends Record> clazz : prefix.classes())
                    if (clazz == iRecordClass)
                        return prefix.value();
            } else
                return prefix.value();

        return null;
    }

    public RecordDescriptor getRecordDescriptor() {
        return this.recordDescriptor;
    }

    private boolean isRecordIgnore(RecordIgnore ignore, Class<? extends Record> recordClass) {
        if (ignore != null) {
            Class<? extends Record>[] classes = ignore.classes();
            if (classes.length != 0) {
                if (recordClass != null)
                    for (Class<? extends Record> clazz : classes) {
                        if (clazz == recordClass)
                            return ignore.value();
                    }
            } else
                return ignore.value();

            return !ignore.value();
        } else
            return false;
    }

    private String rename(FieldRename columnRename, Class<? extends Record> iRecordClass) {
        if (columnRename.rename()) {
            Class<? extends Record>[] classes = columnRename.classes();
            if (classes.length != 0) {
                if (iRecordClass != null)
                    for (Class<? extends Record> clazz : classes) {
                        if (clazz == iRecordClass)
                            return columnRename.value();
                    }
            } else
                return columnRename.value();
        }

        return null;
    }
}
