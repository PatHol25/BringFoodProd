package matchingAlgo.matching;

import matchingAlgo.GS.Coordinate;
import matchingAlgo.GS.User;
import matchingAlgo.GS.UserDatabase;

public class GridDetection {
    private UserDatabase userDatabase;

    public GridDetection(UserDatabase userDatabase ){
        this.userDatabase = userDatabase;
    }

    public User[] getPossibleUsers( User user, double approxDistanceWidth, double approxDistanceHeight ){
        //Get a list of users which are close enough to the user's shopping route
        //The approx distance is a factor which is relative to the distance between the user's home and shop coordinate
        //Is the approx distance 0 then the direct distance between the two coordinates is used

        //  ------------------------------------
        //  |              Height              |
        //  |                                  |
        //  |<-AWidth, H ========== S, AWidth  |
        //  |                 |                |
        //  |              Height              |
        //  ------------------------------------

        approxDistanceHeight = Math.max( approxDistanceHeight, 0.0 );
        approxDistanceWidth = Math.max( approxDistanceWidth, 0.0 );

        Coordinate home = user.getHomeCoordinate();
        Coordinate shop = user.getShopCoordinate();


        Vector route = new Vector(home, shop);

        

    }
}
