package ru.innopolis.stc12.sourceparser;

import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class SourceParser implements Parser {
    private static final Logger LOGGER = Logger.getLogger(Parser.class);
    private Set<String> keys = new TreeSet<>();
    private BlockingQueue<String> result = new LinkedBlockingQueue<>();
    private SentenceFinder sentenceFinder;

    public SourceParser(SentenceFinder sentenceFinder) {
        this.sentenceFinder = sentenceFinder;
    }

    @Override
    public void getOccurencies(String[] sources, String[] words, String res) throws Exception {
        if (sources == null) {
            throw new NullPointerException("sources is null");
        }
        if (words == null) {
            throw new NullPointerException("words is null");
        }
        if (res == null) {
            throw new NullPointerException("res is null");
        }
        keys.addAll(Arrays.asList(words));
        LOGGER.info("filling the words set");
        LOGGER.info("set size = " + keys.size());
        ResultWriter resultWriter = new ResultWriter(result, res);
        resultWriter.start();
        for (String source : sources) {
            sentenceFinder.findSentencesInSource(source, keys, result);
        }
        List<Future<Boolean>> list = sentenceFinder.getFutures();
        if (!list.isEmpty()) {
            for (Future<Boolean> future : list) {
                future.get();
            }
        }
        sentenceFinder.sourcesEnd();
        resultWriter.setFinish(true);
        resultWriter.join();
        LOGGER.info("parsing done");
    }
}
