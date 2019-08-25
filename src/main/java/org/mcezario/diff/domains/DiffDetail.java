package org.mcezario.diff.domains;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiffDetail {

    private final ComparisonDetail detail;

    private final String similarity;

    private final String difference;

    public static DiffDetail equals() {
        return new DiffDetail(ComparisonDetail.EQUALS, "100", null);
    }

    public static DiffDetail differentSize(final String similarity) {
        return new DiffDetail(ComparisonDetail.DIFFERENT_SIZE, similarity, null);
    }

    public static DiffDetail differentContent(final String similarity, final String difference) {
        return new DiffDetail(ComparisonDetail.DIFFERENT_CONTENT, similarity, difference);
    }

}
