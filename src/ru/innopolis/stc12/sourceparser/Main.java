package ru.innopolis.stc12.sourceparser;

public class Main {
    public static void main(String[] args) {
        try {
            long start = System.currentTimeMillis();

            String[] sources = new String[]{"D://Projects//java/file.txt"};
            String[] words1 = new String[]{"My", "sentence", "but", "world"};
            String res = "result.txt";

            SourceParser sourceParser = new SourceParser();
            sourceParser.getOccurencies(sources, words1, res);

            System.out.println(System.currentTimeMillis() - start);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
