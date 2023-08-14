package cn.zhangyis.simpledb.server;

import cn.zhangyis.simpledb.file.FileMgr;

import java.io.File;

public class SimpleDB {
    public static int BLOCK_SIZE = 400;
    public static int BUFFER_SIZE = 8;

    public static String LOG_FILE = "simpledb.log";

    private FileMgr fm;


    public SimpleDB(String dirName) {
        this(dirName, BLOCK_SIZE, BUFFER_SIZE);
    }

    public SimpleDB(String dirName, int blockSize, int bufferSize) {
        fm = new FileMgr(new File(dirName), blockSize);
    }
}
