package ru.innopolis.stc12.sourceparser;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import java.util.Set;

public class DataParser {
    private boolean isNeedSave = false;

    public DataParser() {
        init();
    }

    private void init() {
    }

    public boolean parse(ByteInputStream buffer, Set words, Set result) {
        if (buffer == null) return false;
        if (words == null) return false;
        if (result == null) return false;
        if (words.isEmpty()) return false;

        //TODO maybe use TRIE structure for words?
        StringBuilder word = new StringBuilder();
        StringBuilder sentence = new StringBuilder();
        int symbol;
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
                    result.add(sentence.toString());
                    isNeedSave = false;
                }
                word.delete(0, word.length());
                sentence.delete(0, sentence.length());
            }
        }
        return true;
    }
}
