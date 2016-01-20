package com.bankofbaku.formdata;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FormItemRelation {
    public static enum Operation {
        MODIFYFROM, DELETEFROM, UPDATETO, CREATETO
    }

    public static enum Type {
        INNER, OUTER
    }

    private Set<FormItemField> fields = new HashSet<FormItemField>();
    private FormItem targetItem;
    private Operation operation;

    public FormItemRelation(FormItem targetItem, Operation operation, FormItemField... fields) {
        this.targetItem = targetItem;
        this.operation = operation;
        this.fields = new HashSet<FormItemField>(Arrays.asList(fields));
    }

    public boolean addField(FormItemField field) {
        return fields.add(field);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FormItemRelation other = (FormItemRelation) obj;
        if (targetItem == null) {
            if (other.targetItem != null)
                return false;
        } else if (!targetItem.equals(other.targetItem))
            return false;
        if (operation != other.operation)
            return false;
        return true;
    }

    public Set<FormItemField> getFields() {
        return fields;
    }

    public FormItem getFormItem() {
        return targetItem;
    }

    public Operation getOperation() {
        return operation;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((targetItem == null) ? 0 : targetItem.hashCode());
        result = prime * result + ((operation == null) ? 0 : operation.hashCode());
        return result;
    }

    public boolean removeField(FormItemField field) {
        return fields.remove(field);
    }

    public void setFormItem(FormItem targetItem) {
        this.targetItem = targetItem;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }
}
