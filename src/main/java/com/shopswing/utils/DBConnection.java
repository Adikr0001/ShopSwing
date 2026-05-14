package com.shopswing.utils;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * JDBC connections for local SQLite or hosted PostgreSQL (e.g. Render {@code DATABASE_URL}).
 *
 * <p>Priority: {@code -Dshopswing.sqlite.path=} (SQLite file) &rarr;
 * {@code JDBC_DATABASE_URL} &rarr; {@code DATABASE_URL} (postgres) &rarr;
 * default SQLite file {@code shopswing.db} in the Maven project directory (path is baked
 * in at {@code mvn package} time via {@code shopswing-db.properties}, so it matches the
 * file in your repo regardless of {@code user.dir}). Fallback: {@code user.dir/shopswing.db}.
 * Override anytime: {@code -Dshopswing.sqlite.path=C:/path/to/shopswing.db}.
 */
public class DBConnection {

    private static final String DB_URL = buildJdbcUrl();
    private static final boolean POSTGRES = DB_URL.startsWith("jdbc:postgresql:");

    private static String getenv(String name) {
        String v = System.getenv(name);
        return v == null ? "" : v;
    }

    private static String buildJdbcUrl() {
        String override = System.getProperty("shopswing.sqlite.path", "").trim();
        if (!override.isEmpty()) {
            if (override.startsWith("jdbc:")) {
                return override;
            }
            return "jdbc:sqlite:" + override.replace('\\', '/');
        }

        String jdbcFromEnv = getenv("JDBC_DATABASE_URL").trim();
        if (!jdbcFromEnv.isEmpty()) {
            return jdbcFromEnv;
        }

        String databaseUrl = getenv("DATABASE_URL").trim();
        if (!databaseUrl.isEmpty()
                && (databaseUrl.startsWith("postgres://") || databaseUrl.startsWith("postgresql://"))) {
            return convertRenderPostgresUrlToJdbc(databaseUrl);
        }

        String projectDb = readPackagedSqliteAbsolutePath();
        if (projectDb != null) {
            return "jdbc:sqlite:" + new File(projectDb).getAbsolutePath().replace('\\', '/');
        }

        File localDb = new File(System.getProperty("user.dir"), "shopswing.db");
        return "jdbc:sqlite:" + localDb.getAbsolutePath().replace('\\', '/');
    }

    /**
     * Path from {@code shopswing-db.properties}, substituted at Maven build from {@code project.basedir}.
     * Returns {@code null} if missing, not filtered yet, or still contains a placeholder.
     */
    private static String readPackagedSqliteAbsolutePath() {
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("shopswing-db.properties")) {
            if (in == null) {
                return null;
            }
            Properties p = new Properties();
            p.load(in);
            String path = p.getProperty("sqlite.absolute.path", "").trim();
            if (path.isEmpty() || path.contains("${")) {
                return null;
            }
            return path;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Render / Heroku style {@code postgres://user:pass@host:5432/dbname} to JDBC URL with ssl.
     */
    private static String convertRenderPostgresUrlToJdbc(String databaseUrl) {
        try {
            URI uri = new URI(databaseUrl);
            String scheme = uri.getScheme();
            if (!"postgres".equals(scheme) && !"postgresql".equals(scheme)) {
                throw new IllegalArgumentException("Unsupported scheme: " + scheme);
            }
            String host = uri.getHost();
            if (host == null) {
                throw new IllegalArgumentException("Missing host");
            }
            int port = uri.getPort() > 0 ? uri.getPort() : 5432;
            String path = uri.getPath();
            if (path == null || path.length() <= 1) {
                throw new IllegalArgumentException("Missing database name in path");
            }
            String dbName = path.substring(1);
            String userInfo = uri.getUserInfo();
            if (userInfo == null || userInfo.isEmpty()) {
                throw new IllegalArgumentException("Missing user info");
            }
            int colon = userInfo.indexOf(':');
            String user = colon > 0
                    ? URLDecoder.decode(userInfo.substring(0, colon), "UTF-8")
                    : URLDecoder.decode(userInfo, "UTF-8");
            String password = (colon > 0 && colon < userInfo.length() - 1)
                    ? URLDecoder.decode(userInfo.substring(colon + 1), "UTF-8")
                    : "";

            String encUser = URLEncoder.encode(user, "UTF-8");
            String encPass = URLEncoder.encode(password, "UTF-8");
            return "jdbc:postgresql://" + host + ":" + port + "/" + dbName
                    + "?sslmode=require&user=" + encUser + "&password=" + encPass;
        } catch (Exception e) {
            throw new RuntimeException("Invalid DATABASE_URL for PostgreSQL: " + e.getMessage(), e);
        }
    }

    static {
        try {
            if (POSTGRES) {
                Class.forName("org.postgresql.Driver");
            } else {
                Class.forName("org.sqlite.JDBC");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found for: " + (POSTGRES ? "PostgreSQL" : "SQLite"));
            e.printStackTrace();
            throw new RuntimeException("JDBC Driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static boolean isPostgreSql() {
        return POSTGRES;
    }

    /** Full JDBC URL (avoid logging in production; use {@link #getJdbcUrlForLogs()}). */
    public static String getJdbcUrl() {
        return DB_URL;
    }

    /** JDBC URL with password masked for console logs. */
    public static String getJdbcUrlForLogs() {
        if (!POSTGRES || !DB_URL.contains("password=")) {
            return DB_URL;
        }
        return DB_URL.replaceAll("(password=)[^&]*", "$1****");
    }

    /** Human-readable storage location for support messages. */
    public static String getSqliteFilePath() {
        if (DB_URL.startsWith("jdbc:sqlite:")) {
            return new File(DB_URL.substring("jdbc:sqlite:".length())).getAbsolutePath();
        }
        return "(PostgreSQL — not a local .db file; inspect data in the Render PostgreSQL dashboard or any client using DATABASE_URL)";
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("SUCCESS: Connected to database!");
                System.out.println("URL (masked): " + getJdbcUrlForLogs());
            }
        } catch (SQLException e) {
            System.err.println("FAILED: Could not connect to database.");
            System.err.println("Error: " + e.getMessage());
        }
    }
}
