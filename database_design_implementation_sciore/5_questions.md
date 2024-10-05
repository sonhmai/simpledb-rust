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