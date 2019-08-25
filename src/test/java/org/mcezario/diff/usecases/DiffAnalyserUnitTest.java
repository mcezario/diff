package org.mcezario.diff.usecases;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mcezario.diff.domains.ComparisonDetail;
import org.mcezario.diff.domains.Diff;
import org.mcezario.diff.domains.DiffDetail;
import org.mcezario.diff.gateways.DiffDatabaseGateway;
import org.mcezario.diff.usecases.exceptions.CalculateDifferenceException;
import org.mcezario.diff.usecases.exceptions.DiffNotFoundException;
import org.mcezario.diff.usecases.exceptions.RequiredSidesException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class DiffAnalyserUnitTest {

    @InjectMocks
    private DiffAnalyser diffAnalyser;

    @Mock
    private DiffDatabaseGateway gateway;

    @Before
    public void initMocks() throws IOException {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(diffAnalyser, "objectMapper", new ObjectMapper());
    }

    @Test
    public void shouldReturnLeftSideSuccessfully() {
        // Given
        final String id = "1";
        final String content = "LEFT Content";

        // Prepare
        final Diff leftSide = Diff.newLeftSide(id, content);
        Mockito.when(gateway.insertLeftSide(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(leftSide.getLeft());

        // When
        diffAnalyser.left(id, content);

        // Then
        verify(gateway, times(1)).insertLeftSide(id, content);
        verify(gateway, times(0)).insertRightSide(anyString(), anyString());
    }

    @Test
    public void shouldReturnRightSideSuccessfully() {
        // Given
        final String id = "1";
        final String content = "RIGHT Content";

        // Prepare
        final Diff rightSide = Diff.newRightSide(id, content);
        Mockito.when(gateway.insertRightSide(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(rightSide.getRight());

        // When
        diffAnalyser.right(id, content);

        // Then
        verify(gateway, times(1)).insertRightSide(id, content);
        verify(gateway, times(0)).insertLeftSide(anyString(), anyString());
    }

    @Test
    public void shouldCompareTwoSidesEqualsAndReturn_EQUALS() {
        // Given
        final String id = "1";
        final String left = "ewoiY29kZSI6IC8KfQ=="; // { "code": 1 } -> Valid JSON
        final String right = "ewoiY29kZSI6IC8KfQ=="; // { "code": 1 } -> Valid JSON

        // Prepare
        Mockito.when(gateway.findDiffById(id)).thenReturn(Optional.of(Diff.newLeftSide("1", left).fillRightSide(right)));

        // When
        final DiffDetail detail = diffAnalyser.compare(id);

        // Then
        assertEquals(ComparisonDetail.EQUALS, detail.getDetail());
        assertEquals("100", detail.getSimilarity());
        assertNull(detail.getDifference());

        verify(gateway, times(1)).findDiffById(id);
    }

    @Test
    public void shouldCompareTwoSidesEqualsAndReturn_DIFFERENT_SIZE() {
        // Given
        final String id = "1";
        final String left = "eyAiY29kZSI6IDEgfQ=="; // { "code": 1 } -> Valid JSON
        final String right = "eyAiY29kZSI6IDkyMzkgfQ=="; // { "code": 9239 } -> Valid JSON

        // Prepare
        Mockito.when(gateway.findDiffById(id)).thenReturn(Optional.of(Diff.newLeftSide("1", left).fillRightSide(right)));

        // When
        final DiffDetail detail = diffAnalyser.compare(id);

        // Then
        assertEquals(ComparisonDetail.DIFFERENT_SIZE, detail.getDetail());
        assertNotNull(detail.getSimilarity());
        assertNull(detail.getDifference());

        verify(gateway, times(1)).findDiffById(id);
    }

    @Test
    public void shouldCompareTwoSidesEqualsAndReturn_DIFFERENT_CONTENT() {
        // Given
        final String id = "1";
        final String left = "eyAiY29kZSI6IDEgfQ=="; // { "code": 1 } -> Valid JSON
        final String right = "eyAiY29kZSI6IDEyMyB9"; // { "code": 123 } -> Valid JSON

        // Prepare
        Mockito.when(gateway.findDiffById(id)).thenReturn(Optional.of(Diff.newLeftSide("1", left).fillRightSide(right)));

        // When
        final DiffDetail detail = diffAnalyser.compare(id);

        // Then
        assertEquals(ComparisonDetail.DIFFERENT_CONTENT, detail.getDetail());
        assertNotNull(detail.getSimilarity());
        assertNotNull(detail.getDifference());

        verify(gateway, times(1)).findDiffById(id);
    }

    @Test(expected = DiffNotFoundException.class)
    public void shouldGetExceptionWhenDiffIdIsNonexistent() {
        // Given
        final String id = "1";

        // Prepare
        Mockito.when(gateway.findDiffById(id)).thenReturn(Optional.empty());

        try {
            // When
            diffAnalyser.compare(id);

        } catch (final DiffNotFoundException e) {
            // Then
            verify(gateway, times(1)).findDiffById(id);

            assertEquals("001", e.id().code());
            assertEquals("Invalid Id.", e.id().message());
            assertEquals(HttpStatus.NOT_FOUND, e.httpStatus());

            throw e;
        }
    }

    @Test(expected = RequiredSidesException.class)
    public void shouldGetExceptionWhenLeftSideIsEmptyInComparisonProcess() {
        // Given
        final String id = "1";

        // Prepare
        Mockito.when(gateway.findDiffById(id)).thenReturn(Optional.of(Diff.newRightSide("1", "RIGHT content")));

        try {
            // When
            diffAnalyser.compare(id);

        } catch (final RequiredSidesException e) {
            // Then
            verify(gateway, times(1)).findDiffById(id);

            assertEquals("003", e.id().code());
            assertEquals("The left and right sides must be filled in to compare the difference.", e.id().message());
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.httpStatus());

            throw e;
        }
    }

    @Test(expected = RequiredSidesException.class)
    public void shouldGetExceptionWhenRightSideIsEmptyInComparisonProcess() {
        // Given
        final String id = "1";

        // Prepare
        Mockito.when(gateway.findDiffById(id)).thenReturn(Optional.of(Diff.newLeftSide("1", "LEFT content")));

        try {
            // When
            diffAnalyser.compare(id);

        } catch (final RequiredSidesException e) {
            // Then
            verify(gateway, times(1)).findDiffById(id);

            assertEquals("003", e.id().code());
            assertEquals("The left and right sides must be filled in to compare the difference.", e.id().message());
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.httpStatus());

            throw e;
        }
    }

    @Test(expected = CalculateDifferenceException.class)
    public void shouldGetExceptionWhenAnySideHasAInvalidJson() {
        // Given
        final String id = "1";
        final String left = "ewoiY29kZSI6IC8KfQ=="; // { "code": 1 } -> Valid JSON
        final String right = "eyAiY29kZSI6IC8gfQ=="; // { "code": / } -> Invalid JSON

        // Prepare
        Mockito.when(gateway.findDiffById(id)).thenReturn(Optional.of(Diff.newLeftSide("1", left).fillRightSide(right)));

        try {
            // When
            diffAnalyser.compare(id);

        } catch (final CalculateDifferenceException e) {
            // Then
            verify(gateway, times(1)).findDiffById(id);

            assertEquals("002", e.id().code());
            assertEquals("Error to compare the difference between left and right side.", e.id().message());
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.httpStatus());

            throw e;
        }
    }

}
