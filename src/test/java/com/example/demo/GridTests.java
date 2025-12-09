package com.example.demo;

import matchingAlgo.GS.Coordinate;
import matchingAlgo.GS.GridSystem;
import matchingAlgo.GS.User;
import matchingAlgo.GS.UserDatabase;
import matchingAlgo.matching.ShopperDatabaseSearcher;

import java.util.Random;

public class GridTests {
    public static void main(String[] args) {

        //Minimum Longtitude: -180.0, Maximum Longtitude: 180.0
        //To get Grids of the size of ~10KM you need exactness of 0.1
        //To get Grids of the size of ~1KM you need exactness of 0.01

        GridSystem longitudeGS = new GridSystem(-180., 180., 1.);
        GridSystem latitudeGS = new GridSystem(-90., 90., 1.);
        UserDatabase userDB = new UserDatabase(longitudeGS.maxNumberToGrid(), latitudeGS.maxNumberToGrid());

        System.out.println("User Generation...");
        for ( int i = 1; i < 100; i++ ){
            Random r = new Random();
            //User 0 is a shopper
            Coordinate home = new Coordinate( r.nextInt( 360 ), r.nextInt( 180 ));
            home.setLongtitudeId(longitudeGS.numberToGrid(home.getLongtitude()));
            home.setLatitudeId(latitudeGS.numberToGrid(home.getLatitude()));

            Coordinate dmShop = new Coordinate(r.nextInt( 360 ), r.nextInt( 180 ));
            dmShop.setLongtitudeId(longitudeGS.numberToGrid(dmShop.getLongtitude()));
            dmShop.setLatitudeId(latitudeGS.numberToGrid(dmShop.getLatitude()));

            User holzer = new User( String.valueOf( i ), home, dmShop);
            userDB.addUser(holzer);
        }
        System.out.println("User Generation done.");

        Random r = new Random();
        //User 0 is a shopper
        Coordinate home = new Coordinate( r.nextInt( 360 ), r.nextInt( 180 ));
        home.setLongtitudeId(longitudeGS.numberToGrid(home.getLongtitude()));
        home.setLatitudeId(latitudeGS.numberToGrid(home.getLatitude()));

        Coordinate sparShop = new Coordinate(r.nextInt( 360 ), r.nextInt( 180 ));
        sparShop.setLongtitudeId(longitudeGS.numberToGrid(sparShop.getLongtitude()));
        sparShop.setLatitudeId(latitudeGS.numberToGrid(sparShop.getLatitude()));

        User felix = new User( String.valueOf( "0" ), home, sparShop);

        System.out.println("Searching for possible grids...");
        int[][] gridCells = felix.getPossibleGrids( 0.1, 0.1, longitudeGS, latitudeGS, true );
        System.out.println("Searching done.");

        ShopperDatabaseSearcher sds = new ShopperDatabaseSearcher( userDB );
        User[] felixPossibleUsers = sds.l2DistanceApproximation( 10, felix, gridCells );

        User bestUser = sds.fourPointApproximation( felix, felixPossibleUsers );
    }
}
