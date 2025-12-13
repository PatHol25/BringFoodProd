package matchingAlgo.GS;

public class Coordinate {
    private double latitude;
    private double longtitude;

    private int latitudeId;
    private int longtitudeId;

    public Coordinate(double latitude, double longtitude) {
        this.latitude = latitude;
        this.longtitude = longtitude;

        //Ids are set to -1 because they are not yet known
        //They will be set after the grid system is constructed
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

    public double distance(Coordinate other){
        return Math.sqrt( Math.pow( other.latitude - this.latitude, 2 ) + Math.pow( other.longtitude - this.longtitude, 2 ) );
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
