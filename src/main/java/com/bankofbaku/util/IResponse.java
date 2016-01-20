package com.bankofbaku.util;

import java.io.Serializable;

public interface IResponse extends Serializable, Cloneable {
    enum Status {
        OK, ERROR, TASKSAVEERROR
    }

    String DefaultMessage = "response_default_message";
}
