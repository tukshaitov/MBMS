package com.bankofbaku.record;

import java.util.List;

public interface ICrud {
    void create(Record record);

    void delete(Record record);

    List<IRecordItem[]> read();

    void update(Record record);
}
