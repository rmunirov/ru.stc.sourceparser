package ru.innopolis.stc12.sourceparser;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class ParserExecutor {
    private DataParser dataParser = new DataParser();

    public boolean execute(List<ByteBuffer> buffers, Set words, Set result) throws ExecutionException, InterruptedException {

        ExecutorService threadPool = Executors.newFixedThreadPool(buffers.size());
        List<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < buffers.size(); i++) {
            ByteBuffer byteBuffer = buffers.get(i);
            futures.add(CompletableFuture.supplyAsync(() -> dataParser.parse(byteBuffer, words, result), threadPool));
        }

        boolean done = false;
        for (Future<Boolean> future : futures) {
            done = future.get();
        }

        threadPool.shutdown();

        return done;
    }
}
