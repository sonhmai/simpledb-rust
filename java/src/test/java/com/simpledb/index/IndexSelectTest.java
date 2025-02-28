package com.simpledb.index;

import com.simpledb.index.planner.IndexSelectPlan;
import com.simpledb.metadata.IndexInfo;
import com.simpledb.metadata.MetadataMgr;
import com.simpledb.plan.*;
import com.simpledb.query.Constant;
import com.simpledb.query.Scan;
import com.simpledb.record.RID;
import com.simpledb.record.TableScan;
import com.simpledb.server.SimpleDB;
import com.simpledb.tx.Transaction;

import java.util.Map;

// Find the grades of student 6.
public class IndexSelectTest {
    public static void main(String[] args) {
        SimpleDB db = new SimpleDB("studentdb");
        MetadataMgr mdm = db.mdMgr();
        Transaction tx = db.newTx();

        // Find the index on StudentId.
        Map<String, IndexInfo> indexes = mdm.getIndexInfo("enroll", tx);
        if (!indexes.containsKey("studentid")) {
            throw new IllegalStateException("expect index studentid to be available (not null).");
        }

        IndexInfo sidIdx = indexes.get("studentid");
        System.out.println("sidIdx: " + sidIdx);

        // Get the plan for the Enroll table
        Plan enrollplan = new TablePlan(tx, "enroll", mdm);

        // Create the selection constant
        Constant c = new Constant(6);

        // Two different ways to use the index in simpledb:
        useIndexManually(sidIdx, enrollplan, c);
        useIndexScan(sidIdx, enrollplan, c);

        tx.commit();
    }

    private static void useIndexManually(IndexInfo indexInfo, Plan p, Constant constant) {
        // Open a scan on the table.
        TableScan s = (TableScan) p.open();  //must be a table scan
        Index idx = indexInfo.open();

        // Retrieve all index records having the specified constant value=6.
        idx.beforeFirst(constant);
        while (idx.next()) {
            // Use the datarid to go to the corresponding `Enroll` record.
            RID datarid = idx.getDataRid();
            s.moveToRid(datarid);  // table scans can move to a specified RID.
            System.out.println("Using index manually: " + s.getString("grade"));
        }
        idx.close();
        s.close();
    }

    private static void useIndexScan(IndexInfo ii, Plan p, Constant c) {
        // Open an index select scan on the enroll table.
        Plan idxplan = new IndexSelectPlan(p, ii, c);
        Scan s = idxplan.open();

        while (s.next()) {
            System.out.println(s.getString("grade"));
        }
        s.close();
    }
}
