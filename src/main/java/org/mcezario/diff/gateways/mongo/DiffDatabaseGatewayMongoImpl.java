package org.mcezario.diff.gateways.mongo;

import org.mcezario.diff.domains.Diff;
import org.mcezario.diff.gateways.DiffDatabaseGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DiffDatabaseGatewayMongoImpl implements DiffDatabaseGateway {

    @Autowired
    private DiffRepository repository;

    @Override
    public Optional<String> insertLeftSide(final String id, String content) {
        final Diff diff = repository.findById(id).orElse(Diff.newLeftSide(id, content));

        return Optional.of(repository.save(diff.fillLeftSide(content)).getLeft());
    }

    @Override
    public Optional<String> insertRightSide(final String id, String content) {
        final Diff diff = repository.findById(id).orElse(Diff.newRightSide(id, content));

        return Optional.of(repository.save(diff.fillRightSide(content)).getRight());
    }

}
