package org.mcezario.diff.http.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.mcezario.diff.domains.DiffDetail;

@JsonInclude( JsonInclude.Include.NON_NULL)
@Data
public class DiffResponse {

    private String result;
    private String similarity;
    private String difference;

    public DiffResponse(final DiffDetail detail) {
        this.result = detail.getDetail().name();
        this.similarity = detail.getSimilarity();
        this.difference = detail.getDifference();
    }

}
