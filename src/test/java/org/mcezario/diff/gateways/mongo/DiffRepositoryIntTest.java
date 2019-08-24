package org.mcezario.diff.gateways.mongo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mcezario.diff.config.SpringMongoConfiguration;
import org.mcezario.diff.domains.Diff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringMongoConfiguration.class })
public class DiffRepositoryIntTest {

    @Autowired
    private DiffRepository repository;

    @Before
    public void setUp() {
        this.repository.deleteAll();
    }

    @Test
    public void shouldStartSavingLeftSideSuccessfully() {
        // Given
        final Diff content = Diff.newLeftSide("ID1", "LEFT SIDE");

        // When
        final Diff diffSaved = repository.save(content);

        // Then
        final Diff diff = repository.findById("ID1").get();

        assertNotNull(diff);
        assertEquals(content.getLeft(), diff.getLeft());
        assertNull(diff.getRight());

        assertEquals(diffSaved, diff);
    }

    @Test
    public void shouldStartSavingRightSideSuccessfully() {
        // Given
        final Diff content = Diff.newRightSide("ID1", "RIGHT Content");

        // When
        final Diff diffSaved = repository.save(content);

        // Then
        final Diff diff = repository.findById("ID1").get();

        assertNotNull(diff);
        assertEquals(content.getRight(), diff.getRight());
        assertNull(diff.getLeft());
        assertEquals(diffSaved, diff);
    }

    @Test
    public void shouldSaveLeftSuccessfullyWithoutAffectRightSideAlreadySaved() {
        // Prepare
        final Diff rightContent = Diff.newRightSide("ID1", "RIGHT SIDE");
        final Diff rightContentSaved = repository.save(rightContent);

        // Given
        final Diff leftContent = rightContentSaved.fillLeftSide("LEFT SIDE");

        // When
        final Diff diffSaved = repository.save(leftContent);

        // Then
        final Diff diff = repository.findById("ID1").get();

        assertNotNull(diff);
        assertEquals(leftContent.getLeft(), diff.getLeft());
        assertEquals(rightContent.getRight(), diff.getRight());

        assertEquals(diffSaved, diff);
    }

    @Test
    public void shouldSaveRightSuccessfullyWithoutAffectLeftSideAlreadySaved() {
        // Prepare
        final Diff leftContent = Diff.newLeftSide("ID1", "lEFT SIDE");
        final Diff leftContentSaved = repository.save(leftContent);

        // Given
        final Diff rightContent = leftContentSaved.fillRightSide("RIGHT SIDE");

        // When
        final Diff diffSaved = repository.save(rightContent);

        // Then
        final Diff diff = repository.findById("ID1").get();

        assertNotNull(diff);
        assertEquals(leftContent.getLeft(), diff.getLeft());
        assertEquals(rightContent.getRight(), diff.getRight());

        assertEquals(diffSaved, diff);
    }

}
