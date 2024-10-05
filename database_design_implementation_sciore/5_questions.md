# Chapter 5 questions

from reading
- why locking at block level, not page or something else?
- if `doRecover` does not tie to one transaction specifically, why is it in a specific transaction? `Transaction` has `RecoveryManager` has `recover`. It seems to me that it acts on a database level recovery rather than a specific transaction scope.

### 5.4 recovery and StartRecord

```
The recovery manager writes a start record to the log when each transaction begins.
(a) What is the practical benefit of having start records in the log?
(b) Suppose that a database system decides not to write start records to the log.
    Can the recovery manager still function properly? 
    What capabilities are impacted?
```

What is the practical benefit of having start records in the log?
- to know when to stop when executing the rollback algo (iterate through log records from the end backwards to undo each record for a transaction). see code below.
```java
private void doRollback() {
    Iterator<byte[]> iter = lm.iterator();
    while (iter.hasNext()) {
      byte[] bytes = iter.next();
      LogRecord rec = LogRecord.createLogRecord(bytes);
      if (rec.txNumber() == txnum) {
        if (rec.op() == LogRecord.START)
          return;
        rec.undo(tx);
      }
    }
}
```

(b) not writing start record for a transaction
- yes, recovery manager can still function properly.
- impact
  - `rollback` performance is impacted (slower) as it needs to traverse all log records to the start to be sure of undoing all operations of that transaction (each txn has its own recovery manager so the rollback method of the RM know which txn number to look for in the log records).
  - not able to measure transaction `commit/rollback ts - start ts` duration for monitoring, tuning.
- `doRecover` is **not** impacted because it relies on `CheckpointRecord`, not `StartRecord`
```java
  private void doRecover() {
    Collection<Integer> finishedTxs = new ArrayList<>();
    Iterator<byte[]> iter = lm.iterator();
    while (iter.hasNext()) {
      byte[] bytes = iter.next();
      LogRecord rec = LogRecord.createLogRecord(bytes);
      if (rec.op() == LogRecord.CHECKPOINT)
        return;
      if (rec.op() == LogRecord.COMMIT || rec.op() == LogRecord.ROLLBACK)
        finishedTxs.add(rec.txNumber());
      else if (!finishedTxs.contains(rec.txNumber()))
        rec.undo(tx);
    }
  }
```

### 5.5

The SimpleDB rollback method writes the rollback log record to disk before it returns. Is this necessary? Is it a good idea?

### 5.7 undo-only

```
Consider the undo-only commit algorithm of Fig. 5.7. 
Explain why it would be incorrect to swap steps 1 and 2 of the algorithm.
Give an example of the bad scenario.

Fig. 5.7 algo for committing a transaction, undo-only recovery
1. Flush the transaction’s modified buffers to disk.
2. Write a commit record to the log.
3. Flush the log page containing the commit record.

Algo after swapped
2. Write a commit record to the log.
1. Flush the transaction’s modified buffers to disk.
3. Flush the log page containing the commit record.
```

writing the commit record before flushing the buffers to disk creates a vulnerable window. 
- Imagine the system crashes after the commit record is written but before the buffer flush completes. 
- the log indicates a successful commit, but the actual data changes might not be fully written to disk. Upon recovery, the system would see the commit record and assume those changes are already persistent, leading to data inconsistency

Bad scenario
1. (start txn) A transaction modifies data in block 'A', changing a value from 10 to 20. change only in mem buffer.
2. (step 2) The commit record for the transaction is written to the log.
3. (step 1) Flush modified buffers to disk (step 1). Not yet flush log page.
4. (crash) db crashed before `step 3` (flush log page to disk)
5. db restarts
6. `undo-only` recovery manager sees commit record for the transaction and assumes the changes to block 'A' are persistent. It does not attempt to redo them. However, the actual value on disk in block 'A' might still be 10 (the old value), leading to incorrect data.

### 5.20 recovery process

Suppose that the recovery manager finds the following records in the log file when the system restarts after a crash.

