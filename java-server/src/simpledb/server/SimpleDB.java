package simpledb.server;

import simpledb.buffer.BufferMgr;
import simpledb.file.FileMgr;
import simpledb.log.LogMgr;
import simpledb.tx.Transaction;

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
