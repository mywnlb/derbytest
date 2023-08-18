package cn.zhangyis.simpledb.tx.concurrency;


import cn.zhangyis.simpledb.file.BlockId;

import java.util.Map;

/**
 * 事物的并发控制，每个事物都有一个并发控制器，用于管理事物的锁和事物的状态
 */
public class ConcurrencyMgr {

    /**
     * 全局共享
     */
    private static LockTable lockTable = new LockTable();

    private Map<BlockId, String> locks = new java.util.HashMap<>();


    /**
     * 尝试给块加上S锁
     *
     * @param blk
     */
    public void sLock(BlockId blk) {
        if (locks.get(blk) == null) {
            lockTable.sLock(blk);
            locks.put(blk, "S");
        }
    }


    /**
     * 尝试给块加上X锁，尝试先给块加上S锁，然后再升级为X锁？
     *
     * @param blk
     */
    public void xLock(BlockId blk) {
        if (!hasXLock(blk)) {
            sLock(blk);
            lockTable.xLock(blk);
            locks.put(blk, "X");
        }
    }

    /**
     * 释放所有的锁
     */
    public void release() {
        for (BlockId blk : locks.keySet()) {
            lockTable.unLock(blk);
        }
        locks.clear();
    }
    private boolean hasXLock(BlockId blk) {
        return locks.get(blk) != null && locks.get(blk).equals("X");
    }

}








