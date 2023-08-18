package cn.zhangyis.simpledb.tx.concurrency;


/**
 * 事物的并发控制，每个事物都有一个并发控制器，用于管理事物的锁和事物的状态
 */
public class ConcurrencyMgr {

    /**
     * 全局共享
     */
    private static LockTable lockTable = new LockTable();

}








