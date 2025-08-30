package com.simpledb.plan;

import com.simpledb.materialize.MergeJoinScan;
import com.simpledb.materialize.SortPlan;
import com.simpledb.materialize.SortScan;
import com.simpledb.query.Scan;
import com.simpledb.record.Schema;
import com.simpledb.tx.Transaction;

import java.util.Collections;
import java.util.List;

public class MergeJoinPlan implements Plan {
    private final Plan leftSortedPlan;
    private final Plan rightSortedPlan;
    private final String fldname1;
    private final String fldname2;
    private final Schema schema = new Schema();

    // TODO: improve this join should work with two keys as well
    public MergeJoinPlan(Transaction tx,
                         Plan leftInputPlan, Plan rightInputPlan,
                         String fldname1, String fldname2) {
        // Why need to wrap the input plans in SortPlan?
        //  Because the MergeJoinPlan requires both input plans to be sorted on the join fields.
        //  The SortPlan is a logical plan that sorts the input plans based on the specified fields
        //  and provides a sorted iterator (SortScan) for the MergeJoinScan to consume.
        //  The SortPlan does not materialize the data but provides a sorted view of the input.
        this.fldname1 = fldname1;
        List<String> sortList1 = Collections.singletonList(fldname1);
        this.leftSortedPlan = new SortPlan(tx, leftInputPlan, sortList1);

        this.fldname2 = fldname2;
        List<String> sortList2 = Collections.singletonList(fldname2);
        this.rightSortedPlan = new SortPlan(tx, rightInputPlan, sortList2);

        schema.addAll(leftSortedPlan.schema());
        schema.addAll(rightSortedPlan.schema());
    }

    /**
     * Volcano/ Iterator model:
     * - Plan is logical recipe, stateless.
     * - A Plan describes how to produce a relation but doesn’t hold per-execution state.
     * - open() materializes a stateful iterator (Scan).
     * - Each call to open() creates a fresh iterator over that plan’s output.
     */
    @Override
    public Scan open() {
        // Why only s2 is casted to SortScan but not s1?
        Scan s1 = leftSortedPlan.open();
        SortScan s2 = (SortScan) rightSortedPlan.open();

        // We don't do the work in plan and return a MergeJoinScan here because we use the Volcano model
        // where the plan is logical and stateless.
        // The MergeJoinScan is the runtime/ physical implementation of the join operation.
        // It wraps two child iterators and implements the join’s runtime logic (next, getVal, hasField, close)
        // 1. walks both sorted streams in lockstep,
        // 2. uses SortScan features (mark/restore) on the right to replay equal-key groups,
        // 3. produces joined tuples to its caller.
        return new MergeJoinScan(s1, s2, fldname1, fldname2);
    }

    @Override
    public int blocksAccessed() {
        // The number of blocks accessed is the sum of the blocks accessed by each plan
        // because it's a single pass over each sorted input plan.
        // Any sorting cost is already included in SortPlan.blocksAccessed() for p1 and p2.
        // The join’s “mark/restore” on the right side is implemented by the scan (e.g., position bookmarks)
        // and is treated as CPU/iterator work, not extra disk I/O.
        return leftSortedPlan.blocksAccessed() + rightSortedPlan.blocksAccessed();
    }

    @Override
    public int recordsOutput() {
        int maxVals = Math.max(leftSortedPlan.distinctValues(fldname1),
                rightSortedPlan.distinctValues(fldname2));
        return (leftSortedPlan.recordsOutput() * rightSortedPlan.recordsOutput()) / maxVals;
    }

    @Override
    public int distinctValues(String fldname) {
        if (leftSortedPlan.schema().hasField(fldname))
            return leftSortedPlan.distinctValues(fldname);
        else
            return rightSortedPlan.distinctValues(fldname);
    }

    @Override
    public Schema schema() {
        return schema;
    }
}
