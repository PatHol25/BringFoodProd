package matchingAlgo.GS;

public class User {
    private String id;
    private Coordinate homeCoordinate;
    private Coordinate shopCoordinate;

    public User(String id, Coordinate homeCoordinate, Coordinate shopCoordinate) {
        this.id = id;
        this.homeCoordinate = homeCoordinate;
        this.shopCoordinate = shopCoordinate;
    }

    public Coordinate getShopCoordinate(){
        return this.shopCoordinate;
    }

    public Coordinate getHomeCoordinate(){
        return this.homeCoordinate;
    }

    public String getId(){
        return this.id;
    }

    public String toString(){
        return "User:\n" +
                "  ID: " + this.id + "\n"+
                "  Home: \n  " + this.homeCoordinate.toString() +
                "  Shop: \n  " + this.shopCoordinate.toString();
    }
}
