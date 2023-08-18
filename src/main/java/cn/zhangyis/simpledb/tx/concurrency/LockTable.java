package cn.zhangyis.simpledb.tx.concurrency;

import cn.zhangyis.simpledb.file.BlockId;

import java.util.HashMap;
import java.util.Map;

public class LockTable {

    /**
     * 最长锁表时间
     */
    private static final Long MAX_TIME = 10000L;

    /**
     * 每个block的锁的标识
     */
    private Map<BlockId, Integer> locks = new HashMap<>();

    /**
     * 尝试获取s锁
     *
     * @param blk
     */
    public synchronized void sLock(BlockId blk) {
        try {
            long timestamp = System.currentTimeMillis();
            //尝试获取s锁
            while (hasXlock(blk) && !waitingTooLong(timestamp)) {
                wait(MAX_TIME);
            }
            if (hasXlock(blk)) {
                throw new LockAbortException();
            }

            int val = getLockVal(blk);
            locks.put(blk, val + 1);
        } catch (InterruptedException e) {
            throw new LockAbortException();
        }
    }

    /**
     * 尝试获取x锁，如果有其他锁存在，则等待
     *
     * @param blk
     */
    public synchronized void xLock(BlockId blk) {
        long timestamp = System.currentTimeMillis();
        try {
            while (hasOtherSLocks(blk) && !waitingTooLong(timestamp)) {
                wait(MAX_TIME);
            }
            if (hasOtherSLocks(blk)) {
                throw new LockAbortException();
            }
            locks.put(blk, -1);
        } catch (InterruptedException e) {
            throw new LockAbortException();
        }
    }

    /**
     * 解锁，如果现在是x锁可以让其他线程获取锁，如果是s锁，则都可以进自然竞争
     * @param blk
     */
    public synchronized void unLock(BlockId blk) {
        int val = getLockVal(blk);
        if (val > 1) {
            locks.put(blk, val - 1);
        } else {
            locks.remove(blk);
            notifyAll();
        }
    }

    private boolean hasOtherSLocks(BlockId blk) {
        return getLockVal(blk) > 0;
    }

    private boolean waitingTooLong(long timestamp) {
        return System.currentTimeMillis() - timestamp > MAX_TIME;
    }

    private boolean hasXlock(BlockId blk) {
        return getLockVal(blk) < 0;
    }

    private int getLockVal(BlockId blk) {
        Integer iVal = locks.get(blk);
        return (iVal == null) ? 0 : iVal;
    }
}
