package ru.innopolis.stc12.sourceparser;

import com.sun.xml.internal.ws.util.ByteArrayBuffer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

public class SourceParser implements Parser {
    private final Integer MAX_BUFFERS = 10;
    private final Integer MAX_BUFFER_SIZE = 10_971_520;
    private Set<String> keys = new TreeSet<>();
    private Set<String> result = new CopyOnWriteArraySet<>();
    private List<ByteArrayBuffer> buffers = new ArrayList<>();
    private ParserExecutor parserExecutor = new ParserExecutor();

    @Override
    public void getOccurencies(String[] sources, String[] words, String res) throws Exception {
        keys.addAll(Arrays.asList(words));

        for (String source : sources) {

            URL url = new URL(source);
            InputStream inputStream = url.openStream();
            int length = inputStream.available();

            if (length > MAX_BUFFER_SIZE) {
                int bufferSize = getBufferSize(length);

                while (inputStream.available() > 0) {
                    buffers.add(getByteBufferWithEndSentence(inputStream, bufferSize));
                    if (buffers.size() >= MAX_BUFFERS) {
                        execute();
                    }
                }
                execute();
            } else {
                //TODO maybe need write small files in common buffer and process
                buffers.add(getByteBufferWithEndSentence(inputStream, length));
                if (buffers.size() >= MAX_BUFFERS) {
                    execute();
                }
            }
        }
        execute();
        //TODO that's right?
        FileOutputStream fileOutputStream = new FileOutputStream("result.txt");
        fileOutputStream.write(result.toString().getBytes());
    }

    private int getBufferSize(int length) {
        if (length <= 0) return -1;
        int partCount = length / MAX_BUFFER_SIZE + 1;
        int bufferSize = MAX_BUFFER_SIZE;
        if (partCount < MAX_BUFFERS) {
            bufferSize = length / MAX_BUFFERS + 1;
        }
        return bufferSize;
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
            return null;
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

    private void execute() throws Exception {
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
