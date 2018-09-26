package ru.innopolis.stc12.sourceparser;

import java.util.HashSet;
import java.util.Set;

public class Delimiters {
    public static Set<Integer> endOfSentence = new HashSet<>();
    public static Set<Integer> endOfWord = new HashSet<>();

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
