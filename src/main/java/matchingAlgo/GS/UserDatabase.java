package matchingAlgo.GS;

import matchingAlgo.Database.DatabaseHandler;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;


public class UserDatabase {
    private final String tableName;

    public UserDatabase(String tableName) {
        this.tableName = tableName;
        try {
            // Tabelle anlegen, falls noch nicht vorhanden
            DatabaseHandler.createUserTable(tableName);
        } catch (SQLException e) {
            throw new IllegalStateException("Tabelle '" + tableName + "' konnte nicht angelegt werden", e);
        }
    }


    public void addUser(User u) {
        try {
            int lonId = u.getHomeCoordinate().getLongtitudeId();
            int latId = u.getHomeCoordinate().getLatitudeId();

            String gridID = lonId + "_" + latId;
            Coordinate home = u.getHomeCoordinate();
            Coordinate shop = u.getShopCoordinate();
            Coordinate end  = u.getEndCoordinate();

            DatabaseHandler.addUser(
                    tableName,
                    u.getId(),
                    gridID,
                    home.getLongtitude(),  home.getLatitude(),
                    shop.getLongtitude(),  shop.getLatitude(),
                    end.getLongtitude(),   end.getLatitude(),
                    1.0);
        } catch (SQLException ex) {
            throw new RuntimeException("addUser() fehlgeschlagen", ex);
        }
    }


    public User[] usersOfCell(int lonId, int latId) {
        try {
            List<User> list = DatabaseHandler.getUsersByGrid(tableName, lonId + "_" + latId);
            return list.toArray(new User[0]);          // ←  List  →  Array
        } catch (SQLException ex) {
            ex.printStackTrace();
            return new User[0];
        }
    }

    public void removeUser(User u) {
        try {
            DatabaseHandler.deleteUser(tableName, u.getGrid_id(), u.getId());
        } catch (SQLException ex) {
            throw new RuntimeException("removeUser() fehlgeschlagen", ex);
        }
    }

}
