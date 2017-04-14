package com.greenbits.binlog;

import com.github.shyiko.mysql.binlog.BinaryLogClient;

import java.util.concurrent.TimeUnit;

/**
 * The server that parses Herer's master database and ETL's models into a reporting database via
 * a MySQL binlog stream.
 */
public class RowServer extends BinaryLogClient {
    private String databaseName;
    private PositionCheckpointer checkpointer;
    private RowEventListener rowEventListener;

    public RowServer(String hostname, int port, String username, String password) {
      super(hostname, port, username, password);
    }

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

        setBinlogFilename(checkpointer.getFileName());
        setBinlogPosition(checkpointer.getPosition());
        registerEventListener(new RowEventListenerAdapter(rowEventListener, checkpointer, databaseName));

        super.connect();
    }

    public void stop() throws Exception {
        disconnect();
    }

    private void assertOption(boolean valid, String option) {
        if (!valid) {
            throw new IllegalArgumentException(option + "is required.");
        }
    }
}

