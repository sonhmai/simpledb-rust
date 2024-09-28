

### 4.1. 

The code for LogMgr.iterator calls flush. Is this call necessary? Explain.

```java
interface LogMgr {
    
    public LogMgr(FileMgr fm, String logfile);

    public int append(byte[] rec);

    public void flush(int lsn);

    // client calls iterator to read records in the log.
    // returns a Java iterator for log records.
    // reverse order: from most recent, moving backwards thru log file.
    // This order because of how recovery manager wants to see them.
    public Iterator<byte[]> iterator();
}
```