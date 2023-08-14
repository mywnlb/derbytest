package cn.zhangyis.simpledb.file;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 文件管理器
 */
public class FileMgr {
    /**
     * 文件目录
     */
    private File dbDirectory;

    /**
     * 块大小
     */
    private int blockSize;

    /**
     * 是否是新的文件
     */
    private boolean isNew;
    /**
     * 已经打开的文件
     */
    private Map<String, RandomAccessFile> openFiles = new HashMap<>();


    public FileMgr(File dbDirectory, int blockSize) {
        this.dbDirectory = dbDirectory;
        this.blockSize = blockSize;
        isNew = !dbDirectory.exists();

        // create the directory if the database is new
        if (isNew) {
            dbDirectory.mkdirs();
        }

        // remove any leftover temporary tables
        for (String filename : dbDirectory.list()) {
            if (filename.startsWith("temp")) {
                new File(dbDirectory, filename).delete();
            }
        }
    }

    /**
     * 将文件中某块写入page
     *
     * @param buk
     * @param page
     */
    public synchronized void read(BlockId buk, Page page) {
        try {
            RandomAccessFile file = getFile(buk.getFileName());
            file.seek(blockSize * buk.getBlockNum());
            file.getChannel().read(page.contents());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 蒋page中的数据写入文件
     *
     * @param buk
     * @param page
     */
    public synchronized void write(BlockId buk, Page page) {
        try {
            RandomAccessFile file = getFile(buk.getFileName());
            file.seek(blockSize * buk.getBlockNum());
            //全双工  nio
            file.getChannel().write(page.contents());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 追加一个文件的快返回引用
     *
     * @param fileName
     * @return
     */
    public synchronized BlockId append(String fileName) {
        int blockNum = getFileBlockNum(fileName);
        BlockId blockId = new BlockId(fileName, blockNum);
        try {
            byte[] data = new byte[blockSize];
            RandomAccessFile file = getFile(fileName);
            file.seek(blockNum*blockSize);
            //阻塞写入
            file.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return blockId;
    }

    private int getFileBlockNum(String fileName) {
        try {
            return (int) (getFile(fileName).length() / blockSize);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private RandomAccessFile getFile(String fileName) throws FileNotFoundException {
        RandomAccessFile file = openFiles.get(fileName);
        if (Objects.isNull(file)) {
            file = new RandomAccessFile(new File(dbDirectory, fileName), "rws");
            openFiles.put(fileName, file);
        }

        return file;
    }

    public boolean isNew(){
        return isNew;
    }

    public int getBlockSize(){
        return blockSize;
    }
}
