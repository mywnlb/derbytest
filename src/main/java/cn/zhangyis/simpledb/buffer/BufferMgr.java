package cn.zhangyis.simpledb.buffer;

import cn.zhangyis.simpledb.file.BlockId;
import cn.zhangyis.simpledb.file.FileMgr;
import cn.zhangyis.simpledb.log.LogMgr;

public class BufferMgr {
    /**
     * 缓冲池
     */
    private Buffer[] bufferPool;

    /**
     * 可用模块
     */
    private int numAvailable;

    private static final long MAX_TIME = 10000; // 10 seconds

    /**
     * Creates a buffer manager having the specified number
     *
     * @param fm       文件管理器
     * @param lm       日志管理器
     * @param numbuffs 缓存池的大小
     */
    public BufferMgr(FileMgr fm, LogMgr lm, int numbuffs) {
        bufferPool = new Buffer[numbuffs];
        numAvailable = numbuffs;
        for (int i = 0; i < numbuffs; i++) {
            bufferPool[i] = new Buffer(fm, lm);
        }
    }

    /**
     * 返回可用的缓存池的大小
     *
     * @return
     */
    public synchronized int available() {
        return numAvailable;
    }

    /**
     * 刷新指定事务修改的脏缓存
     *
     * @param txnum
     */
    public synchronized void flushAll(int txnum) {
        for (Buffer buff : bufferPool) {
            if (buff.modifyingTx() == txnum) {
                buff.flush();
            }
        }
    }

    /**
     * 尝试将指定的缓存块固定到缓存池中的缓存中
     *
     * @param blk
     * @return
     */
    public synchronized Buffer pin(BlockId blk) {
        try {
            long currentTimeMillis = System.currentTimeMillis();
            Buffer buffer = tryToPin(blk);
            while (buffer == null && !waitingTooLong(currentTimeMillis)) {
                wait(MAX_TIME);
                buffer = tryToPin(blk);
            }

            if (buffer == null) {
                throw new BufferAbortException();
            }

            return buffer;
        } catch (Exception e) {
            throw new BufferAbortException();
        }
    }

    private Buffer tryToPin(BlockId blk) {
        //寻找是否有已经存在的缓存
        Buffer buffer = findExistingBuffer(blk);
        if(buffer == null){
            buffer = chooseUnpinnedBuffer();
            if(buffer == null){
                return null;
            }
            buffer.assignToBlock(blk);
        }

        if(!buffer.isPinned()){
            numAvailable--;
        }
        buffer.pin();
        return buffer;
    }

    private Buffer chooseUnpinnedBuffer() {
        for (Buffer buffer : bufferPool) {
            if (!buffer.isPinned()) {
                return buffer;
            }
        }
        return null;
    }

    private Buffer findExistingBuffer(BlockId blk) {
        for (Buffer buffer : bufferPool) {
            BlockId blockId = buffer.block();
            if (blockId != null && blockId.equals(blk)) {
                return buffer;
            }
        }
        return null;
    }

    private boolean waitingTooLong(long startTime) {
        return System.currentTimeMillis() - startTime > MAX_TIME;
    }

    public synchronized void unpin(Buffer buffer) {
        buffer.unpin();
        if (!buffer.isPinned()) {
            numAvailable++;
            notifyAll();
        }
    }
}
