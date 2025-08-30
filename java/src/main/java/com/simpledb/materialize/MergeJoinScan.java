package com.simpledb.materialize;

import com.simpledb.query.Constant;
import com.simpledb.query.Scan;

public class MergeJoinScan implements Scan {

    public MergeJoinScan(Scan s1, Scan s2, String fldname1, String fldname2) {

    }

    @Override
    public void beforeFirst() {

    }

    @Override
    public boolean next() {
        return false;
    }

    @Override
    public int getInt(String fldname) {
        return 0;
    }

    @Override
    public String getString(String fldname) {
        return "";
    }

    @Override
    public Constant getVal(String fldname) {
        return null;
    }

    @Override
    public boolean hasField(String fldname) {
        return false;
    }

    @Override
    public void close() {

    }
}
