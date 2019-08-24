package org.mcezario.diff.domains;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "diff")
@Getter
@Setter(value = AccessLevel.NONE)
@EqualsAndHashCode
public class Diff {

    @Id
    private final String id;

    private final String left;

    private final String right;

    private Diff(final String id, final String left, final String right) {
        this.id = id;
        this.left = left;
        this.right = right;
    }

    public static Diff newLeftSide(final String id, final String content) {
        return new Diff(id, content, null);
    }

    public static Diff newRightSide(final String id, final String content) {
        return new Diff(id, null, content);
    }

    public Diff fillLeftSide(final String content) {
        return new Diff(id, content, right);
    }

    public Diff fillRightSide(final String content) {
        return new Diff(id, left, content);
    }

}
