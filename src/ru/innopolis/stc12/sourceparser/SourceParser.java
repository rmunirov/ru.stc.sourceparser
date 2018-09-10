package ru.innopolis.stc12.sourceparser;

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
            int fileLength = inputStream.available();

        }



/*
            FileInputStream fileInputStream = new FileInputStream(sources[i]);
            FileChannel fileChannel = fileInputStream.getChannel();


            if (fileLength > MAX_BUFFER_SIZE) {
                long startPart = 0;
                long endPart = findSentenceEndPositions(fileInputStream, MAX_BUFFER_SIZE);

                while (endPart <= fileLength) {
                    buffers.add(fileChannel.map(FileChannel.MapMode.READ_ONLY, startPart, endPart - startPart));
                    startPart = endPart + 1;
                    endPart += findSentenceEndPositions(fileInputStream, MAX_BUFFER_SIZE);
                    if (buffers.size() >= MAX_BUFFERS) {
                        execute();
                    }
                }

                execute();
            } else {
                buffers.add(fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileLength));
                if (buffers.size() >= MAX_BUFFERS) {
                    execute();
                }
            }
        }
        if (!buffers.isEmpty()) {
            execute();
        }
        FileOutputStream fileOutputStream = new FileOutputStream("result.txt");
        fileOutputStream.write(result.toString().getBytes());
*/
    }

    private long findSentenceEndPositions(InputStream inputStream, int offset) {
        if (inputStream == null) return -1;
        if (offset <= 0) return -1;

        try {
            int size = inputStream.available();

            if (size <= offset) {
                return size;
            }

            long realOffset = inputStream.skip(offset);

            if (realOffset == 0) {
                return -1;
            }

            int result = 1;
            int symbol = inputStream.read();
            while (!UtilSymbols.endOfSentence.contains(symbol)) {
                symbol = inputStream.read();
                if (symbol == -1) {
                    break;
                }
                result++;
            }
            return realOffset + result;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    private boolean execute() throws Exception {
        boolean done = false/* = parserExecutor.execute(buffers, keys, result)*/;
        if (done == false) {
            throw new Exception("parse failed");
        }
        buffers.clear();
        return done;
    }
}
