package com.example.demo;

import matchingAlgo.GS.Coordinate;
import matchingAlgo.GS.GridSystem;
import matchingAlgo.GS.User;
import matchingAlgo.Database.UserDatabase;
import matchingAlgo.GS.UserCore;
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

    public static void generateUser( int id, double fxHomeX, double fxHomeY, UserDatabase userDB, GridSystem longitudeGS, GridSystem latitudeGS ){
        Random r = new Random();
        double x = fxHomeX + r.nextDouble() * 100 - 0.5;
        double y = fxHomeY + r.nextDouble() * 100 - 0.5;

        Coordinate home = new Coordinate( x, y );
        home.setLongtitudeId(longitudeGS.numberToGrid(home.getLongtitude()));
        home.setLatitudeId(latitudeGS.numberToGrid(home.getLatitude()));

        double xs = x + r.nextDouble() * 0.2 - 0.1;
        double ys = y + r.nextDouble() * 0.2 - 0.1;

        Coordinate dmShop = new Coordinate( xs, ys );
        dmShop.setLongtitudeId(longitudeGS.numberToGrid(dmShop.getLongtitude()));
        dmShop.setLatitudeId(latitudeGS.numberToGrid(dmShop.getLatitude()));

        UserCore core = new UserCore( String.valueOf( id ) );
        User holzer = new User( core, home, dmShop, dmShop, "", "", false, 0 );

        userDB.addUser(holzer);
    }

    public static void main(String[] args){
        //Minimum Longtitude: -180.0, Maximum Longtitude: 180.0
        //To get Grids of the size of ~10KM you need exactness of 0.1
        //To get Grids of the size of ~1KM you need exactness of 0.01
        System.out.println( "Starting..." );
        GridSystem longitudeGS = new GridSystem(-180., 180., 0.125 );
        GridSystem latitudeGS = new GridSystem(-90., 90., 0.125 );

        UserDatabase userDB = new UserDatabase("buyers", longitudeGS, latitudeGS);

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
        for ( int i = 1; i < 20_000_000; i++ ){
            generateUser( i, fxHomeX, fxHomeY, userDB, longitudeGS, latitudeGS );
            System.out.println(i);
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

        System.out.println("Matching...");
        long start = System.nanoTime();

        UserCore core = new UserCore( String.valueOf( "0" ) );
        User felix = new User( core, home, sparShop, fxEnd, "", "", false, 0 );

        int[][] homeshopCells = User.getPossibleGrids( felix.getHomeCoordinate(), felix.getShopCoordinate(), 1, 1, longitudeGS, latitudeGS, true );
        int[][] shopEndCells = User.getPossibleGrids( felix.getHomeCoordinate(), felix.getEndCoordinate(), 1, 1, longitudeGS, latitudeGS, true );
        int[][] gridCells = User.mergeGrids( homeshopCells, shopEndCells );
        System.out.println( "Home-Shop Cells: " + homeshopCells.length + " Shop-End Cells: " + shopEndCells.length + " Grid Cells: " + gridCells.length );

        long gridCellsTime = System.nanoTime();
        User[] felixPossibleUsers = sds.minDistanceUsers(10, felix, gridCells, true);

        long end = System.nanoTime();
        long elapsed = end - start;

        System.out.println("Dauer: " + (elapsed / 1_000_000) + " ms");
        System.out.println("Grid Cells: " + (gridCellsTime - start) / 1_000_000 + " ms");
        System.out.println("L2 Distance: " + (end - gridCellsTime) / 1_000_000 + " ms");
        System.out.println("Elapsed: " + (end - start) / 1_000_000 + " ms");
        System.out.println("Matching done.");
        System.out.println("Matching done.");

    }
}
