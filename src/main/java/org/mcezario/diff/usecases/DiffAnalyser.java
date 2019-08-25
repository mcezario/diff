package org.mcezario.diff.usecases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.mcezario.diff.domains.Diff;
import org.mcezario.diff.domains.DiffDetail;
import org.mcezario.diff.gateways.DiffDatabaseGateway;
import org.mcezario.diff.usecases.exceptions.CalculateDifferenceException;
import org.mcezario.diff.usecases.exceptions.DiffNotFoundException;
import org.mcezario.diff.usecases.exceptions.RequiredSidesException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Collections;
import java.util.Objects;

@Slf4j
@Service
public class DiffAnalyser {

    private static final String SAME_JSON_CONTENT = "SAME_JSON_STRUCTURE";

    @Autowired
    private DiffDatabaseGateway gateway;

    @Autowired
    private ObjectMapper objectMapper;

    public void left(final String id, final String content) {
        gateway.insertLeftSide(id, content);
    }

    public void right(final String id, final String content) {
        gateway.insertRightSide(id, content);
    }

    public DiffDetail compare(final String id) {
        final Diff diff = gateway.findDiffById(id).orElseThrow(DiffNotFoundException::new);
        final String l = diff.getLeft();
        final String r = diff.getRight();

        if (StringUtils.isBlank(l) || StringUtils.isBlank(r)) {
            throw new RequiredSidesException();
        }

        if (StringUtils.equals(l, r)) {

            return DiffDetail.equals();

        } else if (contentLenght(l) != contentLenght(r)) {

            return DiffDetail.differentSize(similarity(l, r));

        }

        return DiffDetail.differentContent(similarity(l, r), calculateDifference(l, r));
    }

    private static int contentLenght(final String content) {
        return Objects.toString(content, StringUtils.EMPTY).length();
    }

    private static String similarity(final String left, final String right) {
        return new BigDecimal(new JaroWinklerSimilarity().apply(left, right) * 100)
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .toString();
    }

    private String calculateDifference(final String left, final String right) {

        try {

            final String difference = JsonDiff.asJson(
                    objectMapper.readTree(Base64.getDecoder().decode(left)),
                    objectMapper.readTree(Base64.getDecoder().decode(right))
            ).toString();

            return StringEscapeUtils.unescapeJson(StringUtils.replace(difference, Collections.emptyList().toString(), SAME_JSON_CONTENT));

        } catch (final Exception e) {
            log.error("Error to calculate the difference between left and right side.", e);
        }

        throw new CalculateDifferenceException();

    }

}
