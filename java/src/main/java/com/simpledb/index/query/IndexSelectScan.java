package com.simpledb.index.query;

import com.simpledb.index.Index;
import com.simpledb.query.Constant;
import com.simpledb.query.Scan;
import com.simpledb.record.RID;
import com.simpledb.record.TableScan;

/**
 * The scan class corresponding to the select relational
 * algebra operator.
 * @author Edward Sciore
 */
public class IndexSelectScan implements Scan {
   private TableScan ts;
   private Index idx;
   private Constant val;
   
   /**
    * Creates an index select scan for the specified
    * index and selection constant.
    * @param idx the index
    * @param val the selection constant
    */
   public IndexSelectScan(TableScan ts, Index idx, Constant val) {
      this.ts  = ts;
      this.idx = idx;
      this.val = val;
      beforeFirst();
   }
   
   /**
    * Positions the scan before the first record,
    * which in this case means positioning the index
    * before the first instance of the selection constant.
    * @see Scan#beforeFirst()
    */
   public void beforeFirst() {
      idx.beforeFirst(val);
   }
   
   /**
    * Moves to the next record, which in this case means
    * moving the index to the next record satisfying the
    * selection constant, and returning false if there are
    * no more such index records.
    * If there is a next record, the method moves the 
    * tablescan to the corresponding data record.
    * @see Scan#next()
    */
   public boolean next() {
      boolean ok = idx.next();
      if (ok) {
         RID rid = idx.getDataRid();
         ts.moveToRid(rid);
      }
      return ok;
   }
   
   /**
    * Returns the value of the field of the current data record.
    * @see Scan#getInt(String)
    */
   public int getInt(String fldname) {
      return ts.getInt(fldname);
   }
   
   /**
    * Returns the value of the field of the current data record.
    * @see Scan#getString(String)
    */
   public String getString(String fldname) {
      return ts.getString(fldname);
   }
   
   /**
    * Returns the value of the field of the current data record.
    * @see Scan#getVal(String)
    */
   public Constant getVal(String fldname) {
      return ts.getVal(fldname);
   }
   
   /**
    * Returns whether the data record has the specified field.
    * @see Scan#hasField(String)
    */
   public boolean hasField(String fldname) {
      return ts.hasField(fldname);
   }
   
   /**
    * Closes the scan by closing the index and the tablescan.
    * @see Scan#close()
    */
   public void close() {
      idx.close();
      ts.close();
   }
}
