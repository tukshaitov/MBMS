package com.bankofbaku.record;

import com.bankofbaku.bean.ApplicationContextProvider;
import com.bankofbaku.record.Record.RecordStatus;
import com.bankofbaku.record.exception.RecordAddException;
import com.bankofbaku.record.exception.RecordCreationExcetption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.JpaTransactionManager;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class RecordSet {

    public enum Order {
        ASC, DESC
    }

    private static final Logger logger = LoggerFactory.getLogger(RecordSet.class);
    private int counter;
    private int dirtyCounter;
    private int version;
    private ICrud crud;
    private boolean isReadOnly;
    private final Object mutex = new Object();
    private RecordDescriptor recordDescriptor;
    private Map<Integer, Record> records = new LinkedHashMap<Integer, Record>(100);

    private JpaTransactionManager tm = null;

    public RecordSet(RecordDescriptor recordDescriptor, boolean isReadOnly, ICrud crud) {
        if (recordDescriptor == null)
            throw new RecordCreationExcetption("RecordDescriptor parameter is mandatory.");

        this.recordDescriptor = recordDescriptor;
        this.isReadOnly = isReadOnly;
        this.crud = crud;
        this.tm = (JpaTransactionManager) ApplicationContextProvider.getApplicationContext().getBean("transactionManager");
    }

    private void addRecord(Record record) {

        RecordStatus recordStatus = record.getRecordStatus();

        if (recordStatus != RecordStatus.NEW && recordStatus != RecordStatus.SERIALIZED)
            throw new RecordAddException("Record status " + recordStatus + " is not correct.");

        int recordId = record.getRecordId();
        Record setRecord = records.get(recordId);

        if (setRecord == record)
            throw new RecordAddException("Adding duplicate record is forbidden.");

        if (setRecord != null)
            synchronized (mutex) {
                recordId = ++counter;
            }

        if (record.getRecordStatus() != RecordStatus.SERIALIZED)
            dirtyCounter++;
        record.setOwnerRecordSet(this);
        record.setRecordId(recordId);
        record.setVersion(++version);
        records.put(record.getRecordId(), record);
    }

    public void copyRecord(Record record) {
        if (record != null && record.getRecordDescriptor() == this.recordDescriptor) {
            Record copyRecord = record.clone();
            addRecord(copyRecord);
        } else
            throw new RecordAddException("Record parameter is not correct.");
    }

    public Record createRecord(RecordStatus recordStatus, IRecordItem... recordItems) throws RecordCreationExcetption {
        try {
            Record record = (Record) recordDescriptor.getRecordClass().newInstance();
            synchronized (mutex) {
                record.setup(++counter, this, recordDescriptor, recordStatus, recordItems);
            }
            addRecord(record);

            return record;
        } catch (InvocationTargetException e) {
            logger.debug(e.getMessage());
        }

        return null;
    }

    public List<Record> getAllRecords() {
        return new ArrayList<Record>(records.values());
    }

    public ICrud getCrud() {
        return crud;
    }

    public Record getRecordById(Integer id) {
        return records.get(id);
    }

    public RecordDescriptor getRecordDescriptor() {
        return recordDescriptor;
    }

    public List<Record> getRecordsByStatuses(RecordStatus status) {
        List<Record> statusRecords = new ArrayList<Record>(10);
        for (Record record : records.values())
            if (record.getRecordStatus() == status)
                statusRecords.add(record);

        return statusRecords;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public boolean removeRecord(Integer id) {
        if (records.remove(id) != null)
            return true;

        return false;
    }

    public boolean removeRecord(Record record) {
        if (record != null && record.getRecordDescriptor() == recordDescriptor) {
            if (record.getRecordStatus() == RecordStatus.NEW) {
                dirtyCounter--;
                records.remove(record.getRecordId());
            } else {
                dirtyCounter++;
                record.setRecordStatus(RecordStatus.DELETED);
            }
            record.setVersion(++version);
            return true;
        }

        return false;
    }

    public void removeRecordsByStatus(RecordStatus status) {
        List<Integer> recordIds = new ArrayList<Integer>(10);
        for (Record record : records.values())
            if (record.getRecordStatus() == status) {
                if (record.getRecordStatus() == RecordStatus.NEW)
                    dirtyCounter--;
                else
                    dirtyCounter++;

                record.setRecordStatus(RecordStatus.DELETED);
                record.setVersion(++version);
            }

        for (Integer id : recordIds)
            records.remove(id);
    }

    public void setCrud(ICrud crud) {
        this.crud = crud;
    }

    public void setReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    public void sort(String fieldName, Order direction) {

    }

    public void save() {
        List<Integer> deletedIds = new ArrayList<Integer>();
        if (crud != null) {
            for (Record record : records.values()) {
                switch (record.getRecordStatus()) {
                    case NEW:
                        crud.create(record);
                        break;
                    case DELETED:
                        crud.delete(record);
                        deletedIds.add(record.getRecordId());
                        break;
                    case DIRTY:
                        crud.update(record);
                        break;
                    default:
                        continue;
                }
            }
        }
    }

    public void onCommit() {
        Iterator<Record> iterator = records.values().iterator();
        while (iterator.hasNext()) {
            Record record = iterator.next();
            switch (record.getRecordStatus()) {
                case NEW:
                case DIRTY:
                    record.setRecordStatus(RecordStatus.SERIALIZED);
                    record.rewrite();
                    break;
                case DELETED:
                    iterator.remove();
                    break;
                default:
                    continue;
            }
        }
        version = 0;
        dirtyCounter = 0;
    }

    public void fill() {
        if (crud != null) {
            records = new LinkedHashMap<Integer, Record>(100);
            counter = 0;

            for (IRecordItem[] recordItems : crud.read())
                this.createRecord(RecordStatus.SERIALIZED, recordItems);
        }
    }

    public Set<String> getFieldNames() {
        return recordDescriptor.getAliases();
    }

    public Object getField(Integer id, String alias) {
        return getField(records.get(id), alias);
    }

    public Object getField(Record record, String alias) {
        return record.getField(alias);
    }

    public boolean setField(Integer id, String alias, Object value) {
        return setField(records.get(id), alias, value);
    }

    public boolean setField(Record record, String alias, Object value) {
        boolean isSet;
        try {
            RecordStatus beforeStatus = record.getRecordStatus();
            isSet = record.setField(alias, value);

            if (beforeStatus == RecordStatus.DIRTY && record.getRecordStatus() == RecordStatus.SERIALIZED)
                dirtyCounter--;
            else if (beforeStatus == RecordStatus.SERIALIZED && record.getRecordStatus() == RecordStatus.DIRTY)
                dirtyCounter++;

            return isSet;
        } catch (InvocationTargetException e) {
            logger.debug(e.getMessage(), e);
            return false;
        }
    }

    public Integer getVersion() {
        return version;
    }

    public Integer getIncrementedVersion() {
        return ++version;
    }

    public List<Record> getRecordsFromVersion(Integer version, boolean ignoreDeleted) {
        List<Record> vRecrods = new ArrayList<Record>();
        for (Record record : records.values())
            if (ignoreDeleted && record.getRecordStatus() == RecordStatus.DELETED)
                continue;

            else if (record.getVersion() >= version)
                vRecrods.add(record);

        return vRecrods;
    }

    public Object generateId(RecordItemDescriptor itemDescriptor) {

        String tableName = itemDescriptor.getTableName();
        Connection connection;
        try {
            connection = tm.getDataSource().getConnection();
            connection.setAutoCommit(false);

            Statement statement = connection.createStatement();

            if (tableName != null && statement != null) {

                try {
                    statement.executeUpdate("INSERT IGNORE INTO " + tableName + " VALUES();");
                } catch (Exception e) {

                }

                ResultSet resultSet = statement
                        .executeQuery("SELECT auto_increment FROM information_schema.tables WHERE table_schema = 'mbms' AND table_name = '"
                                + tableName + "'");
                Integer autoIncrement = null;
                if (resultSet.next())
                    autoIncrement = resultSet.getInt("auto_increment");

                connection.rollback();
                connection.close();

                return autoIncrement;

            }
        } catch (Exception e) {

        }

        return null;
    }

    public boolean isDirty() {
        return dirtyCounter > 0;
    }
}
