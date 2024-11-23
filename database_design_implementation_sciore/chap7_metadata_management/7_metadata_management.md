# Chapter 7: Metadata Management

- metadata
  - table
  - view
  - index
  - statistical
- metadata manager
- system catalog
- catalog tables
- table metadata
  - catalog table about table info
  - catalog table about field info
- view metadata
  - catalog table about views
- index metadata: name, indexed table, fields
- statistical metadata
  - size, value distribution of each table in db
  - 2 ways of statistics storage 
    - persistent in catalog table
    - or calculate from scratch when db restarts
- SQL standard defines information schema
  - standard set of views for accessing database metadata
  - db can store metadata freely, must provide standard API for accessing them
- accurate and detailed metadata important for query planning

simpledb implementation

```
pub MetadataMgr
    void createTable(..)
    void createView(..)
    void createIndex(..)
    // reads
    Layout getLayout(..)
    String getViewDef(..)
    Map<String,IndexInfo> getIndexInfo(..)
    StatInfo getStatInfo(..)

private TableMgr(Transaction tx, boolean isnew)
    void    createTable(..)
    Layout  getLayout(..)
    
private ViewMgr(Transaction tx, TableMgr tm, boolean isnew)
    void    createView(..)
    String  getViewDef(..)

private StatMgr
    StatInfo getStatInfo(String tableName, Layout lo, Transaction tx)
    StatInfo
        int blocksAccessed()
        int recordsOutput()
        int distinctValues(string fieldname)
    
private IndexMgr
    createIndex(String indexName, String tableName, String fieldName, Transaction tx)
    IndexInfo(String indexName, String tableName, String fieldName, Transaction tx)
        Index open()
        int blocksAccessed()
        int recordsOutput()
        int distinctValues(string fieldname)     
```

further readings
``` 
Bruno, N., & Chaudhuri, S. (2004). Conditional selectivity for statistics on query expressions. In Proceedings of the ACM SIGMOD Conference (pp. 311–322).
Gibbons, P., Matias, Y., & Poosala, V. (2002). Fast incremental maintenance of incremental histograms. ACM Transactions on Database Systems, 27(3), 261–298.
Gulutzan, P., & Pelzer, T. (1999). SQL-99 complete, really. Lawrence, KA: R&D Books.
Kreines, D. (2003). Oracle data dictionary pocket reference. Sebastopol, CA: O’Reilly.
Matias, Y., Vitter, J., & Wang, M. (1998). Wavelet-based histograms for selectivity estimation. In Proceedings of the ACM SIGMOD Conference (pp. 448–459). Stonebraker, M., Kreps, P., Wong, E., & Held, G. (1976). The design and
implementation of INGRES. ACM Transactions on Database Systems, 1(3), 189–222.
```