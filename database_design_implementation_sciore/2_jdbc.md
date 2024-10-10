

## Contents
- exploring JDBC functionality in `java.sql`
- basic JDBC
- advanced JDBC
  - hiding Driver
  - explicit transaction handling
  - transaction isolation levels
  - prepared statements
  - scrollable and updatable result sets
  - additional data types
- computing in Java vs SQL
- summary

## Summary
- JDBC methods manage transfer data btw Java client and db engine.
- JDBC consists of 5 interfaces: Driver, Connection, Statement, ResultSet, ResultSetMetaData
```java
JDBC {
    // Driver contains low level details for connecting with db engine.
    // driver class and conn strings are the only vendor-specific code in JDBC program. everthing else is vendor-neutral.
    interface Driver 
    
    // holds db resources other client might need. close them as soon as possible.
    interface Connection 
    
    interface Statement
    
    // represent query output records. holds db resources other client might need. 
    // close them as soon as possible.
    interface ResultSet 
    
    // provides output table schema info (fields name, type, display size)
    interface ResultSetMetaData 
}
```
- JDBC can throw SQLException. client must handle them.
- Full JDBC provides class `DriverManager` and `DataSource` to simplify connection process and make it more vendor-agnostic.
- JDBC defines 4 `isolation levels`. 
Client sets by `conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE)`
  1. Read-Uncommitted: no isolation. Problems: uncommitted data, nonreapeatable reads, phantom records.
  2. Read-Committed: no uncommitted data.
  3. Repeatable-Read: no nonreapeatable reads.
  4. Serializable: no phantom records.
- trade-off is speed. more isolation == slower concurrency.
- programmers analyze concurrency error risks -> make decision of which isolation level.


### ResultSet

```java
public class StudentMajor {
    public static void main(String[] args) {
        String url = "jdbc:derby://localhost/studentdb";
        String qry = "select SName, DName from DEPT, STUDENT " + "where MajorId = DId";
        Driver d = new ClientDriver();
        try (
            Connection conn = d.connect(url, null);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(qry);
                )
    }
}
```

reasons to push as much work as possible from client to db engine
- highly unlikely that a client can compute a query as efficiently as engine.