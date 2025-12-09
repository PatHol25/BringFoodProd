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

        GridSystem longitudeGS = new GridSystem(-180., 180., 0.1 );
        GridSystem latitudeGS = new GridSystem(-90., 90., 0.1 );
        UserDatabase userDB = new UserDatabase(longitudeGS.maxNumberToGrid(), latitudeGS.maxNumberToGrid());

        double fxHomeX = getLongitude();
        double fxHomeY = getLatitude();

        Random r = new Random();
        double fxShopX = fxHomeX + r.nextDouble() * 0.2 - 0.1;
        double fxShopY = fxHomeY + r.nextDouble() * 0.2 - 0.1;

        System.out.println("User Generation...");
        for ( int i = 1; i < 100_000; i++ ){
            //User 0 is a shopper
            fxShopX = fxHomeX + r.nextDouble() * 0.5 - 0.25;
            fxShopY = fxHomeY + r.nextDouble() * 0.5 - 0.25;

            Coordinate home = new Coordinate( fxShopX, fxShopY );
            home.setLongtitudeId(longitudeGS.numberToGrid(home.getLongtitude()));
            home.setLatitudeId(latitudeGS.numberToGrid(home.getLatitude()));

            fxShopX = fxHomeX + r.nextDouble() * 0.2 - 0.1;
            fxShopY = fxHomeY + r.nextDouble() * 0.2 - 0.1;

            Coordinate dmShop = new Coordinate( fxShopX, fxShopY );
            dmShop.setLongtitudeId(longitudeGS.numberToGrid(dmShop.getLongtitude()));
            dmShop.setLatitudeId(latitudeGS.numberToGrid(dmShop.getLatitude()));

            User holzer = new User( String.valueOf( i ), home, dmShop);
            userDB.addUser(holzer);
        }
        System.out.println("User Generation done.");


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

        ShopperDatabaseSearcher sds = new ShopperDatabaseSearcher( userDB );
        User[] felixPossibleUsers = sds.l2DistanceApproximation( 10, felix, gridCells );

        User bestUser = sds.fourPointApproximation( felix, felixPossibleUsers );

        long end = System.nanoTime();
        long elapsed = end - start;

        System.out.println("Dauer: " + (elapsed / 1_000_000) + " ms");
        System.out.println("Matching done.");
    }
}
