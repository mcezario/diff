package org.mcezario.diff.gateways;

import org.mcezario.diff.domains.Diff;

import java.util.Optional;

public interface DiffDatabaseGateway {

    String insertLeftSide(String id, String content);

    String insertRightSide(String id, String content);

    Optional<Diff> findDiffById(String id);

}
