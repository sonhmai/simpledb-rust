

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

jdbc interface
```java
JDBC {
    interface Driver
    interface Connection
    interface Statement
    interface ResultSet // represent query output records
    interface ResultSetMetaData
}
```

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