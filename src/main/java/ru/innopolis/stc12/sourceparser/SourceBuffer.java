package ru.innopolis.stc12.sourceparser;

import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

public class SourceBuffer {
    private static final Logger LOGGER = Logger.getLogger(SourceBuffer.class);
    private final Integer LARGE_FILE_SIZE = 10_485_760;
    private final Integer SMALL_FILE_SIZE = 51_200;
    private InputStream inputStream;

    public SourceBuffer(URL url) throws IOException {
        setSourceUrl(url);
    }

    public SourceBuffer() {
    }

    public void setSourceUrl(URL url) throws IOException {
        this.inputStream = url.openStream();
        LOGGER.info("inputStream is set");
    }

    public ByteArrayOutputStream getBuffer() throws IOException {
        if (inputStream == null) {
            LOGGER.warn("input stream is null");
            return null;
        }
        if (inputStream.available() == 0) {
            LOGGER.warn("input stream is empty");
            return null;
        }
        byte[] buffer = new byte[inputStream.available()];
        if (inputStream.read(buffer) == -1) {
            LOGGER.error("read data from inputStream is failed");
            return null;
        }
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        result.write(buffer);
        return result;
    }

    public ByteArrayOutputStream getBuffer(int size) throws IOException {
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
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        result.write(buffer);
        return result;
    }

    public ByteArrayOutputStream getNextBufferByDelimiter(int size, Set delimiters) throws IOException {
        ByteArrayOutputStream result = getBuffer(size);
        if (result == null) {
            return null;
        }
        if (inputStream.available() == 0) {
            return result;
        }
        ByteArrayOutputStream bufferWithDelimiter = new ByteArrayOutputStream();
        int symbol;
        while ((symbol = inputStream.read()) != -1) {
            bufferWithDelimiter.write(symbol);
            if (delimiters.contains(symbol)) {
                break;
            }
        }
        result.write(bufferWithDelimiter.toByteArray());
        return result;
    }

    public int getAvailableSize() throws IOException {
        return inputStream.available();
    }

    public FileType getSourceType() throws IOException {
        if (inputStream.available() > LARGE_FILE_SIZE) {
            return FileType.LARGE;
        }
        if (inputStream.available() <= SMALL_FILE_SIZE) {
            return FileType.SMALL;
        } else {
            return FileType.MEDIUM;
        }
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
