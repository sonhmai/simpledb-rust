package com.simpledb.tx.recovery;

import com.simpledb.file.BlockId;
import com.simpledb.file.FileMgr;
import com.simpledb.file.Page;
import com.simpledb.log.LogMgr;
import com.simpledb.server.SimpleDB;

import java.util.Iterator;

public class PrintLogFile {
   public static void main(String[] args) {
      SimpleDB db = new SimpleDB("studentdb", 400, 8);
      FileMgr fm = db.fileMgr();
      LogMgr lm = db.logMgr();
      String filename = "com.simpledb.log";

      int lastblock = fm.length(filename) - 1;
      BlockId blk = new BlockId(filename, lastblock);
      Page p = new Page(fm.blockSize());
      fm.read(blk, p);
      Iterator<byte[]> iter = lm.iterator();
      while (iter.hasNext()) {
         byte[] bytes = iter.next();
         LogRecord rec = LogRecord.createLogRecord(bytes);
         System.out.println(rec);
      }
   }
}