``` 
<START, 1>
<START, 2>
<SETSTRING, 2, junk, 33, 0, abc, def> 
<SETSTRING, 1, junk, 44, 0, abc, xyz> 
<START, 3>
<COMMIT, 2>
<SETSTRING, 3, junk, 33, 0, def, joe> 
<START, 4>
<SETSTRING, 4, junk, 55, 0, abc, sue> 
<NQCKPT, 1, 3, 4> # non-quiescent checkpoint
<SETSTRING, 4, junk, 55, 0, sue, max> 
<START, 5>
<COMMIT, 4>


(a) Assuming undo-redo recovery, indicate what changes to the database will be performed.
(b) Assuming undo-only recovery, indicate what changes to the database will be performed.
(c) Is it possible for transaction T1 to have committed, even though it has no commit record in the log?
(d) Is it possible for transaction T1 to have modified a buffer containing block 23?
(e) Is it possible for transaction T1 to have modified block 23 on disk?
(f) Is it possible for transaction T1 to have not modified a buffer containing block 44?
```

--- 
Answer

> (a) Assuming undo-redo recovery, indicate what changes to the database will be performed.



> (b) Assuming undo-only recovery, indicate what changes to the database will be performed.

Recovery actions
1. iterate from log end (latest record)
2. see `COMMIT, 4` -> add 4 to finished txns list
3. see `START, 5` -> startRecord.undo(tx 5)
4. see `SETSTRING, 4, junk, 55, 0, sue, max` -> tx 4 finished -> do nothing
5. see `NQCKPT, 1, 3, 4` -> txns 1,3,4 were running when checkpointing
   - determine which txns still stunning: 1,3 as 4 in finished list
   - mark that it can ignore records before `START, 1` as 1 < 3
6. see `<SETSTRING, 4, junk, 55, 0, abc, sue>` -> ignore as txn 4 in finished
7. `start, 4` -> ignore
8. `<SETSTRING, 3, junk, 33, 0, def, joe>` -> undo as txn 3 not finished
   - setRecord.undo(txn 3)
   - write old value `def` to offset 0 of block 33 of file `junk`
9. `<COMMIT, 2>` -> add txn 2 of finished txns list
10. finished txns: [4, 2]
11. `start, 3` -> startRecord.undo(txn 3)
12. `<SETSTRING, 1, junk, 44, 0, abc, xyz>` -> undo as txn 1 not finished
13. `<SETSTRING, 2, junk, 33, 0, abc, def>`
14. `start, 2` -> ignore as 2 in finished txns
15. `start, 1` -> stop

`undo-only` recovery assumes that changes made by committed txn are flushed to disk before txn commit -> it only needs to undo changes made by uncommitted txns.

Algo for committing transaction with `undo-only` recovery
1. flush transaction's modified buffers to disk
2. write a commit record to the log
3. flush log page containing commit record
4. return to client txn committed

> (c) Is it possible for transaction T1 to have committed, even though it has no commit record in the log?
- if committed means returned committed ack to client, then No.
- if committed means a commit request is received and being executed by db -> Yes
- if committed means changes written to disk db files -> Yes
- example
  - non-quiescent checkpoint `<NQCKPT, 1, 3, 4>` -> T1 was active when the checkpoint was taken. 
  - If T1 committed shortly after the checkpoint and the system crashed before the commit record was written, it would appear as uncommitted during recovery.
  - recovery undo txn 1 operations
    - read block which was modified and written to disk to mem buffer
    - rollback the change to old value
    - write page/ block to disk
    - add rollback log record
    - write page of log transaction to disk

From the book:

The recovery manager assumes that a transaction completed if the log file contains a commit or rollback record for it. So if a transaction had committed prior to the system crash but its commit record did not make it to the log file, then the recovery manager will treat the transaction as if it did not complete. This situation might not seem fair, but there is really nothing else that the recovery manager can do. All it knows is what is contained in the log file, because everything else about the transaction was wiped out in the system crash.


> (d) Is it possible for transaction T1 to have modified a buffer containing block 23?

Yes, it's possible because `LogMgr.append()` does not flush the log record of the modification immediately to disk. It only make sure to flush when T1 commits which has not happened.

