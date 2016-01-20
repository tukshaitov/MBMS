package com.bankofbaku.formdata;

import com.bankofbaku.exception.FormItemCreationException;
import com.bankofbaku.formdata.FormItemRelation.Operation;
import com.bankofbaku.formdata.FormItemRelation.Type;
import com.bankofbaku.record.IRecordItem;
import com.bankofbaku.record.Record;
import com.bankofbaku.record.Record.RecordStatus;
import com.bankofbaku.record.RecordSet;

import java.util.*;

public abstract class FormItem {

    private Map<String, FormItemField> fields = new HashMap<String, FormItemField>(10);
    private Set<FormItem> outerItems = new HashSet<FormItem>(5);
    private RecordSet recordSet;
    private Set<FormItemRelation> relations = new HashSet<FormItemRelation>(5);

    public FormItem(RecordSet recordSet) {
        if (recordSet == null)
            throw new FormItemCreationException("Mandatory parameter recordSet should be set.");

        this.recordSet = recordSet;

        for (String fieldName : recordSet.getFieldNames())
            fields.put(fieldName, new FormItemField(fieldName));
    }

    public boolean addRelation(Type type, FormItem formItem, Operation operation, FormItemField... fields) {
        if (formItem == this)
            return false;

        boolean isAdded = false;

        if (type == Type.INNER)
            isAdded = relations.add(new FormItemRelation(formItem, operation, fields));
        else
            isAdded = formItem.addRelation(Type.INNER, this, operation, fields);

        if (isAdded == true)
            outerItems.add(formItem);

        return isAdded;
    }

    public boolean canBeRemoved(Record record) {
        return false;
    }

    private boolean containRelation(FormItem formItem) {
        for (FormItemRelation relation : relations)
            if (relation.getFormItem() == formItem)
                return true;

        return false;

    }

    public Record createRecord(IRecordItem... recordItems) {
        Record record = recordSet.createRecord(RecordStatus.NEW, recordItems);
        if (record != null) {
            for (FormItemRelation relation : relations)
                if (relation.getOperation() == Operation.CREATETO)
                    relation.getFormItem().createRecord(recordItems);
        }

        return record;
    }

    public FormItemField getField(String name) {
        return fields.get(name);
    }

    public Set<String> getFieldNames() {
        return recordSet.getFieldNames();
    }

    public Set<FormItemField> getFields() {
        return new HashSet<FormItemField>(fields.values());
    }

    public RecordSet getRecordSet() {
        return recordSet;
    }

    public boolean removeRecord(Integer recordId) {
        return removeRecord(recordSet.getRecordById(recordId));
    }

    public boolean removeRecord(Record record) {

        Set<FormItem> items = new HashSet<FormItem>(10);

        for (FormItemRelation relation : relations)
            if (relation.getOperation() == Operation.DELETEFROM) {
                if (!relation.getFormItem().canBeRemoved(record))
                    return false;

                items.add(relation.getFormItem());
            }

        boolean isRemoved = recordSet.removeRecord(record);
        if (isRemoved)
            for (FormItem item : items)
                item.removeRecord(record);

        return isRemoved;
    }

    public void removeRelation(Type type, FormItem formItem, Operation... operations) {
        if (formItem == this)
            return;

        Set<Operation> opns = new HashSet<Operation>(Arrays.asList(operations));

        if (type == Type.INNER) {
            for (Iterator<FormItemRelation> i = relations.iterator(); i.hasNext(); ) {
                FormItemRelation itemRelation = i.next();
                if (itemRelation.getFormItem() == formItem)

                    if (operations.length > 0 && !opns.contains(itemRelation.getOperation()))
                        continue;
                    else
                        i.remove();
            }
        } else {
            formItem.removeRelation(Type.INNER, this, operations);
            if (!formItem.containRelation(this))
                outerItems.remove(formItem);
        }

    }

    public void removeRelation(Type type, Operation... operations) {

        Set<Operation> opns = new HashSet<Operation>(Arrays.asList(operations));

        if (type == Type.INNER) {
            for (Iterator<FormItemRelation> i = relations.iterator(); i.hasNext(); ) {
                FormItemRelation itemRelation = i.next();
                if (operations.length > 0 && !opns.contains(itemRelation.getOperation()))
                    continue;
                else
                    i.remove();
            }
        } else {
            for (Iterator<FormItem> i = outerItems.iterator(); i.hasNext(); ) {
                FormItem item = i.next();
                item.removeRelation(type, this, operations);
                if (!item.containRelation(this))
                    i.remove();
            }
        }

    }

    public void save() {
        for (FormItemRelation relation : relations)
            if (relation.getOperation() == Operation.DELETEFROM)
                relation.getFormItem().save();

        recordSet.save();
    }

    public boolean setField(Integer recordId, String field, Object value) {
        return setField(recordSet.getRecordById(recordId), field, value);
    }

    public boolean setField(Record record, String field, Object value) {
        boolean isSet = recordSet.setField(record, field, value);
        if (isSet)
            for (FormItemRelation relation : relations)
                if (relation.getFields().contains(fields.get(field)) && relation.getOperation() == Operation.UPDATETO)
                    relation.getFormItem().setField(record, field, value);

        return isSet;
    }

    public List<Record> getRecordsFromVersion(Integer version) {
        if (recordSet != null)
            if (version == 0)
                return recordSet.getRecordsFromVersion(version, true);
            else
                return recordSet.getRecordsFromVersion(version, false);

        return null;
    }

    public Integer getVersion() {
        return recordSet.getVersion();
    }

    public void fill() {
        recordSet.fill();
    }

    public boolean isDirty() {
        return recordSet.isDirty();
    }

    public void onCommit() {
        recordSet.onCommit();
    }

    public void deleteAll() {
        Iterator<Record> ri = this.getRecordSet().getAllRecords().iterator();
        while (ri.hasNext())
            this.removeRecord(ri.next());
    }
}
