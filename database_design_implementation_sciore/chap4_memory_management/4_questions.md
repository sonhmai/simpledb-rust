

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

### 4.7 buffer pool replacement strategies

Consider the example pin/unpin scenario of Fig. 4.9a, together with the additional operations pin(60); pin(70). 
For each of the four replacement strategies given in the text
- draw the state of the buffers, 
- assuming that the buffer pool contains five buffers.

```
time    action
1       pin(10); pin(20); pin(30); 
4       pin(40); unpin(20); pin(50); 
7       unpin(40); unpin(10); unpin(30); 
10      unpin(50); pin(60); pin(70)
```

1-naive: scan pool, replace first unpinned buffer found
``` 
# done time 10

Buffer      0   1   2   3   4
block#      10  20  30  40  50
t read      1   2   3   4   6
t unpin     8   5   9   7   10

# done time 11 and 12

Buffer      0   1   2   3   4
block#      60  70  30  40  50
t read      11  12  3   4   6
t unpin     -   -   9   7   10
```

2-FIFO: replace smallest time read in first
``` 
# done time 10 same as above as no replacement
# done time 11 and 12 - same as 0, 1 have smallest read in time 1,2

Buffer      0   1   2   3   4
block#      60  70  30  40  50
t read      11  12  3   4   6
t unpin     -   -   9   7   10
```

3-LRU: replace smallest time unpin first
``` 
# done time 10 same as above as no replacement
# done time 11 and 12 - different

Buffer      0   1   2   3   4
block#      10  60  30  70  50
t read      1   11  3   12   6
t unpin     8   -   9   -   10
```

4-Clock: scan buffer pool clockwise after last replacement and replace first unpinned
``` 
# done time 10 same as above as no replacement
# done time 11 and 12 - final same as FIFO and naive
# because last replace == read in is buffer-4 -> scan clockwise
# -> next is 0 for block-60, then buffer-1 for block-70

Buffer      0   1   2   3   4
block#      60  70  30  40  50
t read      11  12  3   4   6
t unpin     -   -   9   7   10
```

### 4.9 buffer manager pin same block

Suppose that two different clients each want to pin the same block but are placed on the `wait list` because no buffers are available. Consider the implementation of the SimpleDB class BufferMgr. Show that when a single buffer becomes available, both clients will be able to use it.



### 4.10

Consider the adage “Virtual is its own reward.” Comment on the cleverness of the pun, and discuss its applicability to the buffer manager.

skip