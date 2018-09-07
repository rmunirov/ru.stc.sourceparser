package ru.innopolis.stc12.sourceparser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Main {
    public static void main(String[] args) {
        try {
            long start = System.currentTimeMillis();
            URL url = new URL("https://moodle.innopolis.university/mod/assign/view.php?id=2016");
            URLConnection urlConnection = url.openConnection();
            urlConnection.addRequestProperty("User-Agent", "Mozilla/4.76");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));


            System.out.println(bufferedReader.read());

            String[] sources = new String[]{"D://Projects//java/file.txt"};
            String[] words1 = new String[]{"My", "sentence", "but", "world", "compile", "the", "key", "press", "button", "top"};
            String res = "result.txt";

            SourceParser sourceParser = new SourceParser();
            sourceParser.getOccurencies(sources, words1, res);

            System.out.println(System.currentTimeMillis() - start);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
