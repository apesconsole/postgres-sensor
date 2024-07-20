package com.apesconsole.test.postgres_sensor;

import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Executors;

@Service
public class PostgresListenerService {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @PostConstruct
    public void startListening() {
    	Executors.newSingleThreadExecutor().submit(() -> {
            try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                 Statement statement = connection.createStatement()) {

                statement.execute("LISTEN events");
                PGConnection pgConnection = connection.unwrap(PGConnection.class);

                while (true) {
                    PGNotification[] notifications = pgConnection.getNotifications(5000);
                    if (notifications != null) {
                        for (PGNotification notification : notifications) {
                            System.out.println("Received notification: " + notification.getParameter());
                            // Process the notification here
                        }
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
