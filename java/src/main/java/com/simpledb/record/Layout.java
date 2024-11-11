package com.simpledb.record;

import com.simpledb.file.Page;

import java.util.HashMap;
import java.util.Map;

/**
 * Layout contains physical info about a record.
 */
public class Layout {

  private Map<String, Integer> offsets; // pre-calculated offsets of the fields within a record

  private final Schema schema;

  public Layout(Schema schema) {
    this.schema = schema;
    offsets = new HashMap<>();
    int pos = Integer.BYTES; // leave space for int 4B empty/inuse flag
    for (String fieldname: schema.fields()) {
      offsets.put(fieldname, pos);
      pos += lengthInBytes(fieldname);
    }
  }

  public int offset(String fieldname) {
    return offsets.get(fieldname);
  }

  public int slotSize() {
    return 1;
  }

  private int lengthInBytes(String fieldname) {
    int fieldType = schema.type(fieldname);
    if (fieldType == java.sql.Types.INTEGER)
      return Integer.BYTES;
    else // varchar
      return Page.maxLength(1); // TODO fix this and test
  }
}
