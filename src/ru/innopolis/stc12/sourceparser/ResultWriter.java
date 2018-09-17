package ru.innopolis.stc12.sourceparser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class ResultWriter extends Thread {
    private BlockingQueue<String> queue;
    private String file;
    private volatile boolean finish = false;

    public ResultWriter(BlockingQueue<String> queue, String file) {
        this.queue = queue;
        this.file = file;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
        interrupt();
    }

    @Override
    public void run() {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            while ((!finish) || (!queue.isEmpty())) {
                outputStream.write(queue.take().getBytes());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
