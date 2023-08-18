package cn.zhangyis.simpledb.tx.recovery;

import cn.zhangyis.simpledb.file.BlockId;
import cn.zhangyis.simpledb.file.Page;
import cn.zhangyis.simpledb.tx.Transaction;

public class SetIntRecord implements LogRecord {
    private int txnum, offset, val;
    private BlockId blk;

    /**
     * 构造一个设置整数记录。
     *
     * @param page
     */
    public SetIntRecord(Page page) {
        //获取事务id
        txnum = page.getInt(Integer.BYTES);
        //获取块id
        String filename = page.getString(2 * Integer.BYTES);

    }

    @Override
    public int op() {
        return SETINT;
    }

    @Override
    public int txNumber() {
        return txnum;
    }

    @Override
    public void undo(Transaction tx) {

    }
}
