package org.mcezario.diff.gateways.mongo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mcezario.diff.domains.Diff;
import org.mcezario.diff.gateways.DiffDatabaseGateway;
import org.mcezario.diff.gateways.exceptions.DiffDatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DiffDatabaseGatewayMongoImpl implements DiffDatabaseGateway {

    @Autowired
    private DiffRepository repository;

    @Override
    public String insertLeftSide(final String id, final String content) {
        try {
            log.debug("Finding left side. id: {}", id);
            final Diff diff = repository.findById(id).orElse(Diff.newLeftSide(id, content));

            log.info("Saving left side with id: {}, content: {}", id, StringUtils.abbreviate(content, 20));
            return repository.save(diff.fillLeftSide(content)).getLeft();

        } catch (final Exception e) {
            log.error("Error to find/save left side. id: {}", id, e);
        }

        throw DiffDatabaseException.newLeftSideException(id);
    }

    @Override
    public String insertRightSide(final String id, String content) {
        try {
            log.debug("Finding right side. id: {}", id);
            final Diff diff = repository.findById(id).orElse(Diff.newRightSide(id, content));

            log.info("Saving right side. id: {}, content: {}", id, StringUtils.abbreviate(content, 20));
            return repository.save(diff.fillRightSide(content)).getRight();

        } catch (final Exception e) {
            log.error("Error to find/save right side. id: {}", id, e);
        }

        throw DiffDatabaseException.newRightSideException(id);
    }

}
