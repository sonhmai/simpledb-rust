package com.simpledb.record;

/**
 * Schema contains a record's schema.
 * 1. name and type of each field
 * 2. length of each string field
 * <p>
 * It contains no physical info: length of a string is max number of characters allowed,
 * not its size in bytes.
 */
public class Schema {
  public Schema() {}

  public void addField(String fieldname, int type, int length) {}

  public void addIntField(String fieldname) {}

  public void addStringField(String fieldname) {}

  public void add(String fieldname, Schema sch) {}

  public void addAll(Schema sch) {}
}
