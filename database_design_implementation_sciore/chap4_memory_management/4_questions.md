

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

### 4.2

Explain why the method `BufferMgr.pin` is synchronized. What problem could occur if it wasn’t?
- many concurrent threads might be able to pin the same block and modify it.
- if that happens, there will be race condition, overwriting data, update loss.

```java
public class BufferMgr {
    public synchronized Buffer pin(BlockId blk) {
        try {
            long timestamp = System.currentTimeMillis();
            Buffer buff = tryToPin(blk);
            while (buff == null && !waitingTooLong(timestamp)) {
                wait(MAX_TIME);
                buff = tryToPin(blk);
            }
            if (buff == null)
                throw new BufferAbortException();
            return buff;
        } catch (InterruptedException e) {
            throw new BufferAbortException();
        }
    }
}
```