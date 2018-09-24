package ru.innopolis.stc12.sourceparser;

import org.apache.log4j.Logger;

import java.io.File;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class);
    public static void main(String[] args) {
        try {
/*
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
*/
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
            //String[] sources = new String[]{"file:D://Projects//java//testSet//efc7b69e-84ad-427a-ab6c-4d9efe2a145f.txt"};

            long start = System.currentTimeMillis();


            SourceParser sourceParser = new SourceParser();
            LOGGER.debug("Start parse files");
            sourceParser.getOccurencies(sources, words, res);
            LOGGER.debug("End parse files");
            System.out.println("passed - " + (System.currentTimeMillis() - start) * 0.001 + " seconds");

        } catch (Exception e) {
            LOGGER.error(e);
            LOGGER.error(e);
        }
    }
}
