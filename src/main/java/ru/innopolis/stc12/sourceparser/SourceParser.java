package ru.innopolis.stc12.sourceparser;

import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SourceParser implements Parser {
    private static final Logger LOGGER = Logger.getLogger(Parser.class);
    private final Integer MAX_BUFFERS = 10;
    private final Integer MAX_BUFFER_SIZE_FOR_LARGE_FILE = 10_485_760;
    private final Integer MAX_BUFFER_SIZE_FOR_SMALL_FILE = 1_048_576;
    private Set<String> keys = new TreeSet<>();
    private BlockingQueue<String> result = new LinkedBlockingQueue<>();
    private List<ByteArrayBuffer> buffers = new ArrayList<>();
    private ParserExecutor parserExecutor = new ParserExecutor();
    private ByteArrayBuffer bufferForSmallFiles = new ByteArrayBuffer(MAX_BUFFER_SIZE_FOR_SMALL_FILE);

    @Override
    public void getOccurencies(String[] sources, String[] words, String res) throws Exception {
        keys.addAll(Arrays.asList(words));
        LOGGER.info("filling the words set");
        LOGGER.info("set size = " + keys.size());
        ResultWriter resultWriter = new ResultWriter(result, res);
        resultWriter.start();
        ResourceInformation resourceInformation = new ResourceInformation();
        StreamInformation streamInformation = new StreamInformation();
        for (String source : sources) {
            resourceInformation.setResource(source);
            streamInformation.setInputStream(resourceInformation.getInputStream());
            LOGGER.info("file selected: " + "<" + source + ">" + " size = " + resourceInformation.getLength() +
                    " type: " + resourceInformation.getFileType().toString());
            switch (resourceInformation.getFileType()) {
                case LARGE:
                    processLargeFile(streamInformation);
                    break;
                case MEDIUM:
                    processMediumFile(streamInformation);
                    break;
                case SMALL:
                    processSmallFile(streamInformation);
                    break;
            }
            if (buffers.size() >= MAX_BUFFERS) {
                execute(buffers);
            }
        }
        if (bufferForSmallFiles.size() > 0) {
            buffers.add(bufferForSmallFiles);
        }
        execute(buffers);
        resultWriter.setFinish(true);
        resultWriter.join();
        LOGGER.info("parsing done");
    }

    private void processLargeFile(StreamInformation streamInformation) throws Exception {
        int bufferSize = streamInformation.getSameBufferSizeOfParts(MAX_BUFFERS, MAX_BUFFER_SIZE_FOR_LARGE_FILE);
        LOGGER.info("buffer size = " + bufferSize);
        ByteArrayBuffer buffer;
        while ((buffer = streamInformation.getNextBufferByDelimiter(bufferSize, Delimiters.endOfSentence)) != null) {
            buffers.add(buffer);
            LOGGER.info("large file buffer added in list");
            if (buffers.size() >= MAX_BUFFERS) {
                execute(buffers);
            }
        }
        execute(buffers);
    }

    private void processMediumFile(StreamInformation streamInformation) throws IOException {
        buffers.add(streamInformation.getBuffer());
        LOGGER.info("medium file buffer added in list");
    }

    private void processSmallFile(StreamInformation streamInformation) throws IOException {
        if (bufferForSmallFiles.size() >= MAX_BUFFER_SIZE_FOR_SMALL_FILE) {
            LOGGER.info("small files buffer added in list");
            buffers.add(bufferForSmallFiles);
            bufferForSmallFiles = new ByteArrayBuffer(MAX_BUFFER_SIZE_FOR_SMALL_FILE);
        }
        bufferForSmallFiles.write(streamInformation.getBuffer().getRawData());
    }

    private void execute(List<ByteArrayBuffer> buffers) throws Exception {
        if (buffers.isEmpty()) {
            return;
        }
        try {
            boolean done = parserExecutor.execute(buffers, new TreeSet<>(keys), result);
            if (!done) {
                throw new Exception("parse failed");
            }
        } catch (Exception e) {
            LOGGER.error(e);
            throw e;
        } finally {
            for (ByteArrayBuffer buffer : buffers) {
                buffer.close();
            }
            buffers.clear();
            LOGGER.info("buffers close and clear");
        }
    }
}
