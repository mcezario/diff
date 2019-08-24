package org.mcezario.diff.config.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.management.JMXConnectionPoolListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Bean
    public MongoClient mongoClient() {
        final MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder()
                .addConnectionPoolListener(new JMXConnectionPoolListener());
        return new MongoClient(new MongoClientURI(mongoUri, optionsBuilder));
    }

}
