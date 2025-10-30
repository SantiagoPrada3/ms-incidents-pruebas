package pe.edu.vallegrande.vg_ms_claims_incidents.application.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableReactiveMongoRepositories(basePackages = "pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.repository")
public class MongoConfig extends AbstractReactiveMongoConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Override
    public MongoClient reactiveMongoClient() {
        log.info("Conectando a MongoDB con URI: {}", mongoUri);
        return MongoClients.create(mongoUri);
    }

    @Override
    protected String getDatabaseName() {
        String dbName = "claims-incidents";
        log.info("Usando base de datos: {}", dbName);
        return dbName;
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate(ReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory) {
        return new ReactiveMongoTemplate(reactiveMongoDatabaseFactory);
    }
}