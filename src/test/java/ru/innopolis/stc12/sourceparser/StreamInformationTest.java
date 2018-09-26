package ru.innopolis.stc12.sourceparser;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class StreamInformationTest {
    private static final Logger LOGGER = Logger.getLogger(StreamInformationTest.class);
    private StreamInformation streamInformation;

    @BeforeEach
    void setUp() throws FileNotFoundException {
        String source = "D://Projects//java//testSet//7657f580-b3a2-495c-8716-cf772becde1c.txt";
        InputStream inputStream = new FileInputStream(source);
        streamInformation = new StreamInformation(inputStream);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void setInputStream() {
    }

    @Test
    void getBuffer() throws IOException {
        assertNotNull(streamInformation.getBuffer());
        streamInformation.setInputStream(null);
        assertNull(streamInformation.getBuffer());
        //TODO need teat assertEquals?
    }

    @Test
    void getBuffer1() throws IOException {
        assertNotNull(streamInformation.getBuffer(1));
        assertNull(streamInformation.getBuffer(0));
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