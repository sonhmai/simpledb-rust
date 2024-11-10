package com.simpledb.record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Schema contains a record's schema (logical info). Physical info is in Layout.
 * 1. name and type of each field
 * 2. length of each string field
 * <p>
 * It contains no physical info: length of a string is max number of characters allowed,
 * not its size in bytes.
 */
public class Schema {
  private final List<String> fields = new ArrayList<>();
  private final Map<String, FieldInfo> info = new HashMap<>();

  public Schema() {}

  public void addField(String fieldname, int type, int length) {
    fields.add(fieldname);
    info.put(fieldname, new FieldInfo(type, length));
  }

  public void addIntField(String fieldname) {}

  public void addStringField(String fieldname) {}

  public void add(String fieldname, Schema sch) {}

  public void addAll(Schema sch) {}

  public List<String> fields() {
    return fields;
  }

  public boolean hasField(String fieldname) {
    return info.containsKey(fieldname);
  }

  public int type(String fieldname) {
    return info.get(fieldname).type;
  }

  class FieldInfo {
    int type;
    int length;
    public FieldInfo(int type, int length) {
      this.type = type;
      this.length = length;
    }
  }
}
