package ru.innopolis.stc12.sourceparser;

import java.util.HashSet;
import java.util.Set;

public class Delimiters {
    protected static final Set<Integer> endOfSentence = new HashSet<>();
    protected static final Set<Integer> endOfWord = new HashSet<>();

    private Delimiters() {
    }

    static {
        endOfSentence.add(Integer.valueOf('.'));
        endOfSentence.add(Integer.valueOf('?'));
        endOfSentence.add(Integer.valueOf('!'));
        endOfSentence.add(Integer.valueOf('\r'));
        endOfSentence.add(Integer.valueOf('\n'));

        endOfWord.add(Integer.valueOf(' '));
        endOfWord.add(Integer.valueOf(','));
        endOfWord.add(Integer.valueOf('.'));
        endOfWord.add(Integer.valueOf('?'));
        endOfWord.add(Integer.valueOf('!'));
    }
}
