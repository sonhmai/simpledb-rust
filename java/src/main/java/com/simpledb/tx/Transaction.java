package com.simpledb.tx;

import com.simpledb.buffer.Buffer;
import com.simpledb.buffer.BufferMgr;
import com.simpledb.file.BlockId;
import com.simpledb.file.FileMgr;
import com.simpledb.file.Page;
import com.simpledb.log.LogMgr;
import com.simpledb.tx.RecoveryMgr;

public class Transaction {
  private static int nextTxNum = 0;
  private static final int END_OF_FILE = -1;
  private RecoveryMgr recoveryMgr;
  private ConcurrencyMgr concurMgr;
  private BufferMgr bm;
  private FileMgr fm;
  private int txnum;
//  private BufferList mybuffers;

  public Transaction(FileMgr fm, LogMgr lm, BufferMgr bm) {
    this.fm = fm;
    this.bm = bm;
    txnum = nextTxNumber();
    recoveryMgr = new RecoveryMgr(this, txnum, lm, bm);
    concurMgr = new ConcurrencyMgr();
//    mybuffers = new BufferList(bm);
  }

  public void commit() {
    recoveryMgr.commit();
    System.out.println("transaction " + txnum + " committed");
    concurMgr.release();
//    mybuffers.unpinAll();
  }

  public void rollback() {
    recoveryMgr.rollback();
    System.out.println("transaction " + txnum + " rolled back");
    concurMgr.release();
//    mybuffers.unpinAll();
  }

  public void recover() {
    bm.flushAll(txnum);
    recoveryMgr.recover();
  }

  public void pin(BlockId blk) {
//    mybuffers.pin(blk);
  }

  public void unpin(BlockId blk) {
//    mybuffers.unpin(blk);
  }

  public int getInt(BlockId blk, int offset) {
    concurMgr.sLock(blk);
//    Buffer buff = mybuffers.getBuffer(blk);
//    return buff.contents().getInt(offset);
    return 1;
  }

  public String getString(BlockId blk, int offset) {
    concurMgr.sLock(blk);
//    Buffer buff = mybuffers.getBuffer(blk);
//    return buff.contents().getString(offset);
    return "test";
  }

  public int blockSize() {
    return fm.blockSize();
  }

  public void setInt(BlockId blk, int offset, int val, boolean okToLog) {
    concurMgr.xLock(blk);
  }

  public void setString(BlockId blk, int offset, String val, boolean okToLog) {
    concurMgr.xLock(blk);
  }

  private static synchronized int nextTxNumber() {
    nextTxNum++;
    return nextTxNum;
  }
}