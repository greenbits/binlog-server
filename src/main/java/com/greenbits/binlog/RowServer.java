package com.greenbits.binlog;

import com.google.code.or.OpenReplicator;

import java.util.concurrent.TimeUnit;

/**
 * The server that parses Herer's master database and ETL's models into a reporting database via
 * a MySQL binlog stream.
 */
public class RowServer extends OpenReplicator {
    private String databaseName;
    private PositionCheckpointer checkpointer;
    private RowEventListener rowEventListener;

    public PositionCheckpointer getCheckpointer() {
        return checkpointer;
    }

    public void setCheckpointer(PositionCheckpointer checkpointer) {
        this.checkpointer = checkpointer;
    }

    public RowEventListener getRowEventListener() {
        return rowEventListener;
    }

    public void setRowEventListener(RowEventListener rowEventListener) {
        this.rowEventListener = rowEventListener;
    }
    
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public void start() throws Exception {
        assertOption(checkpointer != null, "Checkpointer");
        assertOption(rowEventListener != null, "Listener");
        
        setBinlogFileName(checkpointer.getFileName());
        setBinlogPosition(checkpointer.getPosition());
        setBinlogEventListener(new RowEventListenerAdapter(rowEventListener, checkpointer, databaseName));

        super.start();
    }

    public void stop(int seconds) throws Exception {
        stop(seconds, TimeUnit.SECONDS);
    }

    private void assertOption(boolean valid, String option) {
        if (!valid) {
            throw new IllegalArgumentException(option + "is required.");
        }
    }
}

