package ru.innopolis.stc12.sourceparser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        try {
/*
            String string = "qwe.zzz!aa ? 11123 ?";
            Matcher matcher = Pattern.compile("([^.!?]+[.!?])").matcher(string);
            while (matcher.find())
            {
                System.out.println(matcher.group(1));
            }
*/

            URL url = new URL("file:D://Projects//java/file.txt");
            InputStream inputStream = url.openStream();
            int size = inputStream.available();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

/*
            Scanner scanner = new Scanner(bufferedReader);
            while (scanner.hasNext()){
                String string = scanner.next();
                System.out.println(string);
            }
            System.out.println("2 - " + (System.currentTimeMillis() - start));
*/


            //String line = bufferedReader.readLine();

            Set<String> result = new TreeSet<>();
            Set<String> words_ = new TreeSet<>();
            words_.add("My");
            words_.add("sentence");
            words_.add("but");
            words_.add("world");
            words_.add("compile");
            words_.add("the");
            words_.add("key");
            words_.add("press");
            words_.add("button");
            words_.add("top");

            boolean isNeedSave = false;
            int symbol;
            StringBuilder word = new StringBuilder();
            StringBuilder sentence = new StringBuilder();
            long start = System.currentTimeMillis();

            Pattern pattern = Pattern.compile("(\\W)(my|sentence|предложение|компьтер|данные|задач|объект|должен|программа|шаг|части)(\\W)");
            Matcher matcher;

            while ((symbol = bufferedReader.read()) != -1) {

                if (UtilSymbols.endOfSentence.contains(Integer.valueOf(symbol))) {
                    sentence.appendCodePoint(symbol);
                    matcher = pattern.matcher(sentence);
                    if (matcher.find()) {
                        result.add(sentence.toString());
                    }
                    sentence.delete(0, sentence.length());
                } else {
                    sentence.appendCodePoint(symbol);
                }

/*
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
                    sentence.delete(0, word.length());
                    word.delete(0, word.length());
                }
*/
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
