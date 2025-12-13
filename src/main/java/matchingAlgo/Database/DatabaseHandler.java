package matchingAlgo.Database;

import java.sql.*;

public class DatabaseHandler {
    public static void sendMessage( Connection conn, String message ){
        try( Statement stmt = conn.createStatement() ){
            stmt.execute( message );
            System.out.println( "Message Executed: " + message );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void resetDB( String url, String user, String password ){
        try( Connection conn = DriverManager.getConnection(url, user, password); Statement stmt = conn.createStatement() ){
            stmt.execute("DROP DATABASE IF EXISTS mydb;");
            stmt.execute("CREATE DATABASE mydb;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTable( Connection conn, String tableName ){
        sendMessage( conn, "CREATE TABLE " + tableName + " (grid_id SERIAL PRIMARY KEY, " +
                "user_id TEXT NOT NULL, " +
                "home_x DOUBLE PRECISION NOT NULL, home_y DOUBLE PRECISION NOT NULL, " +
                "shop_x DOUBLE PRECISION NOT NULL, shop_y DOUBLE PRECISION NOT NULL, " +
                "delivery_x DOUBLE PRECISION NOT NULL, delivery_y DOUBLE PRECISION NOT NULL," +
                "radius DOUBLE PRECISION NOT NULL DEFAULT 0.0, " +
                "matched BOOLEAN NOT NULL DEFAULT FALSE," +
                "matched_ID TEXT NULL);" );
    }

    public static void addUserToTable( Connection conn, String tableName, String userId, double homeX, double homeY, double shopX, double shopY, double deliveryX, double deliveryY, double radius ){
        sendMessage( conn, "INSERT INTO " + tableName + " (user_id, home_x, home_y, shop_x, shop_y, delivery_x, delivery_y, radius) VALUES ('" + userId + "', " + homeX + ", " + homeY + ", " + shopX + ", " + shopY + ", " + deliveryX + ", " + deliveryY + ", " + radius + ");" );
    }

    public static void deleteUserFromTable( Connection conn, String tableName, String userId ){
        sendMessage( conn, "DELETE FROM " + tableName + " WHERE user_id = '" + userId + "';" );
    }

    public static void setUserToMatched(Connection conn, String tableName, String userId, String matchedId) {
        String lockQuery = "SELECT * FROM " + tableName + " WHERE user_id = ? FOR UPDATE";
        String updateQuery = "UPDATE " + tableName + " SET matched = TRUE, matched_id = ? WHERE user_id = ?";

        try {
            conn.setAutoCommit(false); // start transaction

            // 1. Lock the row
            try (PreparedStatement lockStmt = conn.prepareStatement(lockQuery)) {
                lockStmt.setString(1, userId);
                lockStmt.executeQuery(); // locks the row
            }

            // 2. Update the row
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setString(1, matchedId);
                updateStmt.setString(2, userId);
                int rows = updateStmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("User " + userId + " set to matched with ID " + matchedId);
                } else {
                    System.out.println("No user found with ID " + userId);
                }
            }

            conn.commit(); // release lock
        } catch (SQLException e) {
            try {
                conn.rollback(); // rollback on error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(true); // restore default
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public static void getUserByID( Connection conn, String user_id ){
        String command = "SELECT * FROM users WHERE user_id = '" + user_id + "';";

        try( Statement stm = conn.createStatement() ){
            boolean hasResult = stm.execute( command );
            if ( hasResult ){
                ResultSet rs = stm.getResultSet();

                while ( rs.next() ){
                    int grid_id = rs.getInt( "grid_id" );
                    System.out.println( "Grid ID: " + grid_id );

                    int matched_id = rs.getInt( "matched_id" );
                    System.out.println( "Matched ID: " + matched_id );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void getUsersByGridId( Connection conn, int grid_id ){
        String command = "SELECT * FROM users WHERE grid_id = " + grid_id + ";";

        try( Statement stm = conn.createStatement() ){
            boolean hasResult = stm.execute( command );
            if ( hasResult ){
                ResultSet rs = stm.getResultSet();

                while ( rs.next() ){
                    int gi = rs.getInt( "grid_id" );
                    System.out.println( "Grid ID: " + gi );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/mydb";
        String user = "postgres";
        String password = "sql";

        //resetDB( "jdbc:postgresql://localhost:5432/postgres", user, password );

        try( Connection conn = DriverManager.getConnection(url, user, password) ){
            //createTable( conn, "users" );
            addUserToTable( conn, "users", "1", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 );
            getUserByID( conn, "1" );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
