package cn.zhangyis.simpledb.log;

import cn.zhangyis.simpledb.file.BlockId;
import cn.zhangyis.simpledb.file.FileMgr;
import cn.zhangyis.simpledb.file.Page;

import java.util.Iterator;

public class LogIterator implements Iterator<byte[]> {
    private FileMgr fm;
    private BlockId blk;
    private Page p;
    private int currentpos;
    private int boundary;

    public LogIterator(FileMgr fm, BlockId currentBlk) {
        this.fm = fm;
        this.blk = currentBlk;
        byte[] b = new byte[fm.getBlockSize()];
        p = new Page(b);
        moveToBlock(blk);
    }

    private void moveToBlock(BlockId blk) {
        fm.read(blk,p);
        currentpos = p.getInt(0);
        boundary = currentpos;
    }

    @Override
    public boolean hasNext() {
        return currentpos<fm.getBlockSize() || blk.getBlockNum()>0;
    }

    @Override
    public byte[] next() {
        if (currentpos == fm.getBlockSize()) {
            blk = new BlockId(blk.getFileName(), blk.getBlockNum()-1);
            moveToBlock(blk);
        }
        byte[] rec = p.getBytes(currentpos);
        currentpos += Integer.BYTES + rec.length;
        return rec;
    }
}