Sequence of Actions Modifying a Value
1.  **Transaction Initiates Modification:** A `Transaction` using the `setInt` or `setString` methods to modify data in the database.
2.  **Lock Acquisition:** To isolate its access, the transaction obtains an exclusive lock (`xLock`) on the relevant block from the `ConcurrencyMgr`. This lock prevents any other concurrent transaction from accessing the block.
3.  **Log Record Generation:** *Before directly mutating the buffer*, the transaction interacts with the `RecoveryMgr`.  The `recoveryMgr.setInt` or `recoveryMgr.setString` methods are called. These methods within `RecoveryMgr` don't immediately modify the buffer. Their crucial role is to generate a log record—either a `SetIntRecord` or `SetStringRecord`—that encapsulates the modification details: old value, new value, and the modification's location.
4.  **Log Append:** The generated log record is then appended to the log file on disk through the `LogMgr.append()` method.  Importantly, at this step, the log record is **not guaranteed** to be immediately written to disk. The `LogMgr` uses a buffered approach, accumulating records in memory (`logpage`) before writing them to disk. `this step does not flush to disk immediately, that's why the answer to (d) is yes it is possible`
5.  **Buffer Modification:** Only after log record generation and appending does the transaction modify the value within the buffer. using the `Page` class's `p.setInt` or `p.setString` methods.
6.  **Marking the Buffer:**  The buffer is then marked as modified via `buff.setModified(txnum, lsn)`. This links the modification to the transaction ID (`txnum`) and, crucially, the LSN (log sequence number) of the generated log record.
7.  **Ensuring Log Durability:** Although `LogMgr.append()` doesn't inherently flush to disk, SimpleDB guarantees log durability at key points:
   - **During Commit (`RecoveryMgr.commit()`):**  *Crucially*, when a transaction commits, SimpleDB  **flushes all modified buffers to disk**  *before*  writing the commit record to the log. **Critically, it then flushes the log page containing the commit record**.  This ensures both the transaction's modifications and the commit record itself are safely on disk, [1, 2].
   - Forced Flushing (`LogMgr.flush(int lsn)`): The `LogMgr` allows forcing a specific log record (and any preceding ones) to be written to disk, providing an extra layer of control.

> (e) Is it possible for transaction T1 to have modified block 23 on disk?

is there any case where the buffer was flushed to disk without log record being flushed?
- If no, not possible. because for T1 to have modified block 23 on disk, the setRecord needs to be on disk first, and we will see it in the log. Here we don't see a txn1 record in the log.
- If yes, possible.

> (f) Is it possible for transaction T1 to have not modified a buffer containing block 44?

No. 
Because we have this setRecord `<SETSTRING, 1, junk, 44, 0, abc, xyz>` which means txn 1 set offset 0 of block 44 from abc to xyz in file junk.
Having it in the log means that the log page was flushed.


### 5.35 LockTable synchronized methods

> Explain why the lock/unlock methods in class LockTable are **synchronized**. 
- to avoid race condition between threads
- to make sure that lock/ unlock is executed atomically by threads
- meaning that only one thread can execute the whole method at a time, next thread is waiting for other thread to finish before executing

> What bad thing could happen if they were not?
- lost updates: one thread is executing the lock method but not yet finished, other thread is faster and mutate lock state, overwriting former transaction.
- inconsistent state: incorrect lock count for shared lock
- deadlock
- phantom read: a thread got an inconsistent state of lock table if other thread is mutating it concurrently.

Example of a bad scenario of 2 threads requesting lock on same block
``` 
Thread 1: Checks if block is unlocked
Thread 2: Checks if block is unlocked
Thread 1: Sees it's unlocked, proceeds to set lock
Thread 2: Also sees it's unlocked (race condition), proceeds to set lock
Result: Both threads think they have an exclusive lock
```

With `synchronized`, race condition is avoided
``` 
Thread 1: Checks if block is unlocked
Thread 2: Checks if block is unlocked.
JVM: sees synchronized, makes Thread 2 wait for Thread 1 to finish whole method
Thread 1: Sees it's unlocked, proceeds to set lock
Thread 2: Executes lock method, sees it's acquire, wait for it to be released.
```