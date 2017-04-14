package com.greenbits.binlog;

import com.github.shyiko.mysql.binlog.event.TableMapEventData;

import java.util.List;
import java.io.Serializable;

public class WriteEvent {
    private TableMapEventData tableMapEvent;
    private List<Serializable[]> rows;
    private byte[] usedColumns;

    public WriteEvent(TableMapEventData tableMapEvent,
                      List<Serializable[]> rows,
                      byte[] usedColumns) {

        this.tableMapEvent = tableMapEvent;
        this.rows = rows;
        this.usedColumns = usedColumns;
    }

    public String getTableName() {
        return tableMapEvent.getTable();
    }

    public String getDatabaseName() {
        return tableMapEvent.getDatabase();
    }

    public List<Serializable[]> getRows() {
        return rows;
    }

    public byte[] getUsedColumns() {
        return usedColumns;
    }
}
