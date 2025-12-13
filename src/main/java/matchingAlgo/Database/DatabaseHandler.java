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
 * <ul>
 *   <li>HikariCP-Pool (lazy, Holder-Idiom)</li>
 *   <li>Ausschließlich Prepared Statements</li>
 *   <li>Optimierte Indexe für grid_id / user_id</li>
 * </ul>
 */
public final class DatabaseHandler {

    /* =========================================================
     *                1)  DataSource / Pool  (lazy)
     * ========================================================= */

    /** Thread-sichere, faule Initialisierung durch ClassLoader */
    private static final class Holder {
        private static final HikariDataSource DATA_SOURCE = createDataSource();
    }

    public static DataSource getDataSource() { return Holder.DATA_SOURCE; }
    public static Connection getConnection() throws SQLException { return getDataSource().getConnection(); }

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
            cfg.setUsername(Objects.requireNonNullElse(System.getenv("PG_USER"), "postgres"));
            cfg.setPassword(Objects.requireNonNullElse(System.getenv("PG_PW"),   "sql"));

            cfg.setMaximumPoolSize(20);
            cfg.setMinimumIdle(5);
            cfg.setLeakDetectionThreshold(15_000);           // 15 s
            cfg.setPoolName("bringfood-pool");
            return new HikariDataSource(cfg);

        } catch (Exception ex) {
            throw new IllegalStateException("DataSource konnte nicht initialisiert werden: " + ex.getMessage(), ex);
        }
    }

    private DatabaseHandler() { }   // Utility-Klasse

    /* =========================================================
     *                     2)  DDL-Helfer
     * ========================================================= */

    /** Legt Tabelle <i>und</i> Indexe an (idempotent). */
    public static void createUserTable(String table) throws SQLException {
        String ddl = """
            CREATE TABLE IF NOT EXISTS %s (
              entry_id    SERIAL PRIMARY KEY,
              grid_id     TEXT NOT NULL,
              user_id     TEXT NOT NULL,
              home_x      DOUBLE PRECISION NOT NULL,
              home_y      DOUBLE PRECISION NOT NULL,
              shop_x      DOUBLE PRECISION NOT NULL,
              shop_y      DOUBLE PRECISION NOT NULL,
              delivery_x  DOUBLE PRECISION NOT NULL,
              delivery_y  DOUBLE PRECISION NOT NULL,
              radius      DOUBLE PRECISION NOT NULL DEFAULT 0.0,
              reserved    BOOLEAN   NOT NULL DEFAULT FALSE,
              reserved_by TEXT,
              reserved_at TIMESTAMP
            )""".formatted(table);

        // Index-DDL
        String idxGrid      = "CREATE INDEX IF NOT EXISTS idx_%1$s_grid_id     ON %1$s (grid_id)".formatted(table);
        String idxGridUser  = "CREATE INDEX IF NOT EXISTS idx_%1$s_grid_user   ON %1$s (grid_id, user_id)".formatted(table);
        String idxUser      = "CREATE INDEX IF NOT EXISTS idx_%1$s_user_id     ON %1$s (user_id)".formatted(table);

        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute(ddl);
            s.execute(idxGrid);
            s.execute(idxGridUser);
            s.execute(idxUser);
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

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, gridId);
            ps.setString(2, userId);
            ps.setDouble(3, homeX);   ps.setDouble(4, homeY);
            ps.setDouble(5, shopX);   ps.setDouble(6, shopY);
            ps.setDouble(7, endX);    ps.setDouble(8, endY);
            ps.setDouble(9, radius);
            ps.executeUpdate();
        }
    }

    // ---------- UPDATE ----------
    /**
     * Reserviert einen Käufer, falls noch nicht reserviert.
     * Verwendet grid_id + user_id ⇒ zusammengesetzter Index.
     */
    public static boolean reserveBuyer(String table,
                                       String gridId,
                                       String buyerId,
                                       String shopperId) throws SQLException {

        String sql = """
            UPDATE %s
               SET reserved = TRUE,
                   reserved_by = ?,
                   reserved_at = NOW()
             WHERE grid_id = ? AND user_id = ? AND reserved = FALSE
            """.formatted(table);

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, shopperId);
            ps.setString(2, gridId);
            ps.setString(3, buyerId);
            return ps.executeUpdate() == 1;
        }
    }

    // ---------- DELETE ----------
    /** Löscht exakt einen User anhand grid_id + user_id (Index-gestützt). */
    public static void deleteUser(String table,
                                  String gridId,
                                  String userId) throws SQLException {

        String sql = "DELETE FROM " + table + " WHERE grid_id = ? AND user_id = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, gridId);
            ps.setString(2, userId);
            ps.executeUpdate();
        }
    }

    // ---------- SELECT ----------
    /** Alle nicht reservierten Nutzer einer Zelle (alter Endpunkt, nutzt Index auf grid_id). */
    public static List<User> getUsersByGrid(String table,
                                            String gridId) throws SQLException {

        String sql = "SELECT * FROM " + table + " WHERE grid_id = ? AND reserved = FALSE";
        List<User> result = new ArrayList<>();

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
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

    /** Schnelle Einzelsuche: grid_id + user_id (nutzt zusammengesetzten Index). */
    public static User getUserInGrid(String table,
                                     String gridId,
                                     String userId) throws SQLException {

        String sql = "SELECT * FROM " + table +
                     " WHERE grid_id = ? AND user_id = ? AND reserved = FALSE";

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, gridId);
            ps.setString(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Coordinate home = new Coordinate(rs.getDouble("home_x"), rs.getDouble("home_y"));
                    Coordinate shop = new Coordinate(rs.getDouble("shop_x"), rs.getDouble("shop_y"));
                    Coordinate end  = new Coordinate(rs.getDouble("delivery_x"), rs.getDouble("delivery_y"));
                    return new User(rs.getString("user_id"), home, shop, end);
                }
                return null;
            }
        }
    }

    // ---------- MISC ----------
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

    public static void main(String[] args) throws Exception {

        final String TABLE = "buyers";
        System.out.println("---- TESTLAUF ----");

        // ­Schema säubern
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute("DROP TABLE IF EXISTS " + TABLE);
        }

        // 1) Tabelle + Indexe anlegen
        createUserTable(TABLE);
        System.out.println("Tabelle erstellt.");

        /*
        // 2) Zwei Test-User einfügen
        addUser(TABLE, "u1", "g1_1", 1,2,  3,4, 5,6, 1.0);
        addUser(TABLE, "u2", "g1_1", 7,8,  9,1, 2,3, 1.0);
        System.out.println("2 User eingefügt. Gesamt: " + getTotalUserCount(TABLE));

        // 3) getUsersByGrid
        List<User> list = getUsersByGrid(TABLE, "g1_1");
        System.out.println("getUsersByGrid('g1_1') liefert " + list.size() + " Einträge.");

        // 4) Einzelsuche
        User u = getUserInGrid(TABLE, "g1_1", "u1");
        System.out.println("getUserInGrid => " + (u != null ? u.getId() : "null"));

        // 5) Reservierung
        boolean ok = reserveBuyer(TABLE, "g1_1", "u1", "shopper42");
        System.out.println("reserveBuyer => " + ok);

        // 6) Löschen von u2
        deleteUser(TABLE, "g1_1", "u2");
        System.out.println("Nach deleteUser Gesamt: " + getTotalUserCount(TABLE));

        */

        // 7) Aufräumen & Pool schließen
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute("DROP TABLE IF EXISTS " + TABLE);
        }

        shutdownPool();
        System.out.println("---- ENDE ----");
    }

}
