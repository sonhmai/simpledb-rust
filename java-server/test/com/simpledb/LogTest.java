package com.simpledb;

import com.simpledb.file.Page;
import com.simpledb.log.LogMgr;
import com.simpledb.server.SimpleDB;

import java.util.Iterator;

public class LogTest {
    private static LogMgr lm;

    public static void main(String[] args) {
        SimpleDB db = new SimpleDB("logtest", 400, 8);
        lm = db.logMgr();
        // create 70 log records in 2 batches: 1-35, 36-70
        createRecords(1, 35);
        // only 20 records were printed because 20 fit 1st log block and flushed to disk after log 21st created.
        // the rest 15 (21 to 35) are in mem and not flushed yet.
        printLogRecords("The log file now has these records:");
        createRecords(36, 70);
        lm.flush(65); // ensure 65 log record is on disk -> flush 65 and smaller
        // 66 to 70 are also flushed because they are in the same page as 65, hence printed here.
        printLogRecords("The log file now has these records:");
    }

    private static void printLogRecords(String msg) {
        System.out.println(msg);
        Iterator<byte[]> iter = lm.iterator();
        while (iter.hasNext()) {
            byte[] rec = iter.next();
            // create a Page to wrap the record to read String and Int from it
            Page p = new Page(rec);
            String s = p.getString(0);
            int npos = Page.maxLength(s.length());
            int val = p.getInt(npos);
            System.out.println("[" + s + ", " + val + "]");
        }
        System.out.println();
    }

    private static void createRecords(int start, int end) {
        System.out.print("Creating records: ");
        for (int i = start; i <= end; i++) {
            byte[] rec = createLogRecord("record" + i, i + 100);
            int lsn = lm.append(rec);
            System.out.print(lsn + " ");
        }
        System.out.println();
    }

    private static byte[] createLogRecord(String s, int n) {
        int npos = Page.maxLength(s.length());
        // allocate byte array to be log record
        byte[] b = new byte[npos + Integer.BYTES];
        // Page to use setString and setInt to place string and int in log record at correct offsets
        Page p = new Page(b);
        p.setString(0, s);
        p.setInt(npos, n);
        return b;
    }
}
