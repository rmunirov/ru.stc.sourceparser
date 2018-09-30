package ru.innopolis.stc12.sourceparser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class SentenceFinderTest {
    private final Integer MAX_BUFFER_SIZE_FOR_LARGE_FILE = 10_485_760;
    private SentenceFinder sentenceFinder;
    private SourceBuffer sourceBuffer = Mockito.mock(SourceBuffer.class);
    private String source = "file:\\source";
    private Set words = Mockito.mock(TreeSet.class);
    private BlockingQueue result = Mockito.mock(BlockingQueue.class);
    private CustomByteBuffer customByteBuffer = Mockito.mock(CustomByteBuffer.class);

    @BeforeEach
    void setUp() {
        sentenceFinder = new SentenceFinder(sourceBuffer);
    }

    @Test
    void getFutures() {
        assertNotNull(sentenceFinder.getFutures());
    }

    @Test
    void findSentencesInSourceWhenParametersNull() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> sentenceFinder.findSentencesInSource(null, words, result)),
                () -> assertThrows(NullPointerException.class, () -> sentenceFinder.findSentencesInSource(source, null, result)),
                () -> assertThrows(NullPointerException.class, () -> sentenceFinder.findSentencesInSource(source, words, null))
        );
    }

    @Test
    void findSentenceInSourceWhenLargeSource() throws IOException {
        when(sourceBuffer.getSourceType(any())).thenReturn(SourceType.LARGE);
        when(sourceBuffer.getAvailableSize()).thenReturn(MAX_BUFFER_SIZE_FOR_LARGE_FILE);
        when(sourceBuffer.getNextBufferByDelimiter(anyInt(), any())).thenReturn(new CustomByteBuffer()).thenReturn(null);
        sentenceFinder.findSentencesInSource(source, new TreeSet<>(), result);
        verify(sourceBuffer, times(1)).getSourceType(any());
        verify(sourceBuffer, times(1)).getAvailableSize();
        verify(sourceBuffer, times(2)).getNextBufferByDelimiter(anyInt(), any());
    }

    @Test
    void findSentenceInSourceWhenMediumSource() throws IOException {
        when(sourceBuffer.getSourceType(any())).thenReturn(SourceType.MEDIUM);
        when(sourceBuffer.getBuffer()).thenReturn(new CustomByteBuffer());
        sentenceFinder.findSentencesInSource(source, new TreeSet<>(), result);
        verify(sourceBuffer, times(1)).getBuffer();
    }

    @Test
    void findSentenceInSourceWhenSmallSource() throws IOException {
        when(sourceBuffer.getSourceType(any())).thenReturn(SourceType.SMALL);
        when(sourceBuffer.getBuffer()).thenReturn(new CustomByteBuffer());
        sentenceFinder.findSentencesInSource(source, new TreeSet<>(), result);
        verify(sourceBuffer, times(1)).getBuffer();
    }

    @Test
    void findSentenceInBufferByAsyncWhenParameterNull() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> sentenceFinder.findSentenceInBufferByAsync(null, words, result)),
                () -> assertThrows(NullPointerException.class, () -> sentenceFinder.findSentenceInBufferByAsync(customByteBuffer, null, result)),
                () -> assertThrows(NullPointerException.class, () -> sentenceFinder.findSentenceInBufferByAsync(customByteBuffer, words, null))
        );
    }

    @Test
    void findSentenceInBufferWhenParametersNull() {
        assertAll(
                () -> assertEquals(false, sentenceFinder.findSentenceInBuffer(null, words, result)),
                () -> assertEquals(false, sentenceFinder.findSentenceInBuffer(customByteBuffer, null, result)),
                () -> assertEquals(false, sentenceFinder.findSentenceInBuffer(customByteBuffer, words, null))
        );
    }

    @Test
    void findSentenceInBufferWhenWordsEmpty() {
        when(words.isEmpty()).thenReturn(true);
        assertEquals(false, sentenceFinder.findSentenceInBuffer(customByteBuffer, words, result));
    }

    @Test
    void findSentenceInBufferWhenAllOk() {
        when(customByteBuffer.getRawData()).thenReturn(new byte[1]);
        assertEquals(true, sentenceFinder.findSentenceInBuffer(customByteBuffer, words, result));
    }
}