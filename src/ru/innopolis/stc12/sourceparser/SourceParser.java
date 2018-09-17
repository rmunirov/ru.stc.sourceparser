package ru.innopolis.stc12.sourceparser;

import com.sun.xml.internal.ws.util.ByteArrayBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SourceParser implements Parser {
    private final Integer MAX_BUFFERS = 10;
    private final Integer LARGE_FILE_SIZE = 10_485_760;
    private final Integer SMALL_FILE_SIZE = 51_200;
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
        ResultWriter resultWriter = new ResultWriter(result, res);
        resultWriter.start();
        for (String source : sources) {
            InputStream inputStream = getFileInputStream(source);
            Integer length = inputStream.available();
            FileType fileType = getFileType(length);
            switch (fileType) {
                case LARGE:
                    processLargeFile(inputStream, length);
                    break;
                case MEDIUM:
                    processMediumFile(inputStream, length);
                    break;
                case SMALL:
                    processSmallFile(inputStream);
                    break;
            }
            if (buffers.size() >= MAX_BUFFERS) {
                execute(buffers);
            }
        }
        execute(buffers);
        resultWriter.setFinish(true);
        resultWriter.join();
    }

    private InputStream getFileInputStream(String source) throws IOException {
        URL url = new URL(source);
        return url.openStream();
    }

    private FileType getFileType(Integer length) {
        if (length > LARGE_FILE_SIZE) {
            return FileType.LARGE;
        }
        if (length <= SMALL_FILE_SIZE) {
            return FileType.SMALL;
        } else {
            return FileType.MEDIUM;
        }
    }

    private void processLargeFile(InputStream inputStream, Integer length) throws Exception {
        int bufferSize = getBufferSize(length, MAX_BUFFER_SIZE_FOR_LARGE_FILE);
        while (inputStream.available() > 0) {
            buffers.add(getByteBufferWithEndSentence(inputStream, bufferSize));
            if (buffers.size() >= MAX_BUFFERS) {
                execute(buffers);
            }
        }
        execute(buffers);
    }

    private void processMediumFile(InputStream inputStream, Integer length) throws IOException {
        buffers.add(getByteBufferWithEndSentence(inputStream, length));
    }

    private void processSmallFile(InputStream inputStream) throws IOException {
        if (bufferForSmallFiles.size() >= MAX_BUFFER_SIZE_FOR_SMALL_FILE) {
            buffers.add(bufferForSmallFiles);
            bufferForSmallFiles = new ByteArrayBuffer(MAX_BUFFER_SIZE_FOR_SMALL_FILE);
        }
        bufferForSmallFiles.write(inputStream);
    }

    private int getBufferSize(int length, int maxBufferSize) {
        if (length <= 0) return -1;
        int partCount = length / maxBufferSize + 1;
        if (partCount <= MAX_BUFFERS) {
            return length / partCount + 1;
        } else {
            return length / ((partCount / MAX_BUFFERS + 1) * MAX_BUFFERS) + 1;
        }
    }

    private ByteArrayBuffer getByteBufferWithEndSentence(InputStream inputStream, int size) throws IOException {
        if (inputStream == null) return null;
        if (size <= 0) return null;

        int length = inputStream.available();
        if (length < size) {
            size = length;
        }

        byte[] bytes = new byte[size];
        if (inputStream.read(bytes) == -1) {
            throw new IOException("can't read data from stream");
        }
        //found end sentence
        ByteArrayBuffer buffer = new ByteArrayBuffer(bytes, bytes.length);
        ByteArrayBuffer sentenceEndBuffer = getEndSentence(inputStream);
        if (sentenceEndBuffer.size() > 0) {
            buffer.write(sentenceEndBuffer.getRawData());
        }

        return buffer;
    }

    private ByteArrayBuffer getEndSentence(InputStream inputStream) throws IOException {
        ByteArrayBuffer sentenceEndBuffer = new ByteArrayBuffer();
        int symbol;
        while ((symbol = inputStream.read()) != -1) {
            sentenceEndBuffer.write(symbol);
            if (symbol == '?' || symbol == '.' || symbol == '!' || symbol == '\r' || symbol == '\n') {
                break;
            }
        }
        return sentenceEndBuffer;
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
            System.out.println(e.getMessage());
            throw e;
        } finally {
            for (ByteArrayBuffer buffer : buffers) {
                buffer.close();
            }
            buffers.clear();
        }
    }
}
