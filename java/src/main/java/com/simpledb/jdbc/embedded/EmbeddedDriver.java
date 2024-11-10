package com.simpledb.jdbc.embedded;

import com.simpledb.jdbc.DriverAdapter;
import com.simpledb.server.SimpleDB;

import java.sql.SQLException;
import java.util.Properties;

public class EmbeddedDriver extends DriverAdapter {

    @Override
    public EmbeddedConnection connect(String url, Properties info) throws SQLException {
        // example embedded url jdbc:simpledb:studentdb
        // remove the protocol piece "jdbc:simpledb:"
        String dbname = url.replace("jdbc:simpledb:", "");
        SimpleDB db = new SimpleDB(dbname);
        return new EmbeddedConnection(db);
    }
}
