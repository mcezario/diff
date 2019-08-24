package org.mcezario.diff.gateways;

import java.util.Optional;

public interface DiffDatabaseGateway {

    String insertLeftSide(String id, String content);

    String insertRightSide(String id, String content);

}
