
## 3.15
A database system often contains diagnostic routines.
1. Modify the class `FileMgr` so that it keeps useful statistics, such as the number of blocks read/written.
2. Add new method(s) to the class that will return these statistics.
3. Modify the methods `commit` and `rollback` of the class `RemoteConnectionImpl` (in the `simpledb.jdbc.network` package) so that they print these statistics.
4. Do the same for the class `EmbeddedConnection` (in the `simpledb.jdbc.embedded` package).

The result will be that the engine prints the statistics for each SQL statement it executes

## 3.16

## 3.17
