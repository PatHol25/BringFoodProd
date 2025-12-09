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

        GridSystem longitudeGS = new GridSystem(-180., 180., 0.1);
        System.out.println( longitudeGS.numberToGrid(10.12456) );

        GridSystem latitudeGS = new GridSystem(-90., 90., 0.1);
        System.out.println( latitudeGS.numberToGrid(10.1) );

        //User 0
        Coordinate home = new Coordinate(52.520008, 13.404954);
        home.setLongtitudeId(longitudeGS.numberToGrid(home.getLongtitude()));
        home.setLatitudeId(latitudeGS.numberToGrid(home.getLatitude()));
        System.out.println(home);

        Coordinate dmShop = new Coordinate(52.520008, 13.404954);
        dmShop.setLongtitudeId(longitudeGS.numberToGrid(dmShop.getLongtitude()));
        dmShop.setLatitudeId(latitudeGS.numberToGrid(dmShop.getLatitude()));
        System.out.println(dmShop);

        User Holzer = new User("0", home, dmShop);
        System.out.println(Holzer);

        UserDatabase userDB = new UserDatabase(longitudeGS.maxNumberToGrid(), latitudeGS.maxNumberToGrid());
        userDB.addUser(Holzer);

        userDB.printDatabase();
        userDB.removeUser(Holzer);
        userDB.printDatabase();
    }
}
