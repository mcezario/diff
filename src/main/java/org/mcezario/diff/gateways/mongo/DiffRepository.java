package org.mcezario.diff.gateways.mongo;

import org.mcezario.diff.domains.Diff;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface DiffRepository extends MongoRepository<Diff, String> {

    //

}
