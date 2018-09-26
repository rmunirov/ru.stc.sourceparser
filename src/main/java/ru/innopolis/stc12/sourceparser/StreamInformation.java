package ru.innopolis.stc12.sourceparser;

import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class StreamInformation {
    private static final Logger LOGGER = Logger.getLogger(StreamInformation.class);
    private InputStream inputStream;

    public StreamInformation(InputStream inputStream) {
        setInputStream(inputStream);
    }

    public StreamInformation() {
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        LOGGER.info("inputStream is set");
    }

    public ByteArrayBuffer getBuffer() throws IOException {
        if (inputStream == null) {
            LOGGER.warn("input stream is null");
            return null;
        }
        if (inputStream.available() == 0) {
            LOGGER.warn("input stream is empty");
            return null;
        }
        ByteArrayBuffer result = new ByteArrayBuffer();
        result.write(inputStream);
        return result;
    }

    public ByteArrayBuffer getBuffer(int size) throws IOException {
        if (inputStream == null) {
            LOGGER.warn("input stream is null");
            return null;
        }
        if (inputStream.available() == 0) {
            LOGGER.warn("input stream is empty");
            return null;
        }
        if (size <= 0) {
            LOGGER.warn("the specified size is less than or equal to 0");
            return null;
        }
        if (size > inputStream.available()) {
            size = inputStream.available();
            LOGGER.info("the size variable is change");
        }
        byte[] buffer = new byte[size];
        if (inputStream.read(buffer) == -1) {
            LOGGER.error("read data from inputStream is failed");
            return null;
        }
        return new ByteArrayBuffer(buffer);
    }

    public ByteArrayBuffer getNextBufferByDelimiter(int size, Set delimiters) throws IOException {
        ByteArrayBuffer result = getBuffer(size);
        if (result == null) {
            return null;
        }
        if (inputStream.available() == 0) {
            return result;
        }
        ByteArrayBuffer bufferWithDelimiter = new ByteArrayBuffer();
        int symbol;
        while ((symbol = inputStream.read()) != -1) {
            bufferWithDelimiter.write(symbol);
            if (delimiters.contains(symbol)) {
                break;
            }
        }
        result.write(bufferWithDelimiter.getRawData());
        return result;
    }

    public int getAvailableSize() throws IOException {
        return inputStream.available();
    }

    public int getSameBufferSizeOfParts(int partCountOfDivider, int size) throws IOException {
        if (inputStream == null) {
            return -1;
        }
        if (inputStream.available() <= 0) {
            return -1;
        }
        int partCount = inputStream.available() / size + 1;
        if (partCount <= partCountOfDivider) {
            return inputStream.available() / partCount + 1;
        } else {
            return inputStream.available() / ((partCount / partCountOfDivider + 1) * partCountOfDivider) + 1;
        }
    }
}
