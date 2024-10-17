

2.1.
```
- The Derby documentation recommends that you turn off autocommit when executing a sequence of inserts. 
- Explain why you think it makes this recommendation.
```

- to reduce the overhead to begin and commit a transaction for each insert statement.
- each transaction commit needs to hit the disk with transaction log so latency is high.
- instead open only 1 transaction, do all the inserts and commit.
