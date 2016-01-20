package com.bankofbaku.record;

import com.bankofbaku.record.annotation.RecordPolicy;
import com.bankofbaku.record.annotation.RecordPolicy.DefaultPrefix;
import com.bankofbaku.record.exception.FieldGetException;
import com.bankofbaku.record.exception.FieldSetException;
import com.bankofbaku.record.exception.RecordCloneException;
import com.bankofbaku.record.exception.RecordFillException;
import com.bankofbaku.util.IResponse;
import net.sf.cglib.reflect.FastMethod;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

@JsonSerialize(using = RecordJsonSerializer.class)
@RecordPolicy(defaultPrefix = DefaultPrefix.ITEMNAME)
public abstract class Record implements IResponse {

    public static enum RecordStatus {
        DELETED, DIRTY, NEW, READONLY, SERIALIZED
    }

    private static final Logger logger = LoggerFactory.getLogger(Record.class);
    private static final long serialVersionUID = -4273032765125961792L;
    private Set<String> dirties = new HashSet<String>();
    private RecordDescriptor recordDescriptor;
    private int recordId;
    private int version;
    private RecordStatus recordStatus = RecordStatus.NEW;
    private Record sourceRecord;
    private RecordSet ownerRecordSet;

    public <T extends IRecordItem> Object getItemId(Class<T> itemClass) {
        try {
            String itemClassName = itemClass.getName();
            RecordItemDescriptor itemDescriptor = recordDescriptor.getByItemClassName(itemClassName);
            return itemDescriptor.getIdDescriptor().invokeGetMethod((T) itemDescriptor.getItemField().get(this));
        } catch (Exception e) {
            throw new FieldGetException(e);
        }
    }

