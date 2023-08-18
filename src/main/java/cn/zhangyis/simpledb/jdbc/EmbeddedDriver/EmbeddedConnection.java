package cn.zhangyis.simpledb.jdbc.EmbeddedDriver;

import cn.zhangyis.simpledb.jdbc.ConnectionAdapter;
import cn.zhangyis.simpledb.server.SimpleDB;

public class EmbeddedConnection extends ConnectionAdapter {
    private SimpleDB db;

    public EmbeddedConnection(SimpleDB db) {
        this.db = db;
    }
}
