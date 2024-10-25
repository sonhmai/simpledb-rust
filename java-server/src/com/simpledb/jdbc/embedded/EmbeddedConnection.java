package com.simpledb.jdbc.embedded;

import com.simpledb.jdbc.ConnectionAdapter;
import com.simpledb.server.SimpleDB;
import com.simpledb.tx.Transaction;

import java.sql.SQLException;

public class EmbeddedConnection extends ConnectionAdapter {
    private SimpleDB db;
    private Transaction currentTx;

    public EmbeddedConnection(SimpleDB db) {
        this.db = db;
        this.currentTx = db.newTx();
    }

    @Override
    public void close() throws SQLException {
        currentTx.commit();
    }

    @Override
    public void commit() throws SQLException {
        currentTx.commit();
        this.currentTx = db.newTx();
    }

    @Override
    public void rollback() throws SQLException {
        currentTx.rollback();
        this.currentTx = db.newTx();
    }
}