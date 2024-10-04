# Chapter 5 Transaction Management

`Transaction`

`RecoveryManager`

`ConcurrenyManager`
- each `Transaction` has its own `Concurrency Manager` to manage concurrency
- pessimistic concurrency control using lock protocol `LockTable`
- lock table is shared amongst all concurrency managers.

### write path

```
seats(flight_id, num_available, price)
cust(cust_id, balance_due)

BEGIN;

COMMIT;
```

```mermaid
sequenceDiagram
    title transaction commit
    
    participant client
    participant txn as Transaction
    participant cm as ConcurrencyManager
    participant rm as RecoveryManager
    participant fm as FileManager
    participant lm as LogManager
    participant bm as BufferManager
    
    client ->> txn: new(fm, lm, bm)
    client ->> txn: commit()
    activate txn
    txn ->> rm: commit()
    rm -->> txn: done
    txn ->> cm: release()
    cm -->> txn: done
    txn ->> txn: unpin all buffers
    deactivate txn

```