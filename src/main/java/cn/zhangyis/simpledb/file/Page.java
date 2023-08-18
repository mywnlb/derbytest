package cn.zhangyis.simpledb.file;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Page {
    private ByteBuffer buffer;
    public static Charset CHARSET = StandardCharsets.US_ASCII;

    public Page(int blockSize) {
        buffer = ByteBuffer.allocateDirect(blockSize);
    }

    public Page(byte[] b) {
        buffer = ByteBuffer.wrap(b);
    }

    public int getInt(int offset) {
        return buffer.getInt(offset);
    }

    public void setInt(int offset, int n) {
        buffer.putInt(offset, n);
    }

    public byte[] getBytes(int offset) {
        buffer.position(offset);
        int length = buffer.getInt();
        byte[] b = new byte[length];
        buffer.get(b);
        return b;
    }

    public void setBytes(int offset, byte[] b) {
        buffer.position(offset);
        buffer.putInt(b.length);
        buffer.put(b);
    }

    public String getString(int offset) {
        byte[] b = getBytes(offset);
        return new String(b, CHARSET);
    }

    public void setString(int offset, String s) {
        byte[] b = s.getBytes(CHARSET);
        setBytes(offset, b);
    }

    public static int maxLength(int strlen) {
        float bytesPerChar = CHARSET.newEncoder().maxBytesPerChar();
        return Integer.BYTES + (strlen * (int) bytesPerChar);
    }

    ByteBuffer contents() {
        buffer.position(0);
        return buffer;
    }
}
