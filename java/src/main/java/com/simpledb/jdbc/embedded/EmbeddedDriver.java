package com.simpledb.jdbc.embedded;

import com.simpledb.jdbc.DriverAdapter;
import com.simpledb.server.SimpleDB;

import java.sql.SQLException;
import java.util.Properties;

/**
 * The RMI server-side implementation of RemoteDriver.
 * @author Edward Sciore
 */

public class EmbeddedDriver extends DriverAdapter {   
   /**
    * Creates a new RemoteConnectionImpl object and 
    * returns it.
    * @see com.simpledb.jdbc.network.RemoteDriver#connect()
    */
   public EmbeddedConnection connect(String url, Properties p) throws SQLException {
      String dbname = url.replace("jdbc:simpledb:", "");
      SimpleDB db = new SimpleDB(dbname);
      return new EmbeddedConnection(db);
   }
}

