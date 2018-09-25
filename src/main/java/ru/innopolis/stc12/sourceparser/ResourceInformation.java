package ru.innopolis.stc12.sourceparser;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ResourceInformation {
    private static Logger LOGGER = Logger.getLogger(ResourceInformation.class);
    private InputStream inputStream;
    private int length;

    public ResourceInformation() {
        inputStream = null;
        length = -1;
    }

    public ResourceInformation(String resource) throws IOException {
        setResource(resource);
    }

    private boolean init(String resource) throws IOException {
        if (resource == null) {
            return false;
        }
        if (inputStream != null) {
            inputStream.close();
        }
        URL url = new URL(resource);
        inputStream = url.openStream();
        length = inputStream.available();
        return true;
    }

    public boolean setResource(String resource) {
        try {
            return init(resource);
        } catch (IOException e) {
            LOGGER.error(e);
            return false;
        }
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public FileType getFileType() {
        return null;
    }

    public int getLength() {
        return length;
    }
}
