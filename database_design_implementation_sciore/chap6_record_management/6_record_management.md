# 6 - Record Management

why?
- encode and decode what values are in a binary disk block.
- works at the record abstract level
  - how to iterate through the records
  - how to insert, update values in record

what is record manager responsible for?
- how to store fields in records
- how to store records in blocks
- how to access records in a file

questions to address when designing a `record manager`
1. should each record be placed entirely within one block?
2. will all records in a block be from the same table?
3. is each field representable using a predetermined number of bytes?
4. where should each field value be positioned within its record?

issues and solutions
1. variable-length fields, records -> `ID table`
2. spanned records? -> trade-off between reducing storage waste and implementation complexity
3. non-homogeneous records -> tag field
4. how to determine field offset in record -> byte padding or search

![img.png](img_spanned_vs_unspanned_records.png)

SimpleDB API for record management
```
Schema

Layout

TableScan
    public TableScan(Transaction tx, String tblname, Layout layout);
    
    public void close();
    public boolean hasField(String fieldname);
    
    // methods to establish the current record
    public void     beforeFirst();
    public boolean  next();
    public void     moveToRid(RID r);
    public void     insert();
    
    // methods to access current record
    public int    getInt(String fieldname);
    public String getString(String fieldname);
    public void   setInt(String fieldname, int val);
    public void   setString(String fieldname, String val);
    public RID    currentRid();
    public void   delete();

RID
```