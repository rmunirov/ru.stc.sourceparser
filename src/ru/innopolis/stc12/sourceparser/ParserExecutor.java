package ru.innopolis.stc12.sourceparser;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class ParserExecutor {
    private DataParser dataParser = new DataParser();

    public boolean execute(List<byte[]> buffers, Set words, Set result) throws ExecutionException, InterruptedException {

        ExecutorService threadPool = Executors.newFixedThreadPool(buffers.size());
        List<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < buffers.size(); i++) {
            ByteInputStream byteInputStream = new ByteInputStream(buffers.get(i), buffers.get(i).length);
            //TODO thread pool work right, if pass parameters?
            futures.add(CompletableFuture.supplyAsync(() -> dataParser.parse(byteInputStream, words, result), threadPool));
        }

        boolean done = false;
        for (Future<Boolean> future : futures) {
            done = future.get();
        }

        threadPool.shutdown();

        return done;
    }
}
