package org.mcezario.diff.usecases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.mcezario.diff.domains.ComparisonDetail;
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

        log.debug("Trying to find diff with id: {}", id);
        final Diff diff = gateway.findDiffById(id).orElseThrow(DiffNotFoundException::new);
        final String l = diff.getLeft();
        final String r = diff.getRight();

        log.debug("Checking data integrity. left: {}, right: {}", l, r);
        if (StringUtils.isBlank(l) || StringUtils.isBlank(r)) {
            throw new RequiredSidesException();
        }

        if (StringUtils.equals(l, r)) {

            log.info("Diff id: {}, result: {}", id, ComparisonDetail.EQUALS);

            /*
             * Here, we don't need to decode base64 and compare left json with right json.
             * Base64 comparison is enough to guarantee the equality.
             */
            return DiffDetail.equals();


        } else if (contentLenght(l) != contentLenght(r)) {

            log.info("Diff id: {}, result: {}", id, ComparisonDetail.DIFFERENT_SIZE);

            /*
             * Here, the propose is just compare the length between two base64 content.
             */
            return DiffDetail.differentSize(similarity(l, r));

        }

        log.debug("Checking the json content from diff id: {}", id);

        /*
         * Here, although the base64 contents are different, the propose is compare the content between them (JSON)
         * and see the differences.
         *
         */
        final DiffDetail detail = DiffDetail.differentContent(similarity(l, r), calculateDifference(l, r));
        log.info("Diff id: {}, result: {}, difference: {}", id, ComparisonDetail.DIFFERENT_CONTENT, detail.getDifference());

        return detail;
    }

    private static int contentLenght(final String content) {
        return Objects.toString(content, StringUtils.EMPTY).length();
    }

    /*
     * This method calculates the similarity from two strings according to Jaro–Winkler distance.
     *
     * More details: https://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance
     */
    private static String similarity(final String left, final String right) {
        return new BigDecimal(new JaroWinklerSimilarity().apply(left, right) * 100)
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .toString();
    }

    /*
     * This method is responsible to calculate the difference between two json contents.
     *
     * More details: https://github.com/flipkart-incubator/zjsonpatch
     */
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
