package com.simpledb.record;

import com.simpledb.file.BlockId;
import com.simpledb.tx.Transaction;

public class RecordPage {

  public static  final int EMPTY = 0, USED = 1; // 4-bytes empty/inuse flag

  private BlockId blk;
  private Layout layout; // layout of this record type in the page which applies for all records
  private Transaction tx;

  public int getInt(int slot, String fieldname) {
    return 1;
  }

  public String getString(int slot, String fieldname) {
    return "";
  }

  public void setInt(int slot, String fieldname, int value) {

  }

  public void setString(int slot, String fieldname, String value) {

  }

  /** Sets all record slots in a page to default values: empty/inuse flag to EMPTY, integers to 0,
   * strings to "" */
  public void format() {

  }

  /**
   * sets record flag to EMPTY.
   * @param slot index of the slot to set to empty
   */
  public void delete(int slot) {
    setFlag(slot, EMPTY);
  }

  /**
   * looks for a USED slot after this one.
   *
   * @param slot index of the slot to search after
   * @return -1 if not found used slot after current or offset to the slot if found
   */
  public int nextAfter(int slot) {
    return searchAfter(slot, USED); // search for a used slot after this one
  }

  public int insertAfter(int slot) {
    return 0;
  }

  private void setFlag(int slot, int flag) {
    tx.setInt(blk, offset(slot), flag, true);
  }

  private int searchAfter(int slot, int flag) {
    slot++;
    while (isValidSlot(slot)) {
      if (tx.getInt(blk, offset(slot)) == flag)
        return slot;
      slot++;
    }
    return -1;
  }

  private boolean isValidSlot(int slot) {
    return offset(slot + 1) <= tx.blockSize();
  }

  private int offset(int slot) {
    return slot * layout.slotSize();
  }
}
