package ru.innopolis.stc12.sourceparser;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        try {

            File folder = new File("D://Projects//java//testSet");
            String[] files = folder.list();

            String[] sources = new String[files.length];
            for (int i = 0; i < sources.length; i++) {
                sources[i] = new String("file:" + folder.getAbsolutePath() + "//" + files[i]);
            }
            //String[] sources = new String[]{"file:D://Projects//java//testSet//3192b7cd-e260-4b79-a337-a0f2fe2bf28f.txt"};
            String[] words = new String[]{"asnufliz", "lmmohk", "wczf", "stkxubce", "ymoeo", "Zbrilke", "Gycoha", "gwmdrpnce", "rfvbdkzpk", "tousq", "vzd"};
            String res = "result.txt";

            long start = System.currentTimeMillis();

            //TODO need very mach memory, it is problem? GC manage to clean?
            SourceParser sourceParser = new SourceParser();
            sourceParser.getOccurencies(sources, words, res);

            System.out.println("passed - " + (System.currentTimeMillis() - start) * 0.001 + " seconds");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
