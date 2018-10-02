package ru.innopolis.stc12.sourceparser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SourceParserTest {
    private SourceParser sourceParser;
    private SentenceFinder sentenceFinder = Mockito.mock(SentenceFinder.class);
    private String[] sources = new String[]{"file://file1", "file://file2"};
    private String[] words = new String[]{"word1", "word2"};
    private String result = "file://res";

    @BeforeEach
    void setUp() {
        sourceParser = new SourceParser(sentenceFinder);
    }

    @Test
    void getOccurenciesWhenParametersNull() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> sourceParser.getOccurencies(null, words, result)),
                () -> assertThrows(NullPointerException.class, () -> sourceParser.getOccurencies(sources, null, result)),
                () -> assertThrows(NullPointerException.class, () -> sourceParser.getOccurencies(sources, words, null))
        );
    }

    @Test
    void getOccurenciesWhenAllOk() throws Exception {
        sourceParser.getOccurencies(sources, words, result);
        verify(sentenceFinder, times(2)).findSentencesInSource(any(), any(), any());
    }
}