    @Override
    public Record clone() {
        try {
            return (Record) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Record deepClone() throws RecordCloneException {
        try {
            Record clone = (Record) super.clone();

            for (Class<?> obj = clone.getClass(); !obj.equals(Object.class); obj = obj.getSuperclass()) {
                Field[] fields = obj.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    if (Modifier.isFinal(fields[i].getModifiers()))
                        continue;

                    fields[i].setAccessible(true);
                    Object o = fields[i].get(clone);
                    if (o instanceof IRecordItem)
                        fields[i].set(clone, ((IRecordItem) o).clone());
                }
            }
            return clone;
        } catch (IllegalAccessException | IllegalArgumentException | CloneNotSupportedException e) {
            throw new RecordCloneException(e);
        }
    }

    public final void fill(IRecordItem... recordItems) {
        Map<String, IRecordItem> backupRecordItems = new HashMap<String, IRecordItem>(10);
        Map<String, IRecordItem> backupSourceRecordItems = new HashMap<String, IRecordItem>(10);
        Map<String, IRecordItem> recognizedRecordItems = new HashMap<String, IRecordItem>(10);

        try {
            for (IRecordItem recordItem : recordItems) {
                Class<?> clazz = recordItem.getClass();
                String recordItemClassName = clazz.getName();
                RecordItemDescriptor itemDescriptor = recordDescriptor.getByItemClassName(clazz.getName());
                if (itemDescriptor == null) {
                    while (true) {
                        clazz = clazz.getSuperclass();
                        if (IRecordItem.class.isAssignableFrom(clazz)) {
                            recordItemClassName = clazz.getName();
                            itemDescriptor = recordDescriptor.getByItemClassName(clazz.getName());
                            if (itemDescriptor != null)
                                break;
                        } else
                            break;
                    }
                }

                recognizedRecordItems.put(recordItemClassName, recordItem);

                Field itemField = itemDescriptor.getItemField();
                itemField.setAccessible(true);

                backupRecordItems.put(recordItemClassName, (IRecordItem) itemField.get(sourceRecord));
                backupSourceRecordItems.put(recordItemClassName, (IRecordItem) itemField.get(this));

                itemField.set(this, recordItem);
                itemField.set(sourceRecord, recordItem.clone());
            }

            for (String recordItemClassName : recognizedRecordItems.keySet()) {
                RecordItemDescriptor itemDescriptor = recordDescriptor.getByItemClassName(recordItemClassName);
                FieldDescriptor idDescriptor = itemDescriptor.getIdDescriptor();
                IRecordItem recordItem = recognizedRecordItems.get(recordItemClassName);

                if (idDescriptor != null && idDescriptor.getGetMethod() != null && idDescriptor.getSetMethod() != null
                        && (idDescriptor.invokeGetMethod(recordItem) == null || (Integer) idDescriptor.invokeGetMethod(recordItem) == 0)) {

                    idDescriptor.invokeSetMethod(recordItem, ownerRecordSet.generateId(itemDescriptor));
                }

                dirties.removeAll(itemDescriptor.getMapFieldDescriptors().keySet());
            }

            if (sourceRecord.recordStatus == RecordStatus.SERIALIZED && this.recordStatus == RecordStatus.DIRTY) {
                if (dirties.size() == 0)
                    recordStatus = RecordStatus.SERIALIZED;
            }
        } catch (Exception e) {
            e.printStackTrace();
            for (String className : backupRecordItems.keySet()) {
                try {
                    Field itemField = recordDescriptor.getByItemClassName(className).getItemField();
                    itemField.set(this, backupRecordItems.get(className));
                    itemField.set(sourceRecord, backupRecordItems.get(className));
                } catch (IllegalArgumentException | IllegalAccessException e1) {
                    logger.debug("Items cannot be restore successful for " + this.getClass().getName());
                }
            }
            logger.debug("Method fill cannot be complete successful for " + this.getClass().getName());
            throw new RecordFillException(e);
        }
    }

    public Object getField(String alias) {
        try {
            FieldDescriptor fieldDescriptor = recordDescriptor.getByItemFieldAlias(alias);
            Field itemField = fieldDescriptor.getRecordItemDescriptor().getItemField();
            return fieldDescriptor.getGetMethod().invoke(itemField.get(this), new Object[]{});
        } catch (Exception e) {
            throw new FieldGetException(e);
        }
    }

    RecordDescriptor getRecordDescriptor() {
        return this.recordDescriptor;
    }

    public final int getRecordId() {
        return recordId;
    }

    public final <T extends IRecordItem> T getRecordItem(Class<T> itemClass) {
        try {
            String itemClassName = itemClass.getName();
            RecordItemDescriptor itemDescriptor = recordDescriptor.getByItemClassName(itemClassName);
            return (T) itemDescriptor.getItemField().get(this);
        } catch (Exception e) {
            throw new FieldGetException(e);
        }
    }

    public final IRecordItem[] getRecordItems() {
        IRecordItem[] recordItems = null;
        try {
            List<RecordItemDescriptor> itemDescriptors = recordDescriptor.getItemDescriptors();
            recordItems = new IRecordItem[itemDescriptors.size()];

            for (int i = 0; i < itemDescriptors.size(); i++)
                recordItems[i] = (IRecordItem) itemDescriptors.get(i).getItemField().get(this);

            return recordItems;
        } catch (Exception e) {
            throw new FieldGetException(e);
        }
    }

    public RecordStatus getRecordStatus() {
        return recordStatus;
    }

    public void restore() {
        if (sourceRecord != null)
            fill(getRecordItems());
    }

    public void rewrite() {
        dirties = new HashSet<String>();
        sourceRecord = deepClone();
        if (sourceRecord != null) {
            sourceRecord.sourceRecord = null;
            sourceRecord.dirties = null;
        }
    }

    public final boolean setField(String alias, Object value) throws InvocationTargetException {
        try {

            FieldDescriptor fieldDescriptor = recordDescriptor.getByItemFieldAlias(alias);
            Field itemField = fieldDescriptor.getRecordItemDescriptor().getItemField();
            IRecordItem item = (IRecordItem) itemField.get(this), sourceItem = null;

            boolean isSet = trySetItemField(fieldDescriptor, item, value);

            Object itemValue = null, sourceItemValue = null;

            if (item != null)
                itemValue = fieldDescriptor.invokeGetMethod(item);

            sourceItem = (IRecordItem) itemField.get(sourceRecord);

            if (sourceItem != null)
                sourceItemValue = fieldDescriptor.invokeGetMethod(sourceItem);

            String dirtyKey = fieldDescriptor.getAlias();

            if (sourceRecord.recordStatus == RecordStatus.SERIALIZED) {
                if (itemValue == sourceItemValue || (itemValue != null && itemValue.equals(sourceItemValue))) {
                    if (dirties.remove(dirtyKey) && dirties.size() == 0)
                        recordStatus = RecordStatus.SERIALIZED;
                } else {
                    dirties.add(dirtyKey);
                    recordStatus = RecordStatus.DIRTY;
                }
            }

            if (isSet)
                version = ownerRecordSet.getIncrementedVersion();

            return isSet;
        } catch (Exception e) {
            throw new FieldSetException(e);
        }

    }

    void setRecordId(int id) {
        this.recordId = id;
    }

    public void setRecordStatus(RecordStatus recordStatus) {
        this.recordStatus = recordStatus;
    }

    void setup(int recordId, RecordSet ownerRecordSet, RecordDescriptor recordDescriptor, IRecordItem... recordItems) {
        this.setup(recordId, ownerRecordSet, recordDescriptor, RecordStatus.NEW, recordItems);
    }

    void setup(int recordId, RecordSet ownerRecordSet, RecordDescriptor recordDescriptor, RecordStatus recordStatus, IRecordItem... recordItems) {
        this.recordId = recordId;
        this.recordDescriptor = recordDescriptor;
        this.recordStatus = recordStatus;
        this.ownerRecordSet = ownerRecordSet;
        sourceRecord = clone();
        if (sourceRecord != null) {
            sourceRecord.sourceRecord = null;
            sourceRecord.dirties = null;
        }

        fill(recordItems);
    }

    protected boolean trySetItemField(FieldDescriptor fieldDescriptor, Object obj, Object value) throws InvocationTargetException {

        FastMethod setMethod = fieldDescriptor.getSetMethod();
        Object currentValue = fieldDescriptor.getGetMethod().invoke(obj, new Object[]{});

        if (value == null) {
            setMethod.invoke(obj, new Object[]{null});
            return currentValue == null ? false : true;
        }

        Class<?> valueClass = value.getClass();
        Class<?> fieldClass = fieldDescriptor.getFieldClass();

        if (fieldClass.isInstance(value)) {
            setMethod.invoke(obj, new Object[]{value});
            return currentValue == null ? (value == null ? false : true) : !currentValue.equals(value);
        }

        if (valueClass == String.class) {
            if (fieldClass == Byte.TYPE) {
                Byte bvalue = Byte.parseByte((String) value);
                setMethod.invoke(obj, new Object[]{bvalue});
                return currentValue == null ? true : !currentValue.equals(bvalue);
            } else if (fieldClass == Short.TYPE || fieldClass == Short.class) {
                Short svalue = Short.parseShort((String) value);
                setMethod.invoke(obj, new Object[]{svalue});
                return currentValue == null ? true : !currentValue.equals(svalue);
            } else if (fieldClass == Character.TYPE || fieldClass == Character.class) {
                String svalue = (String) value;
                if (svalue.length() != 1)
                    throw new ClassCastException();

                Character cvalue = svalue.charAt(0);
                setMethod.invoke(obj, new Object[]{cvalue});
                return currentValue == null ? true : !currentValue.equals(cvalue);
            } else if (fieldClass == Integer.TYPE || fieldClass == Integer.class) {
                Integer ivalue = Integer.parseInt((String) value);
                setMethod.invoke(obj, new Object[]{ivalue});
                return currentValue == null ? true : !currentValue.equals(ivalue);
            } else if (fieldClass == Long.TYPE || fieldClass == Long.class) {
                Long lvalue = Long.parseLong((String) value);
                setMethod.invoke(obj, new Object[]{lvalue});
                return currentValue == null ? true : !currentValue.equals(lvalue);
            } else if (fieldClass == Float.TYPE || fieldClass == Float.class) {
                Float fvalue = Float.parseFloat((String) value);
                setMethod.invoke(obj, new Object[]{fvalue});
                return currentValue == null ? true : !currentValue.equals(fvalue);
            } else if (fieldClass == Double.TYPE || fieldClass == Double.class) {
                Double dvalue = Double.parseDouble((String) value);
                setMethod.invoke(obj, new Object[]{dvalue});
                return currentValue == null ? true : !currentValue.equals(dvalue);
            }
        }
        if (valueClass == Boolean.class) {
            if (fieldClass == Boolean.TYPE) {
                Boolean bvalue = ((Boolean) value).booleanValue();
                setMethod.invoke(obj, new Object[]{bvalue});
                return currentValue == null ? true : !currentValue.equals(bvalue);
            }
        } else if (valueClass == Double.class) {
            if (fieldClass == Double.TYPE) {
                Double dvalue = ((Double) value).doubleValue();
                setMethod.invoke(obj, new Object[]{dvalue});
                return currentValue == null ? true : !currentValue.equals(dvalue);
            }
        } else if (valueClass == Float.class) {
            if (fieldClass == Float.TYPE) {
                Float fvalue = ((Float) value).floatValue();
                setMethod.invoke(obj, new Object[]{fvalue});
                return currentValue == null ? true : !currentValue.equals(fvalue);
            } else if (fieldClass == Double.TYPE || fieldClass == Double.class) {
                Double dvalue = ((Float) value).doubleValue();
                setMethod.invoke(obj, new Object[]{dvalue});
                return currentValue == null ? true : !currentValue.equals(dvalue);
            }
        } else if (valueClass == Long.class) {
            if (fieldClass == Long.TYPE) {
                Long lvalue = ((Long) value).longValue();
                setMethod.invoke(obj, new Object[]{lvalue});
                return currentValue == null ? true : !currentValue.equals(lvalue);
            } else if (fieldClass == Float.TYPE || fieldClass == Float.class) {
                Float fvalue = ((Long) value).floatValue();
                setMethod.invoke(obj, new Object[]{fvalue});
                return currentValue == null ? true : !currentValue.equals(fvalue);
            } else if (fieldClass == Double.TYPE || fieldClass == Double.class) {
                Double dvalue = ((Long) value).doubleValue();
                setMethod.invoke(obj, new Object[]{dvalue});
                return currentValue == null ? true : !currentValue.equals(dvalue);
            }

        } else if (valueClass == Integer.class) {
            if (fieldClass == Integer.TYPE) {
                Integer ivalue = ((Integer) value).intValue();
                setMethod.invoke(obj, new Object[]{ivalue});
                return currentValue == null ? true : !currentValue.equals(ivalue);
            } else if (fieldClass == Long.TYPE || fieldClass == Long.class) {
                Long lvalue = ((Integer) value).longValue();
                setMethod.invoke(obj, new Object[]{lvalue});
                return currentValue == null ? true : !currentValue.equals(lvalue);
            } else if (fieldClass == Float.TYPE || fieldClass == Float.class) {
                Float fvalue = ((Integer) value).floatValue();
                setMethod.invoke(obj, new Object[]{fvalue});
                return currentValue == null ? true : !currentValue.equals(fvalue);
            } else if (fieldClass == Double.TYPE || fieldClass == Double.class) {
                Float fvalue = ((Integer) value).floatValue();
                setMethod.invoke(obj, new Object[]{fvalue});
                return currentValue == null ? true : !currentValue.equals(fvalue);
            }
        } else if (valueClass == Short.class) {
            if (fieldClass == Short.TYPE) {
                Short svalue = ((Short) value).shortValue();
                setMethod.invoke(obj, new Object[]{svalue});
                return currentValue == null ? true : !currentValue.equals(svalue);
            } else if (fieldClass == Integer.TYPE || fieldClass == Integer.class) {
                Integer ivalue = ((Short) value).intValue();
                setMethod.invoke(obj, new Object[]{ivalue});
                return currentValue == null ? true : !currentValue.equals(ivalue);
            } else if (fieldClass == Long.TYPE || fieldClass == Long.class) {
                Long lvalue = ((Short) value).longValue();
                setMethod.invoke(obj, new Object[]{lvalue});
                return currentValue == null ? true : !currentValue.equals(lvalue);
            } else if (fieldClass == Float.TYPE || fieldClass == Float.class) {
                Float fvalue = ((Short) value).floatValue();
                setMethod.invoke(obj, new Object[]{fvalue});
                return currentValue == null ? true : !currentValue.equals(fvalue);
            } else if (fieldClass == Double.TYPE || fieldClass == Double.class) {
                Double dvalue = ((Short) value).doubleValue();
                setMethod.invoke(obj, new Object[]{dvalue});
                return currentValue == null ? true : !currentValue.equals(dvalue);
            }
        } else if (valueClass == Character.class) {
            if (fieldClass == Character.TYPE) {
                Character cvalue = ((Character) value).charValue();
                setMethod.invoke(obj, new Object[]{cvalue});
                return currentValue == null ? true : !currentValue.equals(cvalue);
            } else if (fieldClass == Integer.TYPE || fieldClass == Integer.class) {
                Integer ivalue = (int) ((Character) value).charValue();
                setMethod.invoke(obj, new Object[]{ivalue});
                return currentValue == null ? true : !currentValue.equals(ivalue);
            } else if (fieldClass == Long.TYPE || fieldClass == Long.class) {
                Long lvalue = (long) ((Character) value).charValue();
                setMethod.invoke(obj, new Object[]{lvalue});
                return currentValue == null ? true : !currentValue.equals(lvalue);
            } else if (fieldClass == Float.TYPE || fieldClass == Float.class) {
                Float fvalue = (float) ((Character) value).charValue();
                setMethod.invoke(obj, new Object[]{fvalue});
                return currentValue == null ? true : !currentValue.equals(fvalue);
            } else if (fieldClass == Double.TYPE || fieldClass == Double.class) {
                Double dvalue = (double) ((Character) value).charValue();
                setMethod.invoke(obj, new Object[]{dvalue});
                return currentValue == null ? true : !currentValue.equals(dvalue);
            }
        } else if (valueClass == Byte.class) {
            if (fieldClass == Byte.TYPE) {
                Byte bvalue = ((Byte) value).byteValue();
                setMethod.invoke(obj, new Object[]{bvalue});
                return currentValue == null ? true : !currentValue.equals(bvalue);
            } else if (fieldClass == Short.TYPE || fieldClass == Short.class) {
                Short svalue = ((Byte) value).shortValue();
                setMethod.invoke(obj, new Object[]{svalue});
                return currentValue == null ? true : !currentValue.equals(svalue);
            } else if (fieldClass == Character.TYPE || fieldClass == Character.class) {
                Character cvalue = (char) ((Byte) value).byteValue();

                setMethod.invoke(obj, new Object[]{cvalue});
                return currentValue == null ? true : !currentValue.equals(cvalue);
            } else if (fieldClass == Integer.TYPE || fieldClass == Integer.class) {
                Integer ivalue = ((Byte) value).intValue();
                setMethod.invoke(obj, new Object[]{ivalue});
                return currentValue == null ? true : !currentValue.equals(ivalue);
            } else if (fieldClass == Long.TYPE || fieldClass == Long.class) {
                Long lvalue = ((Byte) value).longValue();
                setMethod.invoke(obj, new Object[]{lvalue});
                return currentValue == null ? true : !currentValue.equals(lvalue);
            } else if (fieldClass == Float.TYPE || fieldClass == Float.class) {
                Float fvalue = ((Byte) value).floatValue();
                setMethod.invoke(obj, new Object[]{fvalue});
                return currentValue == null ? true : !currentValue.equals(fvalue);
            } else if (fieldClass == Double.TYPE || fieldClass == Double.class) {
                Double dvalue = ((Byte) value).doubleValue();
                setMethod.invoke(obj, new Object[]{dvalue});
                return currentValue == null ? true : !currentValue.equals(dvalue);
            }
        }

        throw new ClassCastException();
    }

    public RecordSet getOwnerRecordSet() {
        return ownerRecordSet;
    }

    public void setOwnerRecordSet(RecordSet ownerRecordSet) {
        this.ownerRecordSet = ownerRecordSet;
    }

}
