package cn.zhangyis.simpledb.file;

import lombok.Data;

/**
 * 文件的块标识符  一个文件有多个块组成，物理层面，每个快的大小和一个page的大小一样 一般为4k  mysql中为16k
 */
@Data
public class BlockId {
    private String fileName;
    private int blockNum;


    public BlockId(String fileName, int blockNum) {
        this.fileName = fileName;
        this.blockNum = blockNum;
    }


}
