package com.bankofbaku.formdata;

import com.bankofbaku.record.ICrud;
import com.bankofbaku.record.RecordSet;
import com.bankofbaku.record.RecordSetFactory;

public class GridBase implements IGrid {
    RecordSet recordSet;

    public GridBase(RecordSet recordSet) {
        this.recordSet = recordSet;
    }

    public GridBase(RecordSetFactory recordSetFactory) {
        recordSet = recordSetFactory.create();
    }

    public GridBase(RecordSetFactory recordSetFactory, boolean isReadOnly, ICrud crud) {
        recordSet = recordSetFactory.create(isReadOnly, crud);
    }

    public GridBase(RecordSetFactory recordSetFactory, boolean isReadOnly) {
        recordSet = recordSetFactory.create(isReadOnly);
    }

    public GridBase(RecordSetFactory recordSetFactory, ICrud crud) {
        recordSet = recordSetFactory.create(crud);
    }

}
