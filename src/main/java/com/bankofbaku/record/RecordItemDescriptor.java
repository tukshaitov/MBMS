package com.bankofbaku.record;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.bankofbaku.record.exception.FieldGetException;
import com.bankofbaku.record.exception.FieldNotFoundException;
import com.bankofbaku.record.exception.FieldSetException;
import net.sf.cglib.reflect.FastClass;

public class RecordItemDescriptor {
	private Map<String, FieldDescriptor> fieldDescriptors = new HashMap<String, FieldDescriptor>(10);
	private FastClass itemClass;
	private Field itemField;
	private String tableName;
	private FieldDescriptor idDescriptor;

	public FieldDescriptor getIdDescriptor() {
		return idDescriptor;
	}

	public void setIdDescriptor(FieldDescriptor idDescriptor) {
		this.idDescriptor = idDescriptor;
	}

	public Map<String, FieldDescriptor> getMapFieldDescriptors() {
		return fieldDescriptors;
	}

	public void setMapFieldDescriptors(Map<String, FieldDescriptor> mapFieldDescriptors) {
		this.fieldDescriptors = mapFieldDescriptors;
	}

	public FastClass getItemClass() {
		return itemClass;
	}

	void setItemClass(FastClass itemClass) {
		this.itemClass = itemClass;
	}

	void setItemClass(Class<?> itemClass) {
		this.itemClass = FastClass.create(itemClass);
	}

	public Field getItemField() {
		return itemField;
	}

	void setItemField(Field itemField) {
		this.itemField = itemField;
	}

	Object invokeGetMethod(String alias, Object obj) {
		FieldDescriptor fieldDescriptor = fieldDescriptors.get(alias);
		if (fieldDescriptor == null)
			throw new FieldNotFoundException("Alias " + alias + " cannot be found for " + itemClass.getName());
		else
			try {
				return fieldDescriptor.invokeGetMethod(obj);
			}
			catch (InvocationTargetException e) {
				throw new FieldGetException(e);
			}
	}

	void invokeSetMethod(String alias, Object obj, Object... args) {
		FieldDescriptor fieldDescriptor = fieldDescriptors.get(alias);
		if (fieldDescriptor == null)
			throw new FieldNotFoundException("Alias " + alias + " cannot be found for " + itemClass.getName());
		else
			try {
				fieldDescriptor.invokeSetMethod(obj, args);
			}
			catch (InvocationTargetException e) {
				throw new FieldSetException(e);
			}
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}
