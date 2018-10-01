package ru.innopolis.stc12.sourceparser;

import org.apache.log4j.Logger;

import java.io.File;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class);
    public static void main(String[] args) {
        try {
            LOGGER.debug("Start processing");
            LOGGER.debug("Search for files in a folder");
            File folder = new File("D://Projects//java//testSet");
            String[] files = folder.list();
            LOGGER.debug("Updating file names");
            String[] sources = new String[files.length];
            for (int i = 0; i < sources.length; i++) {
                sources[i] = "file:" + folder.getAbsolutePath() + "//" + files[i];
            }
            LOGGER.debug("Creating array with key words");
            String[] words = new String[]{"asnufliz", "lmmohk", "wczf", "stkxubce", "ymoeo", "Zbrilke", "Gycoha", "gwmdrpnce", "rfvbdkzpk", "tousq", "vzd", "waob", "Pjvtod"};
            String res = "result.txt";

            long start = System.currentTimeMillis();
            SourceBuffer sourceBuffer = new SourceBuffer();
            SentenceFinder sentenceFinder = new SentenceFinder(sourceBuffer);
            SourceParser sourceParser = new SourceParser(sentenceFinder);
            LOGGER.debug("Start parse files");
            sourceParser.getOccurencies(sources, words, res);
            LOGGER.debug("End parse files");
            LOGGER.debug("passed - " + (System.currentTimeMillis() - start) * 0.001 + " seconds");

        } catch (Exception e) {
            LOGGER.error(e);
            LOGGER.error(e);
        }
    }
}
