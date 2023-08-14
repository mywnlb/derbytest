package cn.zhangyis.simpledb.log;

import cn.zhangyis.simpledb.file.BlockId;
import cn.zhangyis.simpledb.file.FileMgr;
import cn.zhangyis.simpledb.file.Page;

/**
 * @description: 日志管理器
 */
public class LogMgr {

    private FileMgr fm;

    private String logFile;

    private Page logPage;

    private BlockId currentBlk;

    private int latestLSN = 0;

    private int lastSavedLSN = 0;


}
