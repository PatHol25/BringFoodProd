package com.example.demo;

import matchingAlgo.GS.Coordinate;
import matchingAlgo.GS.GridSystem;
import matchingAlgo.GS.User;
import matchingAlgo.GS.UserDatabase;

public class GridTests {
    public static void main(String[] args) {

        //Minimum Longtitude: -180.0, Maximum Longtitude: 180.0
        //To get Grids of the size of ~10KM you need exactness of 0.1
        //To get Grids of the size of ~1KM you need exactness of 0.01

        GridSystem longitudeGS = new GridSystem(-180., 180., 1.);
        GridSystem latitudeGS = new GridSystem(-90., 90., 1.);

        //User 0
        Coordinate home = new Coordinate(0, 170);
        home.setLongtitudeId(longitudeGS.numberToGrid(home.getLongtitude()));
        home.setLatitudeId(latitudeGS.numberToGrid(home.getLatitude()));

        Coordinate dmShop = new Coordinate(0, -170);
        dmShop.setLongtitudeId(longitudeGS.numberToGrid(dmShop.getLongtitude()));
        dmShop.setLatitudeId(latitudeGS.numberToGrid(dmShop.getLatitude()));

        User holzer = new User("0", home, dmShop);

        UserDatabase userDB = new UserDatabase(longitudeGS.maxNumberToGrid(), latitudeGS.maxNumberToGrid());
        userDB.addUser(holzer);
        userDB.removeUser(holzer);

        int[][] gridCells = holzer.getPossibleGrids( 0.1, 0.1, longitudeGS, latitudeGS, true );
        for ( int[] cell : gridCells ) {
            System.out.println( cell[0] + " " + cell[1] );
        }
    }
}
