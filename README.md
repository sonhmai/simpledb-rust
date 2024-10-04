SimpleDB


based on the book [Database Design and Implementation, 2nd edition by Edward Sciore](https://link.springer.com/book/10.1007/978-3-030-33836-7)
and [Phil Eaton's book club](https://eatonphil.com/2024-database-design-and-implementation.html)

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