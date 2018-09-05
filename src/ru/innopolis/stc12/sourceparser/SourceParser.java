package ru.innopolis.stc12.sourceparser;

import java.util.concurrent.CopyOnWriteArraySet;

public class SourceParser implements Parser {
    private CopyOnWriteArraySet<String> words = new CopyOnWriteArraySet<>();

    @Override
    public void getOccurencies(String[] sources, String[] words, String res) throws Exception {

    }
}
