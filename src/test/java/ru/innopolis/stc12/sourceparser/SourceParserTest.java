package ru.innopolis.stc12.sourceparser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SourceParserTest {
    private static SourceParser sourceParser;

    @BeforeAll
    static void initAll() {
        sourceParser = new SourceParser();
    }

    @BeforeEach
    void init() {

    }

    @Test
    void getOccurenciesTest() {
        assertThrows(Exception.class, () -> sourceParser.getOccurencies(null, null, null));
    }
}
