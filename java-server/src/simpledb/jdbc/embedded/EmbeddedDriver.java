package simpledb.jdbc.embedded;

import simpledb.jdbc.DriverAdapter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class EmbeddedDriver extends DriverAdapter {

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        return null;
    }
}
