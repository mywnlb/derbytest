package cn.zhangyis.simpledb.tx.recovery;

import cn.zhangyis.simpledb.file.Page;
import cn.zhangyis.simpledb.tx.Transaction;

/**
 * 日志记录接口
 */
public interface LogRecord {
    int CHECKPOINT = 0, START = 1,
            COMMIT = 2, ROLLBACK = 3,
            SETINT = 4, SETSTRING = 5;


    /**
     * 返回日志记录的类型
     * @return
     */
    int op();


    /**
     * 返回日志记录中存储的事务id
     * @return
     */
    int txNumber();

    /**
     * 撤销日志记录中编码的操作
     * @param tx
     */
    void undo(Transaction tx);


    /**
     * 解释日志迭代器返回的字节
     * @param bytes
     * @return
     */
    static LogRecord createLogRecord(byte[] bytes) {
        Page page = new Page(bytes);
        switch (page.getInt(0)) {
            case CHECKPOINT:
                return new CheckpointRecord();
            case START:
                return new StartRecord(page);
            case COMMIT:
                return new CommitRecord(page);
            case ROLLBACK:
                return new RollbackRecord(page);
            case SETINT:
                return new SetIntRecord(page);
            case SETSTRING:
                return new SetStringRecord(page);
            default:
                return null;
        }
    }
}
