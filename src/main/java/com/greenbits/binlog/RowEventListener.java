package com.greenbits.binlog;

public interface RowEventListener {
    void startup(ServerVersion version);

    void beginTransaction();

    void onUpdate(UpdateEvent updateEvent);

    void onWrite(WriteEvent writeEvent);

    void onDelete(DeleteEvent deleteEvent);
    
    void onError(Throwable error);

    void commitTransaction();
}
