package com.simpledb.record;

import com.simpledb.query.Constant;
import com.simpledb.query.UpdateScan;
import com.simpledb.tx.Transaction;

public class TableScan implements UpdateScan {

  private Transaction transaction;
  private String tableName;
  private Layout layout;
  private RecordPage rp;
  private int currentslot;

  public TableScan(Transaction tx, String tableName, Layout layout) {

  }

  public void delete() {
    rp.delete(currentslot);
  }

  @Override
  public RID getRid() {
    return null;
  }

  @Override
  public void moveToRid(RID rid) {

  }

  public void close() {

  }

  /**
   * Positions the current record before first record.
   * JDBC API has this method as well to position the result set at the same place.
   */
  public void beforeFirst() {}

  /** Positions current record at the next record in the file */
  public boolean next() {
    return true;
  }

  public void insert() {}

  public int getInt(String tablename) {
    return 1;
  }

  @Override
  public void setVal(String fldname, Constant val) {

  }

  public void setInt(String tablename, int value) {}

  public String getString(String tablename) {
    return "default";
  }

  @Override
  public Constant getVal(String fldname) {
    return null;
  }

  @Override
  public boolean hasField(String fldname) {
    return false;
  }

  public void setString(String tablename, String value) {}
}
