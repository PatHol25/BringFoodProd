package com.example.demo;

import matchingAlgo.Database.DatabaseHandler;
import matchingAlgo.GS.Coordinate;
import matchingAlgo.GS.GridSystem;
import matchingAlgo.GS.User;
import matchingAlgo.GS.UserDatabase;
import matchingAlgo.matching.ShopperDatabaseSearcher;

import java.util.Random;

public class GridTests {
    public static double getLatitude(){
        Random r = new Random();
        return r.nextDouble() * 180. - 90.;
    }

    public static double getLongitude(){
        Random r = new Random();
        return r.nextDouble() * 360 - 180;
    }

    public static void main(String[] args) throws java.sql.SQLException {
        //Minimum Longtitude: -180.0, Maximum Longtitude: 180.0
        //To get Grids of the size of ~10KM you need exactness of 0.1
        //To get Grids of the size of ~1KM you need exactness of 0.01

        GridSystem longitudeGS = new GridSystem(-180., 180., 0.025 );
        GridSystem latitudeGS = new GridSystem(-90., 90., 0.025 );

        UserDatabase userDB = new UserDatabase("buyers");

        double fxHomeX = 180;
        double fxHomeY = 90;

        Random r = new Random();
        double fxShopX = fxHomeX + r.nextDouble() * 0.2 - 0.1;
        double fxShopY = fxHomeY + r.nextDouble() * 0.2 - 0.1;

        double fxEndX = fxShopX + r.nextDouble() * 0.2 - 0.1;
        double fxEndY = fxShopY + r.nextDouble() * 0.2 - 0.1;

        System.out.println("Home: " + fxHomeX + ", " + fxHomeY);
        System.out.println("Shop: " + fxShopX + ", " + fxShopY);
        System.out.println( "Distance: " + Math.pow( ( Math.pow( ( fxHomeX - fxShopX ), 2 ) + Math.pow( ( fxHomeY - fxShopY ), 2 ) ), 0.5 ) * 111 );


        System.out.println("User Generation...");
        for ( int i = 1; i < 1; i++ ){
            double x = getLongitude() + r.nextDouble() * 0.5 - 0.25;
            double y = getLongitude() + r.nextDouble() * 0.5 - 0.25;

            Coordinate home = new Coordinate( x, y );
            home.setLongtitudeId(longitudeGS.numberToGrid(home.getLongtitude()));
            home.setLatitudeId(latitudeGS.numberToGrid(home.getLatitude()));

            double xs = x + r.nextDouble() * 0.2 - 0.1;
            double ys = y + r.nextDouble() * 0.2 - 0.1;

            Coordinate dmShop = new Coordinate( xs, ys );
            dmShop.setLongtitudeId(longitudeGS.numberToGrid(dmShop.getLongtitude()));
            dmShop.setLatitudeId(latitudeGS.numberToGrid(dmShop.getLatitude()));

            User holzer = new User( String.valueOf( i ), home, dmShop);
            userDB.addUser(holzer);

            if ( i % 10000 == 0 ) System.out.println( i );
        }

        System.out.println("User Generation done.");

        ShopperDatabaseSearcher sds = new ShopperDatabaseSearcher( userDB, longitudeGS, latitudeGS );


        //User 0 is a shopper
        Coordinate home = new Coordinate( fxHomeX, fxHomeY );
        home.setLongtitudeId(longitudeGS.numberToGrid(home.getLongtitude()));
        home.setLatitudeId(latitudeGS.numberToGrid(home.getLatitude()));

        Coordinate sparShop = new Coordinate( fxShopX, fxShopY );
        sparShop.setLongtitudeId(longitudeGS.numberToGrid(sparShop.getLongtitude()));
        sparShop.setLatitudeId(latitudeGS.numberToGrid(sparShop.getLatitude()));

        Coordinate fxEnd = new Coordinate( fxEndX, fxEndY );
        fxEnd.setLongtitudeId(longitudeGS.numberToGrid(sparShop.getLongtitude()));
        fxEnd.setLatitudeId(latitudeGS.numberToGrid(sparShop.getLatitude()));


        User felix = new User( String.valueOf( "0" ), home, sparShop, fxEnd );

        System.out.println("Matching...");
        long start = System.nanoTime();
        int[][] homeshopCells = User.getPossibleGrids( felix.getHomeCoordinate(), felix.getShopCoordinate(), 1, 1, longitudeGS, latitudeGS, true );
        int[][] shopEndCells = User.getPossibleGrids( felix.getHomeCoordinate(), felix.getEndCoordinate(), 1, 1, longitudeGS, latitudeGS, true );
        int[][] gridCells = User.mergeGrids( homeshopCells, shopEndCells );
        System.out.println( "Home-Shop Cells: " + homeshopCells.length + " Shop-End Cells: " + shopEndCells.length + " Grid Cells: " + gridCells.length );


        long gridCellsTime = System.nanoTime();

        for ( int i = 0; i < 1600; i++ ){
            String reservedID = null;        // <- vor der Schleife initialisieren
            while (true) {
                User[] felixPossibleUsers = sds.minDistanceUsers(10, felix, gridCells, true);
                //System.out.println("Possible Users: " + felixPossibleUsers.length);

                boolean matched = false;
                for (User u : felixPossibleUsers) {
                    if (u == null) break;                 // Array kann Lücken enthalten
                    boolean ok = DatabaseHandler.reserveBuyer("buyers", u.getId(), felix.getId());
                    if (ok) {                               // erster freier Kandidat gefunden
                        //System.out.println("Reserviert: " + u.getId());
                        reservedID = u.getId();
                        matched = true;
                        break;                               // innere for-Schleife beenden
                    }
                }
                if (matched) break;                          // <-  while-Schleife beenden
                //System.out.println("Kein freier Käufer in den Top-10 – erneut suchen …");
                break;

            }
            DatabaseHandler.deleteUser("buyers", reservedID);
            System.out.println( i );
        }


        long end = System.nanoTime();
        long elapsed = end - start;

        System.out.println("Dauer: " + (elapsed / 1_000_000) + " ms");
        System.out.println("Grid Cells: " + (gridCellsTime - start) / 1_000_000 + " ms");
        System.out.println("L2 Distance: " + (end - gridCellsTime) / 1_000_000 + " ms");
        System.out.println("Elapsed: " + (end - start) / 1_000_000 + " ms");
        System.out.println("Matching done.");

        DatabaseHandler.shutdownPool();
        System.out.println("Matching done.");

    }
}
