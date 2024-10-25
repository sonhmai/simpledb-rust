package com.simpledb.server;

import com.simpledb.buffer.BufferMgr;
import com.simpledb.file.FileMgr;
import com.simpledb.log.LogMgr;
import com.simpledb.tx.Transaction;

import java.io.File;

public class SimpleDB {

    private FileMgr fm;
    private BufferMgr bm;
    private LogMgr lm;

    public SimpleDB(String dirname) {
        File dbDirectory = new File(dirname);
    }

    public Transaction newTx() {
        return new Transaction(fm, lm, bm);
    }
}
