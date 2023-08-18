package cn.zhangyis.simpledb.server;

import cn.zhangyis.simpledb.buffer.BufferMgr;
import cn.zhangyis.simpledb.file.FileMgr;
import cn.zhangyis.simpledb.log.LogMgr;

import java.io.File;

public class SimpleDB {
    public static int BLOCK_SIZE = 400;
    public static int BUFFER_SIZE = 8;

    public static String LOG_FILE = "simpledb.log";

    private FileMgr fm;

    private LogMgr lm;

    private BufferMgr bm;

    public SimpleDB(String dirName) {
        this(dirName, BLOCK_SIZE, BUFFER_SIZE);
    }

    public SimpleDB(String dirName, int blockSize, int bufferSize) {
        fm = new FileMgr(new File(dirName), blockSize);
        lm = new LogMgr(fm, LOG_FILE);
        bm = new BufferMgr(fm, lm, bufferSize);
    }

    public FileMgr getFileMgr() {
        return fm;
    }
    public LogMgr getLogMgr() {
        return lm;
    }

    public BufferMgr getBufferMgr() {
        return bm;
    }
}
