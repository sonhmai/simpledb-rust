package com.simpledb.server;

import java.io.File;

import com.simpledb.file.FileMgr;
import com.simpledb.log.LogMgr;
import com.simpledb.buffer.BufferMgr;
import com.simpledb.tx.Transaction;

/**
 * The class that configures the system.
 *
 * @author Edward Sciore
 */
public class SimpleDB {
    public static int BLOCK_SIZE = 400;
    public static int BUFFER_SIZE = 8;
    public static String LOG_FILE = "com.simpledb.log";

    private FileMgr fm;
    private BufferMgr bm;
    private LogMgr lm;

    /**
     * A constructor useful for debugging.
     *
     * @param dirname   the name of the database directory
     * @param blocksize the block size
     * @param buffsize  the number of buffers
     */
    public SimpleDB(String dirname, int blocksize, int buffsize) {
        File dbDirectory = new File(dirname);
        fm = new FileMgr(dbDirectory, blocksize);
        lm = new LogMgr(fm, LOG_FILE);
        bm = new BufferMgr(fm, lm, buffsize);
    }

    /**
     * A simpler constructor for most situations. Unlike the
     * 3-arg constructor, it also initializes the metadata tables.
     *
     * @param dirname the name of the database directory
     */
    public SimpleDB(String dirname) {
        this(dirname, BLOCK_SIZE, BUFFER_SIZE);
        Transaction tx = newTx();
        boolean isnew = fm.isNew();
        if (isnew)
            System.out.println("creating new database");
        else {
            System.out.println("recovering existing database");
            tx.recover();
        }
        tx.commit();
    }

    /**
     * A convenient way for clients to create transactions
     * and access the metadata.
     */
    public Transaction newTx() {
        return new Transaction(fm, lm, bm);
    }


    // These methods aid in debugging
    public FileMgr fileMgr() {
        return fm;
    }

    public LogMgr logMgr() {
        return lm;
    }

    public BufferMgr bufferMgr() {
        return bm;
    }
}
