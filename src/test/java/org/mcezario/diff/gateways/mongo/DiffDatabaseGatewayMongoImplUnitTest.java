package org.mcezario.diff.gateways.mongo;

import org.junit.Before;
import org.junit.Test;
import org.mcezario.diff.domains.Diff;
import org.mcezario.diff.gateways.DiffDatabaseGateway;
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
        final Optional<String> leftSide = gateway.insertLeftSide(id, content);

        // Then
        assertNotNull(leftSide);
        assertEquals(content, leftSide.get());
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
        final Optional<String> rightSide = gateway.insertRightSide(id, content);

        // Then
        assertNotNull(rightSide);
        assertEquals(content, rightSide.get());
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
        final Optional<String> leftSide = gateway.insertLeftSide(id, content);

        // Then
        assertNotNull(leftSide);
        assertEquals(content, leftSide.get());
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
        final Optional<String> rightSide = gateway.insertRightSide(id, content);

        // Then
        assertNotNull(rightSide);
        assertEquals(content, rightSide.get());
        verify(repository, VerificationModeFactory.times(1)).findById(id);

        final ArgumentCaptor<Diff> proposalArgumentCaptor = ArgumentCaptor.forClass(Diff.class);
        verify(repository, times(1)).save(proposalArgumentCaptor.capture());
        assertEquals(diffToBeInserted, proposalArgumentCaptor.getValue());
    }

}
