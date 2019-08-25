package org.mcezario.diff.http.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.mcezario.diff.domains.DiffDetail;

@JsonInclude( JsonInclude.Include.NON_NULL)
@Data
public class DiffResponse {

    private String result;
    private String similiraty;
    private String differente;

    public DiffResponse(final DiffDetail detail) {
        this.result = detail.getDetail().name();
        this.similiraty = detail.getSimilarity();
        this.differente = detail.getDifference();
    }

}
