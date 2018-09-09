package ru.innopolis.stc12.sourceparser;

import java.nio.ByteBuffer;
import java.util.Set;

public class DataParser {
    private boolean isNeedSave = false;

    public DataParser() {
        init();
    }

    private void init() {
    }

    public boolean parse(ByteBuffer buffer, Set words, Set result) {
/*
        ByteArrayBuffer sentence = new ByteArrayBuffer();
        ByteArrayBuffer word = new ByteArrayBuffer();
*/
        StringBuilder word = new StringBuilder();
        StringBuilder sentence = new StringBuilder();
        while (buffer.hasRemaining()) {
            int symbol = buffer.getChar();
            if (symbol < 0) {
                symbol += 127;
            }

            if (!isNeedSave) {
                if (UtilSymbols.endOfWord.contains(Integer.valueOf(symbol))) {
                    if (words.contains(word.toString())) {
                        isNeedSave = true;
                        //word.reset();
                        word.delete(0, word.length());
                    }
                    //word.reset();
                    word.delete(0, word.length());
                    //sentence.write(symbol);
                    sentence.appendCodePoint(symbol);
                    continue;
                }
                //word.write(symbol);
                word.appendCodePoint(symbol);
            }

            //sentence.write(symbol);
            sentence.appendCodePoint(symbol);

            if (UtilSymbols.endOfSentence.contains(Integer.valueOf(symbol))) {
                if (isNeedSave) {
                    result.add(sentence.toString());
                    isNeedSave = false;
                }
                //sentence.reset();
                //word.reset();
                word.delete(0, word.length());
                sentence.delete(0, word.length());
            }
        }
        return true;
    }
}
