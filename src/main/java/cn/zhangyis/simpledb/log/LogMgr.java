package cn.zhangyis.simpledb.log;

import cn.zhangyis.simpledb.file.BlockId;
import cn.zhangyis.simpledb.file.FileMgr;
import cn.zhangyis.simpledb.file.Page;

import java.util.Iterator;

/**
 * @description: 日志管理器
 */
public class LogMgr {

    private FileMgr fm;

    private String logFile;

    private Page logPage;

    private BlockId currentBlk;

    private int latestLSN = 0;

    private int lastSavedLSN = 0;

    public LogMgr(FileMgr fm,String logFile){
        this.fm = fm;
        this.logFile = logFile;

        logPage = new Page(fm.getBlockSize());

        int fileBlockNum = fm.getFileBlockNum(logFile);
        if(fileBlockNum == 0){
            currentBlk = appendNewBlock();
        }else {
            currentBlk = new BlockId(logFile,fileBlockNum - 1);
            fm.read(currentBlk,logPage);
        }
    }

    public Iterator<byte[]> iterator(){
        flush();
        return new LogIterator(fm,currentBlk);
    }

    public void flush(int lsn){
        if(lsn >= lastSavedLSN){
            flush();
        }
    }


    private void flush() {
        fm.write(currentBlk,logPage);
        lastSavedLSN = latestLSN;
    }

    private BlockId appendNewBlock() {
        BlockId blk = fm.append(logFile);
        logPage.setInt(0,fm.getBlockSize());
        fm.write(blk,logPage);
        return blk;
    }

    /**
     * Appends a log record to the log buffer.
     * The record consists of an arbitrary array of bytes.
     * Log records are written right to left in the buffer.
     * The size of the record is written before the bytes.
     * The beginning of the buffer contains the location
     * of the last-written record (the "boundary").
     * Storing the records backwards makes it easy to read
     * them in reverse order.
     * @param logrec a byte buffer containing the bytes.
     * @return the LSN of the final value
     */
    public synchronized int append(byte[] logrec) {
        int boundary = logPage.getInt(0);
        int recsize = logrec.length;
        int bytesneeded = recsize + Integer.BYTES;
        if (boundary - bytesneeded < Integer.BYTES) { // the log record doesn't fit,
            flush();        // so move to the next block.
            currentBlk = appendNewBlock();
            boundary = logPage.getInt(0);
        }
        int recpos = boundary - bytesneeded;

        logPage.setBytes(recpos, logrec);
        logPage.setInt(0, recpos); // the new boundary
        latestLSN += 1;
        return latestLSN;
    }
}
