package cn.zhangyis.simpledb.buffer;

import cn.zhangyis.simpledb.file.BlockId;
import cn.zhangyis.simpledb.file.FileMgr;
import cn.zhangyis.simpledb.file.Page;
import cn.zhangyis.simpledb.log.LogMgr;

public class Buffer {

    private FileMgr fm;
    private LogMgr lm;

    private Page contents;
    private BlockId blk = null;

    private int pins = 0;
    private int txnum = -1;
    private int lsn = -1;

    public Buffer(FileMgr fm, LogMgr lm) {
        this.fm = fm;
        this.lm = lm;
        contents = new Page(fm.getBlockSize());
    }

    public Page contents() {
        return contents;
    }

    public BlockId block() {
        return blk;
    }

    public void setModified(int txnum, int lsn) {
        this.txnum = txnum;
        if (lsn >= 0)
            this.lsn = lsn;
    }

    public int modifyingTx() {
        return txnum;
    }

    /**
     * 将指定快的内容读入缓存中  如果缓存中的内容被修改过，之前的内容先写入磁盘
     *
     * @param blk
     */
    public void assignToBlock(BlockId blk) {
        flush();
        this.blk = blk;
        fm.read(blk, contents);
        pins = 0;
    }

    public void flush() {
        if (txnum >= 0) {
            lm.flush(lsn);
            fm.write(blk, contents);
            txnum = -1;
        }
    }

    /**
     * 如果当前缓存块被固定，则返回true
     *
     * @return
     */
    public boolean isPinned() {
        return pins > 0;
    }


    /**
     * Increase the buffer's pin count.
     */
    void pin() {
        pins++;
    }

    /**
     * Decrease the buffer's pin count.
     */
    void unpin() {
        pins--;
    }
}
