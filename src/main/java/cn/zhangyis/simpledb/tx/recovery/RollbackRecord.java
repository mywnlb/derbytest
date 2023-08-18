package cn.zhangyis.simpledb.tx.recovery;

import cn.zhangyis.simpledb.file.Page;
import cn.zhangyis.simpledb.log.LogMgr;
import cn.zhangyis.simpledb.tx.Transaction;

public class RollbackRecord implements LogRecord {
    private final int txnum;

    public RollbackRecord(Page page) {
        txnum = page.getInt(Integer.BYTES);
    }

    @Override
    public int op() {
        return ROLLBACK;
    }

    @Override
    public int txNumber() {
        return txnum;
    }

    @Override
    public void undo(Transaction tx) {

    }

    public String toString() {
        return "<ROLLBACK " + txnum + ">";
    }

    /**
     * 一个将回滚记录写入日志的静态方法。
     * @param lm
     * @param txnum
     * @return
     */
    public static int writeToLog(LogMgr lm, int txnum) {
        byte[] rec = new byte[2 * Integer.BYTES];
        Page p = new Page(rec);
        p.setInt(0, ROLLBACK);
        p.setInt(Integer.BYTES, txnum);
        return lm.append(rec);
    }

}
