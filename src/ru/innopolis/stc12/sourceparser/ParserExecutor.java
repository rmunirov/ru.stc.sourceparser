package ru.innopolis.stc12.sourceparser;

import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class ParserExecutor {
    private static final Logger LOGGER = Logger.getLogger(ParserExecutor.class);
    private DataParser dataParser = new DataParser();

    public boolean execute(List<ByteArrayBuffer> buffers, Set words, BlockingQueue result) throws ExecutionException, InterruptedException {
        LOGGER.info("start execute parsing the buffers");
        ExecutorService threadPool = Executors.newFixedThreadPool(buffers.size());
        List<Future<Boolean>> futures = new ArrayList<>();
        LOGGER.info("created " + buffers.size() + " thread in the thread pool");

        for (int i = 0; i < buffers.size(); i++) {
            InputStream stream = buffers.get(i).newInputStream();
            futures.add(CompletableFuture.supplyAsync(() -> dataParser.parse(stream, words, result), threadPool));
        }

        boolean done = false;
        for (Future<Boolean> future : futures) {
            done = future.get();
        }
        threadPool.shutdown();
        LOGGER.info("thread pool shutdown");

        return done;
    }
}
