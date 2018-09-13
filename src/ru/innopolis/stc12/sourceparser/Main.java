package ru.innopolis.stc12.sourceparser;

import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
            File folder = new File("D://Projects//java//testSet");
            String[] files = folder.list();

            String[] sources = new String[files.length];
            for (int i = 0; i < sources.length; i++) {
                sources[i] = new String("file:" + folder.getAbsolutePath() + "//" + files[i]);
            }
            String[] words = new String[]{"asnufliz", "lmmohk", "wczf", "stkxubce", "ymoeo", "Zbrilke", "Gycoha", "gwmdrpnce", "rfvbdkzpk", "tousq", "vzd"};
            String res = "result.txt";

            long start = System.currentTimeMillis();

            //TODO need very mach memory, it is problem? GC manage to clean?
            SourceParser sourceParser = new SourceParser();
            sourceParser.getOccurencies(sources, words, res);

            System.out.println("passed - " + (System.currentTimeMillis() - start) * 0.001 + " seconds");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
