package com.greenbits.binlog;

import com.google.code.or.binlog.BinlogEventV4; 

public interface RowEventListener {
    void startup(ServerVersion version);

    void beginTransaction();

    void onUpdate(UpdateEvent updateEvent);

    void onWrite(WriteEvent writeEvent);

    void onDelete(DeleteEvent deleteEvent);

    void onQuery(String sql);

    void onError(Throwable error, BinlogEventV4 event);

    void commitTransaction();
}
