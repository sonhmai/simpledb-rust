package simpledb.server;

import simpledb.buffer.BufferMgr;
import simpledb.file.FileMgr;
import simpledb.log.LogMgr;
import simpledb.tx.Transaction;

public class SimpleDB {

    private FileMgr fm;
    private BufferMgr bm;
    private LogMgr lm;

    public Transaction newTx() {
        return new Transaction(fm, lm, bm);
    }
}
