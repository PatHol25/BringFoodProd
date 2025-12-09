package com.example.demo;

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

    public static void main(String[] args) {
        //Minimum Longtitude: -180.0, Maximum Longtitude: 180.0
        //To get Grids of the size of ~10KM you need exactness of 0.1
        //To get Grids of the size of ~1KM you need exactness of 0.01

        GridSystem longitudeGS = new GridSystem(-180., 180., 0.025 );
        GridSystem latitudeGS = new GridSystem(-90., 90., 0.025 );
        UserDatabase userDB = new UserDatabase(longitudeGS.maxNumberToGrid(), latitudeGS.maxNumberToGrid());

        double fxHomeX = getLongitude();
        double fxHomeY = getLatitude();
        int fxHomeXGrid = longitudeGS.numberToGrid(fxHomeX);
        int fxHomeYGrid = latitudeGS.numberToGrid(fxHomeY);

        Random r = new Random();
        double fxShopX = fxHomeX + r.nextDouble() * 0.2 - 0.1;
        double fxShopY = fxHomeY + r.nextDouble() * 0.2 - 0.1;

        System.out.println("Home: " + fxHomeX + ", " + fxHomeY);
        System.out.println("Shop: " + fxShopX + ", " + fxShopY);
        System.out.println( "Distance: " + Math.pow( ( Math.pow( ( fxHomeX - fxShopX ), 2 ) + Math.pow( ( fxHomeY - fxShopY ), 2 ) ), 0.5 ) * 111 );

        System.out.println("User Generation...");
        for ( int i = 1; i < 10_000_000; i++ ){
            double x = fxShopX + r.nextDouble() * 0.5 - 0.25;
            double y = fxShopY + r.nextDouble() * 0.5 - 0.25;

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

        }

        System.out.println("User Generation done.");

        ShopperDatabaseSearcher sds = new ShopperDatabaseSearcher( userDB );

        //User 0 is a shopper
        Coordinate home = new Coordinate( fxHomeX, fxHomeY );
        home.setLongtitudeId(longitudeGS.numberToGrid(home.getLongtitude()));
        home.setLatitudeId(latitudeGS.numberToGrid(home.getLatitude()));

        Coordinate sparShop = new Coordinate( fxShopX, fxShopY );
        sparShop.setLongtitudeId(longitudeGS.numberToGrid(sparShop.getLongtitude()));
        sparShop.setLatitudeId(latitudeGS.numberToGrid(sparShop.getLatitude()));

        User felix = new User( String.valueOf( "0" ), home, sparShop);

        System.out.println("Matching...");
        long start = System.nanoTime();
        int[][] gridCells = felix.getPossibleGrids( 0.1, 0.1, longitudeGS, latitudeGS, true );
        System.out.println( "Grid Cells: " + gridCells.length );

        long gridCellsTime = System.nanoTime();

        User[] felixPossibleUsers = sds.l2DistanceApproximation( 10, felix, gridCells );
        System.out.println( "Possible Users: " + felixPossibleUsers.length );
        long l2Time = System.nanoTime();

        User bestUser = sds.fourPointApproximation( felix, felixPossibleUsers );
        long fourPointTime = System.nanoTime();

        long end = System.nanoTime();
        long elapsed = end - start;

        System.out.println("Dauer: " + (elapsed / 1_000_000) + " ms");
        System.out.println("Grid Cells: " + (gridCellsTime - start) / 1_000_000 + " ms");
        System.out.println("L2 Distance: " + (l2Time - gridCellsTime) / 1_000_000 + " ms");
        System.out.println("Four Point: " + (fourPointTime - l2Time) / 1_000_000 + " ms");
        System.out.println("Matching done.");

    }
}
