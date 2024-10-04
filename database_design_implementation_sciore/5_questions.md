# Chapter 5 questions

from reading
- why locking at block level, not page or something else?

### 5.1



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