package com.greenbits.binlog;

import com.google.code.or.binlog.BinlogEventListener;
import com.google.code.or.binlog.BinlogEventV4;
import com.google.code.or.binlog.BinlogEventV4Header;
import com.google.code.or.binlog.impl.event.*;

import java.util.HashMap;

public class RowEventListenerAdapter implements BinlogEventListener {
    private RowEventListener listener;
    private HashMap<Long, String> tableIdToNameMap;
    private PositionCheckpointer checkpointer;

    public RowEventListenerAdapter(RowEventListener listener,
                                   PositionCheckpointer checkpointer) {

        this.listener = listener;
        this.tableIdToNameMap = new HashMap<Long, String>();

        this.checkpointer = checkpointer;
    }

    @Override
    public void onEvents(BinlogEventV4 event) {
        try {
            if (event instanceof FormatDescriptionEvent) {
                handleFormatDescriptionEvent((FormatDescriptionEvent) event);
            } else if (event instanceof WriteRowsEventV2) {
                handleWriteRowsEvent((WriteRowsEventV2) event);
            } else if (event instanceof UpdateRowsEventV2) {
                handleUpdateRowsEvent((UpdateRowsEventV2) event);
            } else if (event instanceof DeleteRowsEventV2) {
                handleDeleteRowsEvent((DeleteRowsEventV2) event);
            } else if (event instanceof RotateEvent) {
                handleRotateEvent((RotateEvent) event);
            } else if (event instanceof TableMapEvent) {
                handleTableMapEvent((TableMapEvent) event);
            } else if (event instanceof QueryEvent) {
                handleQueryEvent((QueryEvent) event);
            } else if (event instanceof XidEvent) {
                handleXidEvent((XidEvent)event);
            } else {
                handleUnknownEvent(event);
            }
        } catch (Throwable t) {
            handleError(t);
        }
    }

    private void handleFormatDescriptionEvent(FormatDescriptionEvent event) {
        listener.startup(new ServerVersion(
                event.getBinlogVersion(),
                event.getCreateTimestamp(),
                event.getServerVersion().toString()));
    }

    private void handleWriteRowsEvent(WriteRowsEventV2 event) {
        WriteEvent writeEvent = new WriteEvent(getTableName(event.getTableId()),
                event.getRows(),
                event.getColumnCount().intValue(),
                event.getUsedColumns().getValue(),
                event.getExtraInfo());

        listener.onWrite(writeEvent);
        checkpointer.checkpoint(getNextPosition(event));
    }

    private void handleUpdateRowsEvent(UpdateRowsEventV2 event) {
        UpdateEvent updateEvent = new UpdateEvent(getTableName(event.getTableId()),
                event.getRows(),
                event.getColumnCount().intValue(),
                event.getUsedColumnsBefore().getValue(),
                event.getUsedColumnsAfter().getValue(),
                event.getExtraInfo());

        listener.onUpdate(updateEvent);
        checkpointer.checkpoint(getNextPosition(event));
    }

    private void handleDeleteRowsEvent(DeleteRowsEventV2 event) {
        DeleteEvent deleteEvent = new DeleteEvent(getTableName(event.getTableId()),
                event.getRows(),
                event.getColumnCount().intValue(),
                event.getUsedColumns().getValue(),
                event.getExtraInfo());

        listener.onDelete(deleteEvent);
        checkpointer.checkpoint(getNextPosition(event));
    }

    private void handleQueryEvent(QueryEvent event) {
        String sql = event.getSql().toString();
        if (sql.equals("BEGIN")) {
            listener.beginTransaction();
        } else {
            throw new IllegalStateException("Unknown sql string for QueryEvent " + sql + ".");
        }
    }

    private void handleXidEvent(XidEvent event) {
        checkpointer.checkpoint(getNextPosition(event));
        listener.commitTransaction();
    }

    private void handleTableMapEvent(TableMapEvent event) {
        String tableName = event.getTableName().toString();
        tableIdToNameMap.put(event.getTableId(), tableName);
    }

    private void handleRotateEvent(RotateEvent event) {
        checkpointer.rotate(event.getBinlogFileName().toString(), event.getBinlogPosition());
    }

    private void handleUnknownEvent(BinlogEventV4 event) {
        BinlogEventV4Header header = event.getHeader();
        throw new IllegalStateException("Unknown MySQL binlog event type " + header.getEventType() + ".");
    }
    
    private void handleError(Throwable t) {
        listener.onError(t);
    }

    private String getTableName(long tableId) {
        String tableName = tableIdToNameMap.get(tableId);
        if (tableName == null) {
            throw new IllegalStateException("Cannot find table with id '" + tableId + "' in the map.");
        }
        return tableName;
    }

    private long getNextPosition(BinlogEventV4 event) {
        return event.getHeader().getNextPosition();
    }
}
