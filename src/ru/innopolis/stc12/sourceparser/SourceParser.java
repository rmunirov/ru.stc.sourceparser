package ru.innopolis.stc12.sourceparser;

import com.sun.xml.internal.ws.util.ByteArrayBuffer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;

public class SourceParser implements Parser {
    private final Integer MAX_BUFFERS = 20;
    private final Integer MAX_BUFFER_SIZE = 20_971_520;

    private Set<String> keys = new TreeSet<>();
    private Set<String> result = new CopyOnWriteArraySet<>();
    //TODO can create byte buffers at once?
    private List<byte[]> buffers = new ArrayList<>();

    private ParserExecutor parserExecutor = new ParserExecutor();

    @Override
    public void getOccurencies(String[] sources, String[] words, String res) throws Exception {

        for (int i = 0; i < words.length; i++) {
            keys.add(words[i]);
        }

        for (int i = 0; i < sources.length; i++) {

            URL url = new URL(sources[i]);
            InputStream inputStream = url.openStream();
            int length = inputStream.available();

            if (length > MAX_BUFFER_SIZE) {
                int partCount = length / MAX_BUFFER_SIZE + 1;
                int bufferSize = MAX_BUFFER_SIZE;
                if (partCount < MAX_BUFFERS) {
                    bufferSize = length / MAX_BUFFERS + 1;
                }

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

    private byte[] getByteBufferWithEndSentence(InputStream inputStream, int size) throws IOException {
        if (inputStream == null) return null;
        if (size <= 0) return null;

        int length = inputStream.available();

        if (length < size) {
            size = length;
        }

        byte[] bytes = new byte[size];
        inputStream.read(bytes);
        //found end sentence
        ByteArrayBuffer buffer = new ByteArrayBuffer();
        buffer.write(bytes);
        int symbol;
        while (!UtilSymbols.endOfSentence.contains(symbol = inputStream.read())) {
            if (symbol == -1) {
                break;
            }
            buffer.write(symbol);
        }
        return buffer.getRawData();
    }

    private boolean execute() throws Exception {
        if (buffers.isEmpty()) {
            return false;
        }

        boolean done = parserExecutor.execute(buffers, new TreeSet<>(keys), result);
        if (done == false) {
            throw new Exception("parse failed");
        }
        buffers.clear();
        return done;
    }
}
