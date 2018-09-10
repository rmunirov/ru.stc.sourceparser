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
        StringBuilder word = new StringBuilder();
        StringBuilder sentence = new StringBuilder();
        int symbol;
        while ((symbol = buffer.read()) != -1) {
            if (!isNeedSave) {
                if (UtilSymbols.endOfWord.contains(Integer.valueOf(symbol))) {
                    if (words.contains(word.toString())) {
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
                word.delete(0, word.length());
                sentence.delete(0, word.length());
            }
        }
        return true;
    }
}
