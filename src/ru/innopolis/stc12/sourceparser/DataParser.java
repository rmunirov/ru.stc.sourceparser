package ru.innopolis.stc12.sourceparser;

import com.sun.xml.internal.ws.util.ByteArrayBuffer;

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
        ByteArrayBuffer sentence = new ByteArrayBuffer();
        ByteArrayBuffer word = new ByteArrayBuffer();
        while (buffer.hasRemaining()) {
            byte symbol = buffer.get();

            if (!isNeedSave) {
                if (UtilSymbols.endOfWord.contains(Integer.valueOf(symbol))) {
                    if (words.contains(word.toString())) {
                        isNeedSave = true;
                        word.reset();
                    }
                    word.reset();
                    sentence.write(symbol);
                    continue;
                }
                word.write(symbol);
            }

            sentence.write(symbol);

            if (UtilSymbols.endOfSentence.contains(Integer.valueOf(symbol))) {
                if (isNeedSave) {
                    result.add(sentence.toString());
                    isNeedSave = false;
                }
                sentence.reset();
                word.reset();
            }
        }
        return true;
    }
}
