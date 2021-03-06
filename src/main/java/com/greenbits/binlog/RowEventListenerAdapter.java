package com.greenbits.binlog;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;

import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.lang.System.*;

public class RowEventListenerAdapter implements BinaryLogClient.EventListener {
    private static final String MYSQL_DATABASE_NAME = "mysql";
    private static final EventType[] EVENTS_TO_SKIP_ARR = new EventType[] { EventType.ANONYMOUS_GTID, EventType.STOP, EventType.PREVIOUS_GTIDS };
    private static final HashSet<EventType> EVENTS_TO_SKIP = new HashSet<EventType>(Arrays.asList(EVENTS_TO_SKIP_ARR));

    private RowEventListener listener;
    private HashMap<Long, TableMapEventData> tableIdToTableMapEvent;
    private String databaseName;
    private Set<String> tables;

    private PositionCheckpointer checkpointer;

    public RowEventListenerAdapter(RowEventListener listener,
                                   PositionCheckpointer checkpointer,
                                   String databaseName,
                                   Set tables) {

        this.listener = listener;
        this.tableIdToTableMapEvent = new HashMap<Long, TableMapEventData>();
        this.checkpointer = checkpointer;
        this.databaseName = databaseName;
        this.tables = tables;
    }

    @Override
    public void onEvent(Event event) {
        EventData eventData = event.getData();
        try {
            if (eventData instanceof FormatDescriptionEventData) {
                handleFormatDescriptionEvent(event);
            } else if (eventData instanceof WriteRowsEventData) {
                handleWriteRowsEvent(event);
            } else if (eventData instanceof UpdateRowsEventData) {
                handleUpdateRowsEvent(event);
            } else if (eventData instanceof DeleteRowsEventData) {
                handleDeleteRowsEvent(event);
            } else if (eventData instanceof RotateEventData) {
                handleRotateEvent(event);
            } else if (eventData instanceof TableMapEventData) {
                handleTableMapEvent(event);
            } else if (eventData instanceof QueryEventData) {
                handleQueryEvent(event);
            } else if (eventData instanceof XidEventData) {
                handleXidEvent(event);
            } else if (EVENTS_TO_SKIP.contains(event.getHeader().getEventType())){
            } else {
                handleUnknownEvent(event);
            }
        } catch (Throwable t) {
            handleError(t, event);
        }
    }

    private void handleFormatDescriptionEvent(Event event) {
        FormatDescriptionEventData eventData = event.getData();
        listener.startup(new ServerVersion(
                eventData.getBinlogVersion(),
                eventData.getServerVersion().toString()));
    }

    private void handleWriteRowsEvent(Event event) {
        WriteRowsEventData eventData = event.getData();
        TableMapEventData tableMapEvent = getTableMapEvent(eventData.getTableId());
        if (shouldSkip(tableMapEvent)) return;

        WriteEvent writeEvent = new WriteEvent(tableMapEvent,
                eventData.getRows(),
                eventData.getIncludedColumns().toByteArray());

        listener.onWrite(writeEvent);
        checkpointer.checkpoint(getNextPosition(event));
    }

    private void handleUpdateRowsEvent(Event event) {
        UpdateRowsEventData eventData = event.getData();
        TableMapEventData tableMapEvent = getTableMapEvent(eventData.getTableId());
        if (shouldSkip(tableMapEvent)) return;

        UpdateEvent updateEvent = new UpdateEvent(tableMapEvent,
                eventData.getRows(),
                eventData.getIncludedColumnsBeforeUpdate().toByteArray(),
                eventData.getIncludedColumns().toByteArray());

        listener.onUpdate(updateEvent);
        checkpointer.checkpoint(getNextPosition(event));
    }

    private void handleDeleteRowsEvent(Event event) {
        DeleteRowsEventData eventData = event.getData();
        TableMapEventData tableMapEvent = getTableMapEvent(eventData.getTableId());
        if (shouldSkip(tableMapEvent)) return;

        DeleteEvent deleteEvent = new DeleteEvent(tableMapEvent,
                eventData.getRows(),
                eventData.getIncludedColumns().toByteArray());

        listener.onDelete(deleteEvent);
        checkpointer.checkpoint(getNextPosition(event));
    }

    private void handleQueryEvent(Event event) {
        QueryEventData eventData = event.getData();
        if (shouldSkipOnDatabaseName(eventData.getDatabase())) return;

        String sql = eventData.getSql().toString();
        if (sql.equals("BEGIN")) {
            listener.beginTransaction();
        } else if (sql.equals("COMMIT")) {
            commitTransaction(event);
        } else {
            listener.onQuery(sql);
        }
    }

    private void handleXidEvent(Event event) {
        commitTransaction(event);
    }

    private void handleTableMapEvent(Event event) {
        TableMapEventData eventData = event.getData();
        tableIdToTableMapEvent.put(eventData.getTableId(), eventData);
    }

    private void handleRotateEvent(Event event) {
        RotateEventData eventData = event.getData();
        checkpointer.rotate(eventData.getBinlogFilename().toString(), eventData.getBinlogPosition());
    }

    private void handleUnknownEvent(Event event) {
        throw new IllegalStateException("Unknown MySQL binlog event type " + event + ".");
    }

    private void handleError(Throwable t, Event event) {
        listener.onError(t, event);
    }

    private void commitTransaction(Event event) {
        checkpointer.checkpoint(getNextPosition(event));
        listener.commitTransaction();
    }

    private TableMapEventData getTableMapEvent(long tableId) {
        TableMapEventData tableMapEvent = tableIdToTableMapEvent.get(tableId);
        if (tableMapEvent == null) {
            throw new IllegalStateException("Cannot find table with id '" + tableId + "' in the map.");
        }
        return tableMapEvent;
    }

    private long getNextPosition(Event event) {
        return ((EventHeaderV4)event.getHeader()).getNextPosition();
    }

    private boolean shouldSkip(TableMapEventData tableMapEvent) {
      return shouldSkipOnDatabaseName(tableMapEvent.getDatabase()) ||
        shouldSkipOnTable(tableMapEvent.getTable());
    }

    private boolean shouldSkipOnDatabaseName(String databaseName) {
        return this.databaseName != null &&
          !(this.databaseName.equalsIgnoreCase(databaseName) ||
              this.databaseName.equalsIgnoreCase(MYSQL_DATABASE_NAME));
    }

    private boolean shouldSkipOnTable(String tableName) {
        return this.tables != null && !this.tables.contains(tableName);
    }
}
