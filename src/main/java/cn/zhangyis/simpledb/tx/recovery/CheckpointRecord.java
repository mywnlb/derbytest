package cn.zhangyis.simpledb.tx.recovery;

import cn.zhangyis.simpledb.file.Page;
import cn.zhangyis.simpledb.log.LogMgr;
import cn.zhangyis.simpledb.tx.Transaction;


/**
 * 日志检查点接口
 */
public class CheckpointRecord implements LogRecord {

    public CheckpointRecord() {
    }

    @Override
    public int op() {
        return CHECKPOINT;
    }

    /**
     * 日志检查点记录没有关联的事务，所以该方法返回一个“虚拟的”负txid。
     * @return
     */
    @Override
    public int txNumber() {
        return -1;
    }

    /**
     * 不执行任何操作，因为检查点记录不包含任何撤销信息。
     * @param tx
     */
    @Override
    public void undo(Transaction tx) {

    }

    public String toString() {
        return "<CHECKPOINT>";
    }

    /**
     * 写入检查点记录到日志
     */
    public static int writeToLog(LogMgr lm) {
        byte[] rec = new byte[Integer.BYTES];
        Page p = new Page(rec);
        p.setInt(0, CHECKPOINT);
        return lm.append(rec);
    }
}
