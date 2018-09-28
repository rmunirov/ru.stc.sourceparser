package ru.innopolis.stc12.sourceparser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SourceBufferTest {
    private SourceBuffer sourceBuffer;
    private URL url = Mockito.mock(URL.class);
    private ByteArrayInputStream inputStream = Mockito.mock(ByteArrayInputStream.class);

    @BeforeEach
    void setUp() {
        sourceBuffer = new SourceBuffer();
    }

    @Test
    void setSourceUrl() throws IOException {
        when(url.openStream()).thenReturn(inputStream);
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
    void getNextBufferByDelimiter() {
    }

    @Test
    void getAvailableSize() {
    }

    @Test
    void getSourceType() {
    }
}