package org.mcezario.diff.config;

import com.github.fakemongo.Fongo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories("org.mcezario.diff.gateways.mongo")
public class SpringMongoConfiguration {

    private static final String DATABASE_NAME = "integration-tests";

    @Bean
    public MongoTemplate mongoTemplate() {
        final MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
        return mongoTemplate;
    }

    private MongoDbFactory mongoDbFactory() {
        final Fongo fongo = new Fongo("mongodb server in memory");
        return new SimpleMongoDbFactory(fongo.getMongo(), DATABASE_NAME);
    }

}
