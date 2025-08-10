// It configures a connection to ArangoDB and exposes beans required to interact 
// with the database in a Spring Boot application.

// package com.chemarc.shared.arango.config;
package com.example.otpjwt.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.springframework.config.ArangoConfiguration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up the ArangoDB connection.
 */
@Configuration // spring configuration class
public class ArangoConfig implements ArangoConfiguration {

    @Value("${arangodb.spring.data.hosts}")
    private String host;

    @Value("${arangodb.spring.data.port}")
    private int port;

    @Value("${arangodb.spring.data.user}")
    private String user;

    @Value("${arangodb.spring.data.useSsl}")
    private boolean useSsl;

    @Value("${arangodb.spring.data.password}")
    private String password;

    @Value("${arangodb.spring.data.database}")
    private String database;


    // Logger for monitoring and debugging
    protected static final Logger logger = LoggerFactory.getLogger(ArangoConfig.class);

    /**
     * Returns the name of the database to be used.
     */
    @Override
    public String database() {
        return this.database;
    }

    /**
     * Configures and provides an ArangoDB.Builder bean.
     * This builder is used to create an ArangoDB instance.
     */
    @Override
    @Bean
    public ArangoDB.Builder arango() {
        return new ArangoDB.Builder()
                .host(host, port)
                .user(user)
                .password(password)
                .useSsl(useSsl);
    }

    /**
     * Provides an ArangoDatabase bean for interacting with the configured database.
     * 
     * @param builder The ArangoDB.Builder instance.
     * @return The ArangoDatabase instance.
     */
    @Bean
    public ArangoDatabase arangoDatabase(ArangoDB.Builder builder) {
        ArangoDB arangoDB = builder.build();
        return arangoDB.db(database);
    }
}
