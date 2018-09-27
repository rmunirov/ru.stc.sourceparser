package ru.innopolis.stc12.sourceparser;

import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
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
    private List<ByteArrayOutputStream> buffers = new ArrayList<>();
    private ParserExecutor parserExecutor = new ParserExecutor();
    private ByteArrayOutputStream bufferForSmallFiles = new ByteArrayOutputStream(MAX_BUFFER_SIZE_FOR_SMALL_FILE);
    private SourceBuffer sourceBuffer;

    public SourceParser(SourceBuffer sourceBuffer) {
        this.sourceBuffer = sourceBuffer;
    }

    @Override
    public void getOccurencies(String[] sources, String[] words, String res) throws Exception {
        keys.addAll(Arrays.asList(words));
        LOGGER.info("filling the words set");
        LOGGER.info("set size = " + keys.size());
        ResultWriter resultWriter = new ResultWriter(result, res);
        resultWriter.start();
        for (String source : sources) {
            sourceBuffer.setSourceUrl(new URL(source));
            LOGGER.info("file selected: " + "<" + source + ">" + " size = " + sourceBuffer.getAvailableSize() +
                    " type: " + sourceBuffer.getSourceType().toString());
            switch (sourceBuffer.getSourceType()) {
                case LARGE:
                    processLargeFile(sourceBuffer);
                    break;
                case MEDIUM:
                    processMediumFile(sourceBuffer);
                    break;
                case SMALL:
                    processSmallFile(sourceBuffer);
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

    private void processLargeFile(SourceBuffer sourceBuffer) throws Exception {
        int bufferSize = sourceBuffer.getSameBufferSizeOfParts(MAX_BUFFERS, MAX_BUFFER_SIZE_FOR_LARGE_FILE);
        LOGGER.info("buffer size = " + bufferSize);
        ByteArrayOutputStream buffer;
        while ((buffer = sourceBuffer.getNextBufferByDelimiter(bufferSize, Delimiters.endOfSentence)) != null) {
            buffers.add(buffer);
            LOGGER.info("large file buffer added in list");
            if (buffers.size() >= MAX_BUFFERS) {
                execute(buffers);
            }
        }
        execute(buffers);
    }

    private void processMediumFile(SourceBuffer sourceBuffer) throws IOException {
        buffers.add(sourceBuffer.getBuffer());
        LOGGER.info("medium file buffer added in list");
    }

    private void processSmallFile(SourceBuffer sourceBuffer) throws IOException {
        if (bufferForSmallFiles.size() >= MAX_BUFFER_SIZE_FOR_SMALL_FILE) {
            LOGGER.info("small files buffer added in list");
            buffers.add(bufferForSmallFiles);
            bufferForSmallFiles = new ByteArrayOutputStream(MAX_BUFFER_SIZE_FOR_SMALL_FILE);
        }
        bufferForSmallFiles.write(sourceBuffer.getBuffer().toByteArray());
    }

    private void execute(List<ByteArrayOutputStream> buffers) throws Exception {
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
            for (ByteArrayOutputStream buffer : buffers) {
                buffer.close();
            }
            buffers.clear();
            LOGGER.info("buffers close and clear");
        }
    }
}
