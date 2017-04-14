package com.greenbits.binlog;

import com.github.shyiko.mysql.binlog.event.TableMapEventData;

import java.util.List;
import java.util.Map;
import java.io.Serializable;

public class UpdateEvent {
    private final TableMapEventData tableMapEvent;
    private final List<Map.Entry<Serializable[], Serializable[]>> rows;
    private final byte[] usedColumnsBefore;
    private final byte[] usedColumnsAfter;

    public UpdateEvent(TableMapEventData tableMapEvent,
                       List<Map.Entry<Serializable[],Serializable[]>> rows,
                       byte[] usedColumnsBefore,
                       byte[] usedColumnsAfter) {

        this.tableMapEvent = tableMapEvent;
        this.rows = rows;
        this.usedColumnsBefore = usedColumnsBefore;
        this.usedColumnsAfter = usedColumnsAfter;
    }

    public String getTableName() {
        return tableMapEvent.getTable();
    }

    public String getDatabaseName() {
        return tableMapEvent.getDatabase();
    }

    public List<Map.Entry<Serializable[], Serializable[]>> getRows() {
        return rows;
    }

    public byte[] getUsedColumnsBefore() {
        return usedColumnsBefore;
    }

    public byte[] getUsedColumnsAfter() {
        return usedColumnsAfter;
    }
}
