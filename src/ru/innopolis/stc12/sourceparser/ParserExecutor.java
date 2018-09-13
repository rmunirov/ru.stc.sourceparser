package ru.innopolis.stc12.sourceparser;

import com.sun.xml.internal.ws.util.ByteArrayBuffer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class ParserExecutor {
    private DataParser dataParser = new DataParser();

    public boolean execute(List<ByteArrayBuffer> buffers, Set words, Set result) throws ExecutionException, InterruptedException {

        ExecutorService threadPool = Executors.newFixedThreadPool(buffers.size());
        List<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < buffers.size(); i++) {
            InputStream stream = buffers.get(i).newInputStream();
            //TODO thread pool work right, if pass parameters?
            futures.add(CompletableFuture.supplyAsync(() -> dataParser.parse(stream, words, result), threadPool));
        }

        boolean done = false;
        for (Future<Boolean> future : futures) {
            done = future.get();
        }

        threadPool.shutdown();

        return done;
    }
}
