package com.simpledb;

import com.simpledb.record.Layout;
import com.simpledb.record.Schema;
import com.simpledb.record.TableScan;
import com.simpledb.server.SimpleDB;
import com.simpledb.tx.Transaction;

public class TableScanTest {
  public static void main(String[] args) {
    SimpleDB db = new SimpleDB("testfolder", 400, 8);
    Transaction tx = db.newTx();

    Schema schema = new Schema();
    schema.addIntField("col-a");
    schema.addStringField("col-b");
    Layout layout = new Layout(schema);
    TableScan ts = new TableScan(tx, "table-name-test", layout);

    System.out.println("Filling table with 50 records.");
    ts.beforeFirst();
    for (int i = 0; i < 50; i++) {
      ts.insert();
      int n = (int) Math.round(Math.random() * 50);
      ts.setInt("col-a", n);
      ts.setString("col-b", "rec" + n);
      System.out.println("inserting into slot ");
    }

    System.out.println("Deleting records with A-values < 25.");
    int count = 0;
    ts.beforeFirst();
    while (ts.next()) {
      int a = ts.getInt("col-a");
      String b = ts.getString("col-b");
      if (a < 25) {
        count++;
        ts.delete();
        System.out.println("deleted (a=" + a + ", b=" + b);
      }
    }
    System.out.println(count + " records with A-values < 25 were deleted.\n");

    System.out.println("Remaining records: ");

    ts.close();
    tx.commit();
  }
}
