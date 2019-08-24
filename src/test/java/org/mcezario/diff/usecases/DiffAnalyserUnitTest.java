package org.mcezario.diff.usecases;

import org.junit.Before;
import org.junit.Test;
import org.mcezario.diff.domains.Diff;
import org.mcezario.diff.gateways.DiffDatabaseGateway;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DiffAnalyserUnitTest {

    @InjectMocks
    private DiffAnalyser diffAnalyser;

    @Mock
    private DiffDatabaseGateway gateway;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnLeftSideSuccessfully() {
        // Given
        final String id = "1";
        final String content = "LEFT Content";

        // Prepare
        final Diff leftSide = Diff.newLeftSide(id, content);
        Mockito.when(gateway.insertLeftSide(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Optional.of(leftSide.getLeft()));

        // When
        final String leftContent = diffAnalyser.left(id, content);

        // Then
        verify(gateway, times(1)).insertLeftSide(id, content);
        verify(gateway, times(0)).insertRightSide(anyString(), anyString());
        assertEquals(content, leftContent);
    }

    @Test
    public void shouldReturnRightSideSuccessfully() {
        // Given
        final String id = "1";
        final String content = "RIGHT Content";

        // Prepare
        final Diff rightSide = Diff.newRightSide(id, content);
        Mockito.when(gateway.insertRightSide(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Optional.of(rightSide.getRight()));

        // When
        final String rightContent = diffAnalyser.right(id, content);

        // Then
        verify(gateway, times(1)).insertRightSide(id, content);
        verify(gateway, times(0)).insertLeftSide(anyString(), anyString());
        assertEquals(content, rightContent);
    }

    @Test
    public void shouldReturnDiffSuccessfully() {
        final String result = diffAnalyser.difference(1);
        assertEquals("difference", result);
    }

}
