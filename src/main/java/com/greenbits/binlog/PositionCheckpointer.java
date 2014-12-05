package com.greenbits.binlog;

/**
 * An interface to retrieve and update the binlog position.
 */
public interface PositionCheckpointer {
    /**
     * Update the binlog position.
     *
     * @param position the current position
     */
    public void checkpoint(long position);

    /**
     * Update the binlog file name and position.
     *
     * @param fileName the current file name
     * @param position the current position
     */
    public void checkpoint(String fileName, long position);

    /**
     * @return the current binlog file name.
     */
    public String getFileName();

    /**
     * @return the current binlog position.
     */
    public long getPosition();
}
