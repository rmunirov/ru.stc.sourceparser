package ru.innopolis.stc12.sourceparser;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class ResourceInformationTest {
    private static final Logger LOGGER = Logger.getLogger(ResourceInformationTest.class);
    private static ResourceInformation resourceInformation;

    @BeforeEach
    void setUp() {
        resourceInformation = new ResourceInformation();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void setResource() {
        //TODO maybe need to write each test in a separate method
        String trueSource = "file:D://Projects//java//testSet//efc7b69e-84ad-427a-ab6c-4d9efe2a145f.txt";
        String falseSource = "file:this//file//do//not//exist";
        assertAll("test for a setting resource",
                () -> assertTrue(resourceInformation.setResource(trueSource)),
                () -> assertFalse(resourceInformation.setResource(null)),
                () -> assertThrows(IOException.class, () -> resourceInformation.setResource(falseSource)),
                () -> assertDoesNotThrow(() -> resourceInformation.setResource(trueSource)));
    }

    @Test
    void getInputStream() throws IOException {
        assertNull(resourceInformation.getInputStream());
        String source = "file:D://Projects//java//testSet//efc7b69e-84ad-427a-ab6c-4d9efe2a145f.txt";
        resourceInformation.setResource(source);
        assertNotNull(resourceInformation.getInputStream());
    }

    @Test
    void getFileType() throws IOException {
        String smallFileSource = "file:D://Projects//java//testSet//7657f580-b3a2-495c-8716-cf772becde1c.txt";
        String mediumFileSource = "file:D://Projects//java//testSet//5dedc6d6-1910-4ff3-9f25-646643adbba1.txt";
        String largeFileSource = "file:D://Projects//java//testSet//137ea5e2-5223-402e-bc33-234508e6950e.txt";
        resourceInformation.setResource(smallFileSource);
        assertEquals(resourceInformation.getFileType(), FileType.SMALL);
        resourceInformation.setResource(mediumFileSource);
        assertEquals(resourceInformation.getFileType(), FileType.MEDIUM);
        resourceInformation.setResource(largeFileSource);
        assertEquals(resourceInformation.getFileType(), FileType.LARGE);
    }

    @Test
    void getLength() throws IOException {
        //TODO this right?
        String sourceToFileStream = "D://Projects//java//testSet//7657f580-b3a2-495c-8716-cf772becde1c.txt";
        String sourceToResourceInformation = "file:D://Projects//java//testSet//7657f580-b3a2-495c-8716-cf772becde1c.txt";
        InputStream inputStream = new FileInputStream(sourceToFileStream);
        int length = inputStream.available();
        resourceInformation.setResource(sourceToResourceInformation);
        assertEquals(resourceInformation.getLength(), length);
    }
}