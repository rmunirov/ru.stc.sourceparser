package ru.innopolis.stc12.sourceparser;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Main {
    public static void main(String[] args) {
        try {
            long start = System.currentTimeMillis();

            URL url = new URL("file:D://Projects//java/file.txt");
            InputStream inputStream = url.openStream();

            int size = inputStream.available();
            List<byte[]> list = new ArrayList<>();


            while (inputStream.available() > 0) {
                byte[] bytes = new byte[10_971_520];
                ByteArrayBuffer buffer = new ByteArrayBuffer(10_971_520);
                inputStream.read(bytes);
                buffer.write(bytes);
                int smbl;
                while (!UtilSymbols.endOfSentence.contains(smbl = inputStream.read())) {
                    if (smbl == -1) {
                        break;
                    }
                    buffer.write(smbl);
                }
                list.add(buffer.getRawData());
                buffer.close();
            }

            ByteInputStream byteInputStream = new ByteInputStream(list.get(0), list.get(0).length);
/*
            int r;
            StringBuilder st = new StringBuilder();
            while ((r = byteInputStream.read()) != -1) {
                //st.appendCodePoint(r);
            }
*/

            System.out.println("1 - " + (System.currentTimeMillis() - start));

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(byteInputStream, "UTF-8"));

            Set<String> result = new TreeSet<>();
            Set<String> words_ = new TreeSet<>();

            words_.add("My");
            words_.add("sentence");
            words_.add("but");
            words_.add("world");
            words_.add("compile");
//            words_.add("the");
            words_.add("key");
            words_.add("press");
            words_.add("button");
            words_.add("top");

            boolean isNeedSave = false;
            int symbol;
            StringBuilder word = new StringBuilder();
            StringBuilder sentence = new StringBuilder();


            while ((symbol = bufferedReader.read()) != -1) {

                if (!isNeedSave) {
                    if (UtilSymbols.endOfWord.contains(symbol)) {
                        if (words_.contains(word.toString())) {
                            isNeedSave = true;
                            word.delete(0, word.length());
                        }
                        word.delete(0, word.length());
                        sentence.appendCodePoint(symbol);
                        continue;
                    }
                    word.appendCodePoint(symbol);
                }
                sentence.appendCodePoint(symbol);
                if (UtilSymbols.endOfSentence.contains(Integer.valueOf(symbol))) {
                    if (isNeedSave) {
                        result.add(sentence.toString());
                        isNeedSave = false;
                    }
                    sentence.delete(0, sentence.length());
                    word.delete(0, word.length());
                }

            }

            System.out.println("1 - " + (System.currentTimeMillis() - start));
            start = System.currentTimeMillis();


            String[] sources = new String[]{"D://Projects//java/file.txt"};
            String[] words1 = new String[]{"My", "sentence", "but", "world", "compile", "the", "key", "press", "button", "top", "предложение"};
            String res = "result.txt";

            SourceParser sourceParser = new SourceParser();
            sourceParser.getOccurencies(sources, words1, res);

            System.out.println("2 - " + (System.currentTimeMillis() - start));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
