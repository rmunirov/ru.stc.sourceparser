package ru.innopolis.stc12.sourceparser;

import org.apache.log4j.Logger;

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
        if (inputStream != null) {
            inputStream.close();
        }
        if (url == null) {
            throw new NullPointerException("url is null");
        }
        inputStream = url.openStream();
        LOGGER.info("inputStream is set");
    }

    public CustomByteBuffer getBuffer() throws IOException {
        if (inputStream == null) {
            LOGGER.warn("input stream is not set");
            return null;
        }
        return getBuffer(inputStream.available());
    }

    public CustomByteBuffer getBuffer(int size) throws IOException {
        if (inputStream == null) {
            LOGGER.warn("input stream is not set");
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
        CustomByteBuffer result = new CustomByteBuffer(size);
        result.write(buffer);
        return result;
    }

    public CustomByteBuffer getNextBufferByDelimiter(int size, Set delimiters) throws IOException {
        CustomByteBuffer result = getBuffer(size);
        if (result == null) {
            return null;
        }
        if (inputStream.available() == 0) {
            return result;
        }
        CustomByteBuffer bufferToDelimiter = new CustomByteBuffer();
        int symbol;
        while ((symbol = inputStream.read()) != -1) {
            bufferToDelimiter.write(symbol);
            if (delimiters.contains(symbol)) {
                break;
            }
        }
        result.write(bufferToDelimiter.getRawData());
        return result;
    }

    public int getAvailableSize() throws IOException {
        return inputStream.available();
    }

    public SourceType getSourceType(URL url) throws IOException {
        setSourceUrl(url);
        if (inputStream.available() > LARGE_FILE_SIZE) {
            return SourceType.LARGE;
        }
        if (inputStream.available() <= SMALL_FILE_SIZE) {
            return SourceType.SMALL;
        } else {
            return SourceType.MEDIUM;
        }
    }
}
