package ru.innopolis.stc12.sourceparser;

import java.io.ByteArrayOutputStream;

public class CustomByteBuffer extends ByteArrayOutputStream {
    public CustomByteBuffer() {
        super();
    }

    public CustomByteBuffer(int size) {
        super(size);
    }

    public byte[] getRawData() {
        return buf;
    }
}
