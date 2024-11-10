package com.simpledb.log;

import java.util.Iterator;

import com.simpledb.file.BlockId;
import com.simpledb.file.FileMgr;
import com.simpledb.file.Page;

/**
 * Log Manager is responsible for writing log records to log file.
 * It does not understand the content of log records and treat them as bytes indexed by log sequence number (LSN).
 * Database has one LogMgr object which is created when init database engine.
 */
public class LogMgr {
    private FileMgr fm;
    private String logfile;
    private Page logpage;
    private BlockId currentblk;
    /// available LSN for next log record
    private int latestLSN = 0;
    private int lastSavedLSN = 0;

    public LogMgr(FileMgr fm, String logfile) {
        this.fm = fm;
        this.logfile = logfile;
        byte[] b = new byte[fm.blockSize()];
        // allocate a single page to hold the last log block of the file
        logpage = new Page(b);
        int logsize = fm.length(logfile);
        if (logsize == 0) {
            currentblk = appendNewBlock(); // append new block if empty
        } else {
            currentblk = new BlockId(logfile, logsize - 1); // get the last block
            fm.read(currentblk, logpage); // read the current block
        }
    }

    /*
     * Flush only if the specified value is
     * large than or equals to the lastSavedLSN
     */
    public void flush(int lsn) {
        if (lsn >= lastSavedLSN)
            flush();
    }

    /**
     * Return an iterator of log records (byte[]) to client in reverse order starting with
     * the latest one and move backwards.
     */
    public Iterator<byte[]> iterator() {
        flush(); // make sure client can see the latest log records in memory by flushing them to disk
        return new LogIterator(fm, currentblk);
    }

    /**
     * Append new content to page and return an int Log Sequence Number of next log record.
     * Appending does not mean persisting to disk, log manager chooses when to persist separately.
     * Client can force a log record to disk by calling flush(int lsn).
     * <p>
     * Append assigns LSN sequentially starting from 1.
     * Append places log records in the page from right to left. This allows iterator to read in reverse
     * order from left to right (latest to earlier).
     * Boundary value is written to the first four bytes of the page so
     * the iterator will know where the records begin.
     * <p>
     * Get the boundary from the integer in the position 0 in the page.
     * If the current page is not enough, flush and add new block,
     * and set it to the current block. The boundary is the blocksize.
     * Write the log record from the boundary position and update the record position
     * |<boundary>.....<appended contents><boundary pos>|
     */
    public synchronized int append(byte[] logrec) {
        int boundary = logpage.getInt(0); // the first integer indicates the position before which new content will be written.
        int recsize = logrec.length;
        int bytesneeded = recsize + Integer.BYTES;
        if (boundary - bytesneeded < Integer.BYTES) {
            flush();
            currentblk = appendNewBlock();
            boundary = logpage.getInt(0);
        }
        int recpos = boundary - bytesneeded;

        logpage.setBytes(recpos, logrec);
        logpage.setInt(0, recpos);
        latestLSN += 1;
        return latestLSN;
    }

    /*
     * Append a new block,
     * writes the blocksize in the position 0,
     * and save it to the file
     */
    private BlockId appendNewBlock() {
        BlockId blk = fm.append(logfile);
        logpage.setInt(0, fm.blockSize());
        fm.write(blk, logpage);
        return blk;
    }

    private void flush() {
        fm.write(currentblk, logpage);
        lastSavedLSN = latestLSN;
    }
}