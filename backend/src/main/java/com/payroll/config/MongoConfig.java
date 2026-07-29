package com.payroll.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri:mongodb+srv://sahith6345_db_user:sahithsowmithra@cluster0.ovojrzp.mongodb.net/payroll?retryWrites=true&w=majority&appName=Cluster0}")
    private String mongoUri;

    @Override
    protected String getDatabaseName() {
        return "payroll";
    }

    @Bean
    @Override
    public MongoClient mongoClient() {
        // Sanitize MongoDB URI to strip accidental quotes, double-quotes, newlines, or whitespace
        String sanitizedUri = mongoUri == null ? "" : mongoUri.trim();

        if (sanitizedUri.startsWith("\"") && sanitizedUri.endsWith("\"")) {
            sanitizedUri = sanitizedUri.substring(1, sanitizedUri.length() - 1).trim();
        }
        if (sanitizedUri.startsWith("'") && sanitizedUri.endsWith("'")) {
            sanitizedUri = sanitizedUri.substring(1, sanitizedUri.length() - 1).trim();
        }

        if (!sanitizedUri.startsWith("mongodb://") && !sanitizedUri.startsWith("mongodb+srv://")) {
            sanitizedUri = "mongodb+srv://sahith6345_db_user:sahithsowmithra@cluster0.ovojrzp.mongodb.net/payroll?retryWrites=true&w=majority&appName=Cluster0";
        }

        ConnectionString connectionString = new ConnectionString(sanitizedUri);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        return MongoClients.create(mongoClientSettings);
    }
}
