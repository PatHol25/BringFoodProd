package matchingAlgo.Database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import matchingAlgo.GS.Coordinate;
import matchingAlgo.GS.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Zentrale Utility-Klasse für PostgreSQL-Zugriffe.
 *
 *  – Verwendet HikariCP-Connection-Pooling (lazy loaded, Holder-Idiom)<br>
 *  – Arbeitet ausschließlich mit Prepared Statements<br>
 *  – Bietet grundlegende CRUD-Methoden für die User-Tabelle<br>
 *  – Stellt eine {@link #shutdownPool()}-Methode bereit, um alle
 *    Verbindungen sauber zu schließen und »database is being accessed
 *    by other users«-Fehler zu vermeiden.
 */
public final class DatabaseHandler {

    /* =========================================================
     *                1)  DataSource / Pool  (lazy)
     * ========================================================= */

    /** Thread-sichere, faule Initialisierung durch VM-Class-Loader */
    private static final class Holder {
        private static final HikariDataSource DATA_SOURCE = createDataSource();
    }

    public static DataSource getDataSource() {
        return Holder.DATA_SOURCE;
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    /** Pool & alle Verbindungen schließen (z. B. im Shutdown-Hook). */
    public static void shutdownPool() {
        if (!Holder.DATA_SOURCE.isClosed()) {
            Holder.DATA_SOURCE.close();
            System.out.println("[DatabaseHandler] Pool wurde geschlossen.");
        }
    }

    private static HikariDataSource createDataSource() {
        try {
            HikariConfig cfg = new HikariConfig();
            cfg.setJdbcUrl (Objects.requireNonNullElse(
                    System.getenv("PG_URL"),
                    "jdbc:postgresql://localhost:5432/mydb"));
            cfg.setUsername(Objects.requireNonNullElse(
                    System.getenv("PG_USER"), "postgres"));
            cfg.setPassword(Objects.requireNonNullElse(
                    System.getenv("PG_PW"),   "sql"));

            cfg.setMaximumPoolSize(20);
            cfg.setMinimumIdle(5);
            cfg.setLeakDetectionThreshold(15_000);   // 15 s

            cfg.setPoolName("bringfood-pool");
            return new HikariDataSource(cfg);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Konnte DataSource nicht initialisieren. " +
                    "Ist die Datenbank erreichbar? : " + ex.getMessage(), ex);
        }
    }

    private DatabaseHandler() {}   // utility class

    /* =========================================================
     *                     2)  DDL-Helfer
     * ========================================================= */

    public static void createUserTable(String table) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS %s (
              entry_id   SERIAL PRIMARY KEY,
              grid_id    TEXT NOT NULL,
              user_id    TEXT NOT NULL,
              home_x     DOUBLE PRECISION NOT NULL,
              home_y     DOUBLE PRECISION NOT NULL,
              shop_x     DOUBLE PRECISION NOT NULL,
              shop_y     DOUBLE PRECISION NOT NULL,
              delivery_x DOUBLE PRECISION NOT NULL,
              delivery_y DOUBLE PRECISION NOT NULL,
              radius     DOUBLE PRECISION NOT NULL DEFAULT 0.0,
              reserved    BOOLEAN   NOT NULL DEFAULT FALSE,
              reserved_by TEXT,
              reserved_at TIMESTAMP
            )""".formatted(table);

        try (Connection c = getConnection();
             Statement  s = c.createStatement()) {
            s.execute(sql);
        }
    }

    /* =========================================================
     *                     3)  CRUD-Operationen
     * ========================================================= */

    // ---------- INSERT ----------
    public static void addUser(String table,
                               String userId, String gridId,
                               double homeX, double homeY,
                               double shopX, double shopY,
                               double endX,  double endY,
                               double radius) throws SQLException {

        String sql = "INSERT INTO " + table +
                     "(grid_id,user_id,home_x,home_y,shop_x,shop_y,delivery_x,delivery_y,radius) " +
                     "VALUES (?,?,?,?,?,?,?,?,?)";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, gridId);
            ps.setString(2, userId);
            ps.setDouble(3, homeX);
            ps.setDouble(4, homeY);
            ps.setDouble(5, shopX);
            ps.setDouble(6, shopY);
            ps.setDouble(7, endX);
            ps.setDouble(8, endY);
            ps.setDouble(9, radius);

            ps.executeUpdate();
        }
    }

    public static boolean reserveBuyer(String table, String buyerId, String shopperId) throws SQLException {
        String sql =
                "UPDATE " + table + " " +
                        "SET reserved = TRUE, reserved_by = ?, reserved_at = NOW() " +
                        "WHERE user_id = ? AND reserved = FALSE";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, shopperId);
            ps.setString(2, buyerId);
            return ps.executeUpdate() == 1;          // genau eine Zeile geändert ⇒ Reservierung gelungen
        }
    }

    // ---------- DELETE ----------
    public static void deleteUser(String table, String userId) throws SQLException {
        String sql = "DELETE FROM " + table + " WHERE user_id = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, userId);
            ps.executeUpdate();
        }
    }

    // ---------- SELECT ----------
    public static List<User> getUsersByGrid(String table, String gridId) throws SQLException {
        String sql = "SELECT * FROM " + table + " WHERE grid_id = ? AND reserved = FALSE";
        List<User> result = new ArrayList<>();

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, gridId);
            ps.setFetchSize(256);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Coordinate home = new Coordinate(rs.getDouble("home_x"), rs.getDouble("home_y"));
                    Coordinate shop = new Coordinate(rs.getDouble("shop_x"), rs.getDouble("shop_y"));
                    Coordinate end  = new Coordinate(rs.getDouble("delivery_x"), rs.getDouble("delivery_y"));
                    result.add(new User(rs.getString("user_id"), home, shop, end));
                }
            }
        }
        return result;
    }

    public static long getTotalUserCount(String table) {
        String sql = "SELECT COUNT(*) FROM " + table;
        try (Connection c = getConnection();
             Statement  st = c.createStatement();
             ResultSet  rs = st.executeQuery(sql)) {

            return rs.next() ? rs.getLong(1) : 0L;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1L;
        }
    }


    /* =========================================================
     *                   4)  Mini-Test-Lauf
     * ========================================================= */
    public static void main(String[] args) throws Exception {

        // 1) Datenbank/Schema zurücksetzen (nur Demo!)
        try (Connection c = getConnection();
             Statement  s = c.createStatement()) {
            s.execute("DROP TABLE IF EXISTS buyers");
        }

        createUserTable("buyers");

        /*
        // 2) Einen Test-User einfügen
        addUser("users", "demoUser", "g0",
                1, 2, 3, 4, 5, 6, 42);
        */

        // 3) Gesamtanzahl abrufen
        long count = getTotalUserCount("buyers");
        System.out.println("Aktuelle Anzahl der Einträge: " + count);

        // 4) Pool schließen, damit JVM sauber beendet
        shutdownPool();

    }
}
