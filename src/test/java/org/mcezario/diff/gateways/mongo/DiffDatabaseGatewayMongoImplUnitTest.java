package org.mcezario.diff.gateways.mongo;

import com.mongodb.MongoClientException;
import org.junit.Before;
import org.junit.Test;
import org.mcezario.diff.domains.Diff;
import org.mcezario.diff.gateways.DiffDatabaseGateway;
import org.mcezario.diff.gateways.exceptions.DiffDatabaseException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.VerificationModeFactory;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DiffDatabaseGatewayMongoImplUnitTest {

    @InjectMocks
    private DiffDatabaseGateway gateway = new DiffDatabaseGatewayMongoImpl();

    @Mock
    private DiffRepository repository;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldStartSavingLeftSideSuccessfully() {

        // Given
        final String id = "1";
        final String content = "LEFT SIDE";

        // Prepare
        when(repository.findById(id)).thenReturn(Optional.empty());

        final Diff diffToBeInserted = Diff.newLeftSide(id, content);
        final Diff diffInserted = Diff.newLeftSide(id, content);
        when(repository.save(diffToBeInserted)).thenReturn(diffInserted);

        // When
        final String leftSide = gateway.insertLeftSide(id, content);

        // Then
        assertNotNull(leftSide);
        assertEquals(content, leftSide);
        verify(repository, VerificationModeFactory.times(1)).findById(id);

        final ArgumentCaptor<Diff> proposalArgumentCaptor = ArgumentCaptor.forClass(Diff.class);
        verify(repository, times(1)).save(proposalArgumentCaptor.capture());
        assertEquals(diffToBeInserted, proposalArgumentCaptor.getValue());
    }

    @Test
    public void shouldStartSavingRightSideSuccessfully() {

        // Given
        final String id = "1";
        final String content = "RIGHT SIDE";

        // Prepare
        final Diff diffToBeInserted = Diff.newRightSide(id, content);
        final Diff diffInserted = Diff.newRightSide(id, content);

        when(repository.findById(id)).thenReturn(Optional.empty());
        when(repository.save(diffToBeInserted)).thenReturn(diffInserted);

        // When
        final String rightSide = gateway.insertRightSide(id, content);

        // Then
        assertNotNull(rightSide);
        assertEquals(content, rightSide);
        verify(repository, VerificationModeFactory.times(1)).findById(id);

        final ArgumentCaptor<Diff> proposalArgumentCaptor = ArgumentCaptor.forClass(Diff.class);
        verify(repository, times(1)).save(proposalArgumentCaptor.capture());
        assertEquals(diffToBeInserted, proposalArgumentCaptor.getValue());
    }

    @Test
    public void shouldSaveLeftSuccessfullyWithoutAffectRightSideAlreadySaved() {

        // Given
        final String id = "1";
        final String content = "LEFT SIDE";

        // Prepare
        final Diff diffInserted = Diff.newRightSide(id, "RIGHT SIDE");
        when(repository.findById(id)).thenReturn(Optional.of(diffInserted));

        final Diff diffToBeInserted = diffInserted.fillLeftSide(content);
        when(repository.save(diffToBeInserted)).thenReturn(diffInserted.fillLeftSide(content));

        // When
        final String leftSide = gateway.insertLeftSide(id, content);

        // Then
        assertNotNull(leftSide);
        assertEquals(content, leftSide);
        verify(repository, VerificationModeFactory.times(1)).findById(id);

        final ArgumentCaptor<Diff> proposalArgumentCaptor = ArgumentCaptor.forClass(Diff.class);
        verify(repository, times(1)).save(proposalArgumentCaptor.capture());
        assertEquals(diffToBeInserted, proposalArgumentCaptor.getValue());
    }

    @Test
    public void shouldSaveRightSuccessfullyWithoutAffectLeftSideAlreadySaved() {

        // Given
        final String id = "1";
        final String content = "RIGHT SIDE";

        // Prepare
        final Diff diffInserted = Diff.newLeftSide(id, "LEFT SIDE");
        when(repository.findById(id)).thenReturn(Optional.of(diffInserted));

        final Diff diffToBeInserted = diffInserted.fillRightSide(content);
        when(repository.save(diffToBeInserted)).thenReturn(diffInserted.fillRightSide(content));

        // When
        final String rightSide = gateway.insertRightSide(id, content);

        // Then
        assertNotNull(rightSide);
        assertEquals(content, rightSide);
        verify(repository, VerificationModeFactory.times(1)).findById(id);

        final ArgumentCaptor<Diff> proposalArgumentCaptor = ArgumentCaptor.forClass(Diff.class);
        verify(repository, times(1)).save(proposalArgumentCaptor.capture());
        assertEquals(diffToBeInserted, proposalArgumentCaptor.getValue());
    }

    @Test(expected = DiffDatabaseException.class)
    public void shouldGetExceptionWhenSavingLeftSide() {
        // Given
        final String id = "1";
        final String content = "LEFT SIDE";

        // Prepare
        when(repository.findById(id)).thenThrow(new MongoClientException("Error"));

        try {
            // When
            gateway.insertLeftSide(id, content);
        } catch (final DiffDatabaseException e) {

            // Then
            assertEquals("Error to provide left side. id: 1", e.getMessage());
            verify(repository, VerificationModeFactory.times(1)).findById(id);

            throw e;
        }
    }

    @Test(expected = DiffDatabaseException.class)
    public void shouldGetExceptionWhenSavingRightSide() {
        // Given
        final String id = "1";
        final String content = "RIGHT SIDE";

        // Prepare
        when(repository.findById(id)).thenThrow(new MongoClientException("Error"));

        try {
            // When
            gateway.insertRightSide(id, content);
        } catch (final DiffDatabaseException e) {

            // Then
            assertEquals("Error to provide right side. id: 1", e.getMessage());
            verify(repository, VerificationModeFactory.times(1)).findById(id);

            throw e;
        }
    }

}
