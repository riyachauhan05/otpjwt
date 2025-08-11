package com.example.otpjwt.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.springframework.config.ArangoConfiguration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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

    protected static final Logger logger = LoggerFactory.getLogger(ArangoConfig.class);

    @Override
    public String database() {
        return this.database;
    }

    @Override
    @Bean
    public ArangoDB.Builder arango() {
        return new ArangoDB.Builder()
                .host(host, port)
                .user(user)
                .password(password)
                .useSsl(useSsl);
    }

    @Bean
    public ArangoDatabase arangoDatabase(ArangoDB.Builder builder) {
        ArangoDB arangoDB = builder.build();
        return arangoDB.db(database);
    }
}
