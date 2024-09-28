
## 2.2

> Write some SQL queries for the university database. For each query, write a program using Derby that executes that query and prints its output table.

not doing.

## 2.3

> The SimpleIJ program requires each SQL statement to be a single line of
text. Revise it so that a statement can comprise multiple lines and terminate with
a semicolon, similar to Derby’s ij program.

## 2.4 javax.sql.DataSource

> - Write a class `NetworkDataSource` for SimpleDB that works similarly to
the Derby class ClientDataSource. 
> - Add this class to the package simpledb.jdbc.network. 
> - Your code need not implement all of the methods of the interface javax.sql.DataSource (and its superclasses); in fact, the only one of those methods that it needs to implement is the no-arg method `getConnection()`. 
> - What vendor-specific methods should NetworkDataSource have?

## 2.5 sqls text file

It is often useful to be able to create a text file that contains SQL commands. These commands can then be executed in batch by a JDBC program. Write a JDBC program that reads commands from a specified text file and executes them. Assume that each line of the file is a separate command.

## 2.6

- Investigate how a result set can be used to populate a Java JTable object. 
- (Hint: You will need to extend the class AbstractTableModel.) 
- Then revise the demo client FindMajors to have a GUI interface that displays its output in a JTable.

## 2.7 JDBC

``` 
Write JDBC code for the following tasks:

(a) Import data from a text file into an existing table. 
The text file should have one record per line, with each field separated by tabs. 
The first line of the file should be the names of the fields. 
The client should take the name of the file and the name of the table as input, 
and insert the records into the table.
```

## 2.8 null values

- This chapter has ignored the possibility of null values in a result set. 
- To check for null values, you use the method `wasNull` in `ResultSet`. 
- Suppose you call `getInt` or `getString` to retrieve a field value. 
- If you call wasNull immediately afterward, it will return true if the retrieved value was null. 

For example, the following loop prints out graduation years, assuming that some of them might be null:

```java
while (rs.next()) {
    int gradyr = rs.getInt("gradyear");
    if (rs.wasNull())
        System.out.println("null");
    else
        System.out.println(gradyr);
}
```

(a) Rewrite the code for the StudentMajor demo client under the assumption that student names might be null.

(b) Modify the SimpleIJ demo client so that it connects to Derby (instead of SimpleDB). Then rewrite the code under the assumption that any field value might be null.

---


