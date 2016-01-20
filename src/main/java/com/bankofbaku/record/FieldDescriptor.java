package com.bankofbaku.record;

import java.lang.reflect.InvocationTargetException;

import com.bankofbaku.record.exception.FieldDescriptorCreationException;
import com.bankofbaku.util.Formater;
import net.sf.cglib.reflect.FastMethod;

public class FieldDescriptor {
	private Class<?> fieldClass;
	private String fieldName;
	private FastMethod getMethod;
	private Boolean isIgnore;
	private Boolean isIgnorePrefix;
	private String prefix;
	private String rename;
	private FastMethod setMethod;
	private RecordItemDescriptor itemDescriptor;

	public FieldDescriptor(RecordItemDescriptor itemDescriptor) {
		if (itemDescriptor == null)
			throw new FieldDescriptorCreationException("RecordItemDescriptor parameter is mandatory.");

		this.itemDescriptor = itemDescriptor;
	}

	String getAlias() {
		return Formater.firstToLowerCase((isIgnorePrefix == true || prefix == null ? "" : Formater.firstToLowerCase(prefix))
				+ Formater.firstToUpperCase(rename == null ? fieldName : rename));
	}

	RecordItemDescriptor getRecordItemDescriptor() {
		return itemDescriptor;
	}

	Class<?> getFieldClass() {
		return fieldClass;
	}

	String getFieldName() {
		return fieldName;
	}

	FastMethod getGetMethod() {
		return getMethod;
	}

	String getPrefix() {
		return prefix;
	}

	String getRename() {
		return rename;
	}

	FastMethod getSetMethod() {
		return setMethod;
	}

	Boolean isIgnore() {
		return isIgnore;
	}

	Boolean isIgnorePrefix() {
		return isIgnorePrefix;
	}

	Class<?> setFieldClass(Class<?> fieldClass) {
		this.fieldClass = fieldClass;
		return this.fieldClass;
	}

	String setFieldName(String fieldName) {
		this.fieldName = fieldName;
		return this.fieldName;
	}

	void setGetMethod(FastMethod getMethod) {
		this.getMethod = getMethod;
	}

	Boolean setIgnore(Boolean isIgnore) {
		this.isIgnore = isIgnore;
		return this.isIgnore;
	}

	Boolean setIgnorePrefix(Boolean isIgnorePrefix) {
		if (this.isIgnorePrefix == null)
			this.isIgnorePrefix = isIgnorePrefix;
		return this.isIgnorePrefix;
	}

	String setPrefix(String prefix) {
		if (this.prefix == null)
			this.prefix = Formater.firstToLowerCase(prefix);

		return this.prefix;
	}

	String setRename(String rename) {
		if (this.rename == null)
			this.rename = Formater.firstToLowerCase(rename);
		return this.rename;
	}

	void setSetMethod(FastMethod setMethod) {
		this.setMethod = setMethod;
	};

	Object invokeGetMethod(Object obj) throws InvocationTargetException {
		if (getMethod != null)
			return getMethod.invoke(obj, new Object[] {});
		return null;
	}

	void invokeSetMethod(Object obj, Object... args) throws InvocationTargetException {
		if (setMethod != null)
			setMethod.invoke(obj, args);
	}
}
