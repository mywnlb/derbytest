package cn.zhangyis.simpledb.jdbc.EmbeddedDriver;

import cn.zhangyis.simpledb.jdbc.DriverAdapter;
import cn.zhangyis.simpledb.server.SimpleDB;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class EmbeddedDriver extends DriverAdapter {
    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        String dbName = StringUtils.substringAfter(url, "jdbc:simpledb:");
        SimpleDB db = new SimpleDB(dbName);

        return new EmbeddedConnection(db);
    }
}
