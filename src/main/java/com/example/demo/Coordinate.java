package com.example.demo;

public class Coordinate {
    private double latitude;
    private double longtitude;

    private int latitudeId;
    private int longtitudeId;

    public Coordinate(double latitude, double longtitude) {
        this.latitude = latitude;
        this.longtitude = longtitude;

        latitudeId = -1;
        longtitudeId = -1;
    }

    public void setLatitudeId(int latitudeId) {
        this.latitudeId = latitudeId;
    }
    public void setLongtitudeId(int longtitudeId) {
        this.longtitudeId = longtitudeId;
    }

    public int getLatitudeId() {
        return latitudeId;
    }

    public int getLongtitudeId() {
        return longtitudeId;
    }

    public double getLatitude() {
        return latitude;
    }
    public double getLongtitude() {
        return longtitude;
    }

    public String toString() {
        return "Coordinate:\n" +
                "  Latitude: " + latitude + "\n" +
                "  Longtitude: " + longtitude + "\n" +
                "  GridIds:\n" +
                "    LatId:  " + latitudeId + "\n" +
                "    LongId: " + longtitudeId;
    }
}
