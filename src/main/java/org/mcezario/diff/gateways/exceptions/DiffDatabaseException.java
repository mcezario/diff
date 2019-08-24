package org.mcezario.diff.gateways.exceptions;

public class DiffDatabaseException extends RuntimeException {

    private static final String MESSAGE = "Error to provide %s side. id: %s";

    public DiffDatabaseException(final String e) {
        super(e);
    }

    public static DiffDatabaseException newLeftSideException(final String id) {
        return new DiffDatabaseException(String.format(MESSAGE, "left", id));
    }

    public static DiffDatabaseException newRightSideException(final String id) {
        return new DiffDatabaseException(String.format(MESSAGE, "right", id));
    }

}
