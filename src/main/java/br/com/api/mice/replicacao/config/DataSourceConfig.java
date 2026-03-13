package br.com.api.mice.replicacao.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        String jdbcUrl = properties.getUrl();

        if (jdbcUrl != null && jdbcUrl.startsWith("jdbc:postgresql://")) {
            try {
                DatabaseInfo databaseInfo = DatabaseInfo.fromJdbcUrl(jdbcUrl);
                ensureDatabaseExists(databaseInfo, properties.getUsername(), properties.getPassword());
            } catch (SQLException | URISyntaxException exception) {
                throw new IllegalStateException("Falha ao garantir a criacao do banco PostgreSQL.", exception);
            }
        }

        return properties.initializeDataSourceBuilder().build();
    }

    private void ensureDatabaseExists(DatabaseInfo databaseInfo, String username, String password) throws SQLException {
        SQLException lastException = null;

        for (String adminDatabase : new String[]{"postgres", "template1"}) {
            try (Connection connection = DriverManager.getConnection(databaseInfo.adminJdbcUrl(adminDatabase), username, password)) {
                if (databaseAlreadyExists(connection, databaseInfo.databaseName())) {
                    return;
                }

                try (Statement statement = connection.createStatement()) {
                    String safeDatabaseName = databaseInfo.databaseName().replace("\"", "\"\"");
                    statement.execute("CREATE DATABASE \"" + safeDatabaseName + "\"");
                    return;
                }
            } catch (SQLException exception) {
                lastException = exception;
            }
        }

        throw lastException;
    }

    private boolean databaseAlreadyExists(Connection connection, String databaseName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM pg_database WHERE datname = ?")) {
            statement.setString(1, databaseName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private record DatabaseInfo(String host, int port, String databaseName, String query) {

        private static DatabaseInfo fromJdbcUrl(String jdbcUrl) throws URISyntaxException {
            String rawUrl = jdbcUrl.substring("jdbc:".length());
            URI uri = new URI(rawUrl);
            String path = uri.getPath();
            if (path == null || path.length() <= 1) {
                throw new IllegalArgumentException("URL JDBC sem nome do banco: " + jdbcUrl);
            }

            String databaseName = path.substring(1);
            int port = uri.getPort() == -1 ? 5432 : uri.getPort();
            return new DatabaseInfo(uri.getHost(), port, databaseName, uri.getQuery());
        }

        private String adminJdbcUrl(String adminDatabase) {
            StringBuilder builder = new StringBuilder("jdbc:postgresql://")
                .append(host)
                .append(':')
                .append(port)
                .append('/')
                .append(adminDatabase);

            if (query != null && !query.isBlank()) {
                builder.append('?').append(query);
            }

            return builder.toString();
        }
    }
}
