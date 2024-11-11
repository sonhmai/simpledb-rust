package com.simpledb.record;

/**
 * Layout contains physical info about a record.
 */
public class Layout {
  private final Schema schema;

  public Layout(Schema schema) {
    this.schema = schema;
  }

  public int slotSize() {
    return 1;
  }
}
