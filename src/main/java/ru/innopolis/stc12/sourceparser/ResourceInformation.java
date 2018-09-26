package ru.innopolis.stc12.sourceparser;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ResourceInformation {
    private static final Logger LOGGER = Logger.getLogger(ResourceInformation.class);
    private final Integer LARGE_FILE_SIZE = 10_485_760;
    private int length;
    private final Integer SMALL_FILE_SIZE = 51_200;
    private InputStream inputStream = null;

    public ResourceInformation() {
    }

    public ResourceInformation(String resource) throws IOException {
        if (!setResource(resource)) {
            LOGGER.error("resource is not found");
            throw new IOException("resource is not found");
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (inputStream != null) {
            inputStream.close();
        }
    }

    private boolean init(String resource) throws IOException {
        if (resource == null) {
            return false;
        }
        if (inputStream != null) {
            inputStream.close();
            LOGGER.info("resource is released");
        }
        URL url = new URL(resource);
        inputStream = url.openStream();
        length = inputStream.available();
        LOGGER.info("a new resource is set <" + resource + ">");
        return true;
    }

    public boolean setResource(String resource) throws IOException {
        return init(resource);
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public FileType getFileType() {
        if (length > LARGE_FILE_SIZE) {
            return FileType.LARGE;
        }
        if (length <= SMALL_FILE_SIZE) {
            return FileType.SMALL;
        } else {
            return FileType.MEDIUM;
        }
    }

    public int getLength() {
        return length;
    }
}
