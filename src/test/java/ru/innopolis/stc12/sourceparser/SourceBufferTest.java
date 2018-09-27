package ru.innopolis.stc12.sourceparser;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class SourceBufferTest {
    private static final Logger LOGGER = Logger.getLogger(SourceBufferTest.class);
    private SourceBuffer sourceBuffer;

    @BeforeEach
    void setUp() throws IOException {
        String source = "D://Projects//java//testSet//7657f580-b3a2-495c-8716-cf772becde1c.txt";
        sourceBuffer = new SourceBuffer();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void setInputStream() {
    }

    @Test
    void getBuffer() throws IOException {
        assertNotNull(sourceBuffer.getBuffer());
        sourceBuffer.setSourceUrl(null);
        assertNull(sourceBuffer.getBuffer());
        //TODO need teat assertEquals?
    }

    @Test
    void getBuffer1() throws IOException {
        assertNotNull(sourceBuffer.getBuffer(1));
        assertNull(sourceBuffer.getBuffer(0));
    }

    @Test
    void getNextBufferByDelimiter() {
    }

    @Test
    void getAvailableSize() {
    }

    @Test
    void getSameBufferSizeOfParts() {
    }
}