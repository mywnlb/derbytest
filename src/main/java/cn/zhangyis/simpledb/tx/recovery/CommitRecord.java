package cn.zhangyis.simpledb.tx.recovery;

import cn.zhangyis.simpledb.file.Page;
import cn.zhangyis.simpledb.log.LogMgr;
import cn.zhangyis.simpledb.tx.Transaction;

public class CommitRecord implements LogRecord {
    private final int txnum;

    public CommitRecord(Page page) {
        txnum = page.getInt(Integer.BYTES);
    }

    @Override
    public int op() {
        return COMMIT;
    }

    @Override
    public int txNumber() {
        return txnum;
    }


    /**
     * 什么都不做，因为提交记录不包含任何撤消信息。
     * @param tx
     */
    @Override
    public void undo(Transaction tx) {

    }

    public String toString() {
        return "<COMMIT " + txnum + ">";
    }

    /**
     * 一个将提交记录写入日志的静态方法。
     * 此日志记录包含COMMIT运算符，后跟事务id。
     * @param lm
     * @param txnum
     * @return
     */
    public static int writeToLog(LogMgr lm, int txnum) {
        byte[] rec = new byte[2 * Integer.BYTES];
        Page p = new Page(rec);
        p.setInt(0, COMMIT);
        p.setInt(Integer.BYTES, txnum);
        return lm.append(rec);
    }
}
