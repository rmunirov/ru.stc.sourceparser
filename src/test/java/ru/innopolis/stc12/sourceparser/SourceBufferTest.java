package ru.innopolis.stc12.sourceparser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SourceBufferTest {
    private SourceBuffer sourceBuffer;
    private URL url = Mockito.mock(URL.class);
    private ByteArrayInputStream inputStream = Mockito.mock(ByteArrayInputStream.class);
    private static final Integer LARGE_FILE_SIZE = 10_485_760;
    private static final Integer SMALL_FILE_SIZE = 51_200;
    private Set<String> delimetrs = Mockito.mock(TreeSet.class);


    @BeforeEach
    void setUp() {
        sourceBuffer = new SourceBuffer();
    }

    @Test
    void setSourceUrl() throws IOException {
        when(url.openStream()).thenReturn(inputStream);
        sourceBuffer.setSourceUrl(url);
        verify(url, times(1)).openStream();
        assertThrows(NullPointerException.class, () -> sourceBuffer.setSourceUrl(null));
    }

    @Test
    void getBufferWhenNullInputStream() throws IOException {
        when(url.openStream()).thenReturn(null);
        sourceBuffer.setSourceUrl(url);
        assertNull(sourceBuffer.getBuffer());
    }

    @Test
    void getBufferBySizeWhenNullInputStream() throws IOException {
        when(url.openStream()).thenReturn(null);
        sourceBuffer.setSourceUrl(url);
        assertNull(sourceBuffer.getBuffer(1));
    }

    @Test
    void getBufferBySizeWhenAvailableZero() throws IOException {
        when(inputStream.available()).thenReturn(0);
        when(url.openStream()).thenReturn(inputStream);
        sourceBuffer.setSourceUrl(url);
        assertNull(sourceBuffer.getBuffer(1));
    }

    @Test
    void getBufferBySizeWhenSizeZero() throws IOException {
        assertNull(sourceBuffer.getBuffer(0));
    }

    @Test
    void getBufferBySizeWhenReadDataFailed() throws IOException {
        when(inputStream.available()).thenReturn(1);
        when(inputStream.read(any())).thenReturn(-1);
        when(url.openStream()).thenReturn(inputStream);
        sourceBuffer.setSourceUrl(url);
        assertNull(sourceBuffer.getBuffer(1));
    }

    @Test
    void getBufferBySizeWhenAllOk() throws IOException {
        when(inputStream.available()).thenReturn(1);
        when(inputStream.read(any())).thenReturn(1);
        when(url.openStream()).thenReturn(inputStream);
        sourceBuffer.setSourceUrl(url);
        assertNotNull(sourceBuffer.getBuffer(1));
    }

    @Test
    void getNextBufferByDelimiterWhenSizeZero() throws IOException {
        assertNull(sourceBuffer.getNextBufferByDelimiter(0, null));
    }

    @Test
    void getNextBufferByDelimiterWhenAvailableZero() throws IOException {
        when(inputStream.available()).thenReturn(1).thenReturn(0);
        when(inputStream.read(any())).thenReturn(1);
        when(url.openStream()).thenReturn(inputStream);
        sourceBuffer.setSourceUrl(url);
        assertNotNull(sourceBuffer.getNextBufferByDelimiter(1, null));
    }

    @Test
    void getNextBufferByDelimiterWhenDelimiterZero() throws IOException {
        when(inputStream.available()).thenReturn(1);
        when(inputStream.read(any())).thenReturn(1);
        when(url.openStream()).thenReturn(inputStream);
        sourceBuffer.setSourceUrl(url);
        assertNull(sourceBuffer.getNextBufferByDelimiter(1, null));
    }

    @Test
    void getNextBufferByDelimiterWhenContains() throws IOException {
        when(inputStream.available()).thenReturn(1);
        when(inputStream.read(any())).thenReturn(1);
        when(url.openStream()).thenReturn(inputStream);
        when(delimetrs.contains(any())).thenReturn(true);
        sourceBuffer.setSourceUrl(url);
        assertNotNull(sourceBuffer.getNextBufferByDelimiter(1, delimetrs));
    }

    @Test
    void getAvailableSize() throws IOException {
        when(url.openStream()).thenReturn(inputStream);
        when(inputStream.available()).thenReturn(1);
        sourceBuffer.setSourceUrl(url);
        assertEquals(1, sourceBuffer.getAvailableSize());
    }

    @Test
    void getSourceTypeWhenUrlNull() {
        assertThrows(NullPointerException.class, () -> sourceBuffer.getSourceType(null));
    }

    @Test
    void gerSourceTypeWhenSizeLarge() throws IOException {
        when(url.openStream()).thenReturn(inputStream);
        when(inputStream.available()).thenReturn(LARGE_FILE_SIZE);
        assertEquals(SourceType.LARGE, sourceBuffer.getSourceType(url));
    }

    @Test
    void gerSourceTypeWhenSizeSmall() throws IOException {
        when(url.openStream()).thenReturn(inputStream);
        when(inputStream.available()).thenReturn(SMALL_FILE_SIZE);
        assertEquals(SourceType.SMALL, sourceBuffer.getSourceType(url));
    }

    @Test
    void gerSourceTypeWhenSizeMedium() throws IOException {
        when(url.openStream()).thenReturn(inputStream);
        when(inputStream.available()).thenReturn(SMALL_FILE_SIZE + 1);
        assertEquals(SourceType.MEDIUM, sourceBuffer.getSourceType(url));
    }

}