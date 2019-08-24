package org.mcezario.diff.usecases;

import org.mcezario.diff.gateways.DiffDatabaseGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiffAnalyser {

    @Autowired
    private DiffDatabaseGateway gateway;

    public String left(final String id, final String content) {
        return gateway.insertLeftSide(id, content).get();
    }

    public String right(final String id, final String content) {
        return gateway.insertRightSide(id, content).get();
    }

    public String difference(final long id) {
        return "difference";
    }

}
