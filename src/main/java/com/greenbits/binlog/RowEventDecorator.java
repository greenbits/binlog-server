package com.greenbits.binlog;

public class RowEventDecorator implements RowEventListener {
    private RowEventListener nextListener;
    
    public RowEventDecorator(RowEventListener nextListener) {
        this.nextListener = nextListener;
    }
    
    public RowEventListener getNextListener() {
        return this.nextListener;
    }
    
    @Override
    public void startup(ServerVersion version) {
        nextListener.startup(version);
    }

    @Override
    public void beginTransaction() {
        nextListener.beginTransaction();
    }

    @Override
    public void onUpdate(UpdateEvent updateEvent) {
        nextListener.onUpdate(updateEvent);
    }

    @Override
    public void onWrite(WriteEvent writeEvent) {
        nextListener.onWrite(writeEvent);
    }

    @Override
    public void onDelete(DeleteEvent deleteEvent) {
        nextListener.onDelete(deleteEvent);
    }
    
    @Override
    public void onError(Throwable error) {
        nextListener.onError(error);
    }

    @Override
    public void commitTransaction() {
        nextListener.commitTransaction();
    }
}
