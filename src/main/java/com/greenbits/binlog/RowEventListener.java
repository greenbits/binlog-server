package com.greenbits.binlog;

public interface RowEventListener {
    void startup(ServerVersion version);

    void beginTransaction();

    void update(UpdateEvent updateEvent);

    void write(WriteEvent writeEvent);

    void delete(DeleteEvent deleteEvent);

    void commitTransaction();
}
