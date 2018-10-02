package ru.innopolis.stc12.sourceparser;

import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class ResultWriter extends Thread {
    private static final Logger LOGGER = Logger.getLogger(ResultWriter.class);
    private BlockingQueue<String> queue;
    private String file;
    private volatile boolean finish = false;

    public ResultWriter(BlockingQueue<String> queue, String file) {
        LOGGER.info("ResultWriter is created");
        this.queue = queue;
        this.file = file;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
        interrupt();
    }

    @Override
    public void run() {
        LOGGER.info("ResultWriter is started");
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            while ((!finish) || (!queue.isEmpty())) {
                outputStream.write(queue.take().getBytes());
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error(e);
            Thread.currentThread().interrupt();
        }
        LOGGER.info("ResultWriter is stopped");
    }
}
