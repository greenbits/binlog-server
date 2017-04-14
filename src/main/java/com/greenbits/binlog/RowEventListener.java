package com.greenbits.binlog;

import com.github.shyiko.mysql.binlog.event.Event;

public interface RowEventListener {
    void startup(ServerVersion version);

    void beginTransaction();

    void onUpdate(UpdateEvent updateEvent);

    void onWrite(WriteEvent writeEvent);

    void onDelete(DeleteEvent deleteEvent);

    void onQuery(String sql);

    void onError(Throwable error, Event event);

    void commitTransaction();
}
