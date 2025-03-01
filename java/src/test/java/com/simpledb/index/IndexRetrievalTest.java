package com.simpledb.index;

import com.simpledb.metadata.IndexInfo;
import com.simpledb.metadata.MetadataMgr;
import com.simpledb.plan.Plan;
import com.simpledb.plan.TablePlan;
import com.simpledb.query.Constant;
import com.simpledb.query.UpdateScan;
import com.simpledb.record.RID;
import com.simpledb.server.SimpleDB;
import com.simpledb.tx.Transaction;

import java.util.Map;

public class IndexRetrievalTest {
    public static void main(String[] args) {
        SimpleDB db = new SimpleDB("studentdb");
        Transaction tx = db.newTx();
        MetadataMgr mdm = db.mdMgr();

        // Open a scan on the data table.
        Plan studentplan = new TablePlan(tx, "student", mdm);
        UpdateScan studentscan = (UpdateScan) studentplan.open();

        // Open the index on MajorId.
        Map<String, IndexInfo> indexes = mdm.getIndexInfo("student", tx);
        IndexInfo ii = indexes.get("majorid");
        Index idx = ii.open();

        // Retrieve all index records having a dataval of 20.
        idx.beforeFirst(new Constant(20));
        while (idx.next()) {
            // Use the datarid to go to the corresponding STUDENT record.
            RID datarid = idx.getDataRid();
            studentscan.moveToRid(datarid);
            System.out.println(studentscan.getString("sname"));
        }

        // Close the index and the data table.
        idx.close();
        studentscan.close();
        tx.commit();
    }
}
