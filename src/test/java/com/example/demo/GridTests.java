package com.example.demo;

public class GridTests {
    public static void main(String[] args) {

        //Minimum Longtitude: -180.0, Maximum Longtitude: 180.0
        //To get Grids of the size of ~10KM you need exactness of 0.1
        //To get Grids of the size of ~1KM you need exactness of 0.01

        GridSystem longitudeGS = new GridSystem(-180., 180., 0.1);
        System.out.println( longitudeGS.numberToGrid(10.12456) );

        GridSystem latitudeGS = new GridSystem(-90., 90., 0.1);
        System.out.println( latitudeGS.numberToGrid(10.1) );

        Coordinate c = new Coordinate(52.520008, 13.404954);
        c.setLongtitudeId(longitudeGS.numberToGrid(c.getLongtitude()));
        c.setLatitudeId(latitudeGS.numberToGrid(c.getLatitude()));
        System.out.println(c);
    }
}
