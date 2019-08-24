package org.mcezario.diff.gateways;

import java.util.Optional;

public interface DiffDatabaseGateway {

    Optional<String> insertLeftSide(String id, String content);

    Optional<String> insertRightSide(String id, String content);

}
