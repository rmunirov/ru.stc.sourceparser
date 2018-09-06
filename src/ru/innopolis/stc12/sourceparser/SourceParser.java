package ru.innopolis.stc12.sourceparser;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class SourceParser implements Parser {
    private final Integer MAX_THREADS = 20;
    private final Integer MAX_BUFFER_SIZE = 400000;

    private Set<String> keys = new CopyOnWriteArraySet<>();
    private Set<String> result = new CopyOnWriteArraySet<>();
    private List<ByteBuffer> buffers = new ArrayList<>();

    private DataParser dataParser = new DataParser();


    @Override
    public void getOccurencies(String[] sources, String[] words, String res) throws Exception {

        for (int i = 0; i < words.length; i++) {
            keys.add(words[i]);
        }

        for (int i = 0; i < sources.length; i++) {
            FileInputStream fileInputStream = new FileInputStream(sources[i]);
            FileChannel fileChannel = fileInputStream.getChannel();
            int fileLength = fileInputStream.available();
            if (fileLength > MAX_BUFFER_SIZE) {
                int parts = fileLength / MAX_BUFFER_SIZE + 1;

                long startPart = 0;
                long endPart = findSentenceEndPositions(fileInputStream, MAX_BUFFER_SIZE);

                while (endPart <= fileLength) {
                    buffers.add(fileChannel.map(FileChannel.MapMode.READ_ONLY, startPart, endPart - startPart));
                    startPart = endPart + 1;
                    endPart += findSentenceEndPositions(fileInputStream, MAX_BUFFER_SIZE);
                }

                ExecutorService threadPool = Executors.newFixedThreadPool(buffers.size());
                List<Future<Boolean>> futures = new ArrayList<>();
                for (int thread = 0; thread < buffers.size(); thread++) {
                    ByteBuffer byteBuffer = buffers.get(thread);
                    futures.add(CompletableFuture.supplyAsync(() -> dataParser.parse(byteBuffer, keys, result), threadPool));
                }

                boolean done = false;
                for (Future<Boolean> future : futures) {
                    done = future.get();
                }

                FileOutputStream fileOutputStream = new FileOutputStream("result.txt");
                fileOutputStream.write(result.toString().getBytes());

                threadPool.shutdown();
            }
        }

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
}
