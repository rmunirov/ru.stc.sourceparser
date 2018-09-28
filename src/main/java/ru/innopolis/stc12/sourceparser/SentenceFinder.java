package ru.innopolis.stc12.sourceparser;

import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.*;

public class SentenceFinder {
    private static final Logger LOGGER = Logger.getLogger(SentenceFinder.class);
    private final Integer MAX_THREADS = 10;
    private final Integer MAX_BUFFER_SIZE_FOR_LARGE_FILE = 10_485_760;
    private SourceBuffer sourceBuffer;
    private ExecutorService threadPool;
    private List<Future<Boolean>> futures = new ArrayList<>();

    public SentenceFinder(SourceBuffer sourceBuffer) {
        this.sourceBuffer = sourceBuffer;
        threadPool = Executors.newFixedThreadPool(MAX_THREADS);
        LOGGER.info("created " + MAX_THREADS + " thread in the thread pool");
    }

    public void sourcesEnd() {
        threadPool.shutdown();
    }

    public List<Future<Boolean>> getFutures() {
        return futures;
    }

    public boolean findSentencesInSource(String source, Set<String> words, BlockingQueue<String> result) throws IOException {
        SourceType type = sourceBuffer.getSourceType(new URL(source));
        switch (type) {
            case LARGE:
                LOGGER.info("start parsing a large file");
                int bufferSize = getBufferRightSizeByThreadCount(MAX_THREADS, MAX_BUFFER_SIZE_FOR_LARGE_FILE, sourceBuffer.getAvailableSize());
                LOGGER.info("buffer size = " + bufferSize);
                CustomByteBuffer buffer;
                while ((buffer = sourceBuffer.getNextBufferByDelimiter(bufferSize, Delimiters.endOfSentence)) != null) {
                    findSentenceInBufferByAsync(buffer, new TreeSet<>(words), result);
                }
                break;
            case MEDIUM:
                findSentenceInBufferByAsync(sourceBuffer.getBuffer(), new TreeSet<>(words), result);
                break;
            case SMALL:
                findSentenceInBufferByAsync(sourceBuffer.getBuffer(), new TreeSet<>(words), result);
                break;
        }
        return true;
    }

    public void findSentenceInBufferByAsync(CustomByteBuffer buffer, Set words, BlockingQueue<String> result) {
        futures.add(CompletableFuture.supplyAsync(() -> findSentenceInBuffer(buffer, words, result), threadPool));
        LOGGER.info("added buffer in thread pool for parsing");
    }

    public boolean findSentenceInBuffer(CustomByteBuffer buffer, Set words, BlockingQueue<String> result) {
        if (buffer == null) return false;
        if (words == null) return false;
        if (result == null) return false;
        if (words.isEmpty()) return false;
        ByteArrayInputStream inputBuffer = new ByteArrayInputStream(buffer.getRawData());
        LOGGER.info("start parsing buffer in a separate thread");   //TODO need lock?
        StringBuilder word = new StringBuilder();
        StringBuilder sentence = new StringBuilder();
        boolean isNeedSave = false;
        int symbol;
        try {
            while ((symbol = inputBuffer.read()) != -1) {
                if (!isNeedSave) {
                    if (symbol == ' ' || symbol == '.' || symbol == '!' || symbol == '?') {
                        if (words.contains(word.toString())) {
                            isNeedSave = true;
                        }
                        word.delete(0, word.length());
                    } else {
                        word.appendCodePoint(symbol);
                    }
                }
                sentence.appendCodePoint(symbol);

                if (symbol == '?' || symbol == '.' || symbol == '!' || symbol == '\r' || symbol == '\n') {
                    if (isNeedSave) {
                        result.put(sentence.toString());
                        isNeedSave = false;
                    }
                    word.delete(0, word.length());
                    sentence.delete(0, sentence.length());
                }
            }
            buffer.close();
        } catch (InterruptedException | IOException e) {
            LOGGER.error(e);
        }
        LOGGER.info("parsing stop");
        return true;
    }

    private int getBufferRightSizeByThreadCount(int threadCount, int bufferSize, int sourceSize) {
        int partCount = sourceSize / bufferSize + 1;
        if (partCount <= threadCount) {
            return sourceSize / partCount + 1;
        } else {
            return sourceSize / ((partCount / threadCount + 1) * threadCount) + 1;
        }
    }

}
