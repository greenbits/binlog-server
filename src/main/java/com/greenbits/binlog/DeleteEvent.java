package com.greenbits.binlog;

import com.github.shyiko.mysql.binlog.event.TableMapEventData;

import java.util.List;
import java.io.Serializable;

public class DeleteEvent {
    private final TableMapEventData tableMapEvent;
    private final List<Serializable[]> rows;
    private final byte[] usedColumns;

    public DeleteEvent(TableMapEventData tableMapEvent,
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
