package ru.innopolis.stc12.sourceparser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class DataParser {
    public boolean parse(InputStream buffer, Set words, BlockingQueue result) {
        if (buffer == null) return false;
        if (words == null) return false;
        if (result == null) return false;
        if (words.isEmpty()) return false;

        //TODO maybe use TRIE structure for words?
        StringBuilder word = new StringBuilder();
        StringBuilder sentence = new StringBuilder();
        boolean isNeedSave = false;
        int symbol;
        try {
            while ((symbol = buffer.read()) != -1) {
                if (!isNeedSave) {
                    if (symbol == ' ' || symbol == '.' || symbol == '!' || symbol == '?') {
                        if (words.contains(word.toString())) {
                            isNeedSave = true;
                        }
                        word.delete(0, word.length());
                    } else {
                        word.appendCodePoint(symbol);
                    }
                }
                sentence.appendCodePoint(symbol);

                if (symbol == '?' || symbol == '.' || symbol == '!' || symbol == '\r' || symbol == '\n') {
                    if (isNeedSave) {
                        result.put(sentence.toString());
                        isNeedSave = false;
                    }
                    word.delete(0, word.length());
                    sentence.delete(0, sentence.length());
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }
}
