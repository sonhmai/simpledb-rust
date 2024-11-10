package com.simpledb.record;

import com.simpledb.tx.Transaction;

public class TableScan {

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

  public void setInt(String tablename, int value) {}

  public String getString(String tablename) {
    return "default";
  }

  public void setString(String tablename, String value) {}
}
