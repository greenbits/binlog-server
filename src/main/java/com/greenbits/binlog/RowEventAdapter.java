package com.greenbits.binlog;

public class RowEventAdapter implements RowEventListener {
    @Override
    public void startup(ServerVersion version) {}

    @Override
    public void beginTransaction() {}

    @Override
    public void onUpdate(UpdateEvent updateEvent) {}

    @Override
    public void onWrite(WriteEvent writeEvent) {}

    @Override
    public void onDelete(DeleteEvent deleteEvent) {}
    
    @Override
    public void onError(Throwable error) {}

    @Override
    public void commitTransaction() {}
}
