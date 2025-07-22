package com.example.usermanagement.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@Component
@Slf4j
public class DatabaseInitializer implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        initializeDatabase();
    }

    public void initializeDatabase() {
        // Database configuration from application.yml
        String serverUrl = "jdbc:mysql://localhost:3306/";
        String username = "root";
        String password = "272004";
        String databaseName = "user_management";

        try {
            log.info("Early database initialization: Checking if database '{}' exists...", databaseName);

            // Connect to MySQL server (without specifying database)
            try (Connection connection = DriverManager.getConnection(serverUrl, username, password);
                 Statement statement = connection.createStatement()) {

                // Check if database exists
                ResultSet resultSet = statement.executeQuery(
                    "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '" + databaseName + "'");

                if (!resultSet.next()) {
                    // Database doesn't exist, create it
                    log.info("Database '{}' does not exist. Creating it...", databaseName);
                    statement.executeUpdate("CREATE DATABASE " + databaseName +
                        " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
                    log.info("Database '{}' created successfully!", databaseName);
                } else {
                    log.info("Database '{}' already exists.", databaseName);
                }

                resultSet.close();
            }

        } catch (Exception e) {
            log.error("Error during early database initialization: ", e);
            log.warn("Application will continue assuming database exists...");
        }
    }
}
