package cn.zhangyis.simpledb.tx.recovery;

import cn.zhangyis.simpledb.file.Page;
import cn.zhangyis.simpledb.log.LogMgr;
import cn.zhangyis.simpledb.tx.Transaction;

public class StartRecord implements LogRecord {

    private int txnum;

    /**
     * 从日志中读取一个值，创建一个日志记录
     *
     * @param page
     */
    public StartRecord(Page page) {
        int tpos = Integer.BYTES;
        txnum = page.getInt(tpos);
    }

    @Override
    public int op() {
        return START;
    }

    @Override
    public int txNumber() {
        return txnum;
    }

    /**
     * 不执行任何操作，因为开始记录不包含任何撤销信息。
     *
     * @param tx
     */
    @Override
    public void undo(Transaction tx) {

    }

    public String toString() {
        return "<START " + txnum + ">";
    }

    /**
     * 写入开始记录到日志
     */
    public static int writeToLog(LogMgr lm, int txnum) {
        byte[] rec = new byte[2 * Integer.BYTES];
        Page p = new Page(rec);
        p.setInt(0, START);
        p.setInt(Integer.BYTES, txnum);
        return lm.append(rec);
    }
}
