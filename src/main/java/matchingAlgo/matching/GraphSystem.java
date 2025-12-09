package matchingAlgo.matching;

import matchingAlgo.GS.User;

public class GraphSystem {
    public static double threePointDistance(User shopper, User buyer, double ellipsis ){
        ellipsis = Math.max( ellipsis, 1.0 );
        double d1 = new Vector( shopper.getHomeCoordinate(), shopper.getShopCoordinate() ).length() * ellipsis;
        double d2 = new Vector( shopper.getShopCoordinate(), buyer.getHomeCoordinate() ).length() * ellipsis;
        double d3 = new Vector( buyer.getHomeCoordinate(), shopper.getHomeCoordinate() ).length() * ellipsis;

        return d1 + d2 + d3;
    }

    public static double[] fourPointDistance(User shopper, User buyer, double ellipsis){
        //Path 1
        //Shopper -> Shopper Shop -> Buyer Shop -> Buyer Home -> Home
        //Path 2
        //Shopper -> Buyer Shop -> Buyer Home -> Shopper Shop -> Home
        //Path 3
        //Shopper -> Buyer Shop -> Shopper Shop -> Buyer Home -> Home

        //Path 1
        double p1d1 = new Vector( shopper.getHomeCoordinate(), shopper.getShopCoordinate() ).length() * ellipsis;
        double p1d2 = new Vector( shopper.getShopCoordinate(), buyer.getShopCoordinate() ).length() * ellipsis;
        double p1d3 = new Vector( buyer.getShopCoordinate(), buyer.getHomeCoordinate() ).length() * ellipsis;
        double p1d4 = new Vector( buyer.getHomeCoordinate(), shopper.getHomeCoordinate() ).length() * ellipsis;
        double p1 = p1d1 + p1d2 + p1d3 + p1d4;

        //Path 2
        double p2d1 = new Vector( shopper.getHomeCoordinate(), buyer.getShopCoordinate() ).length() * ellipsis;
        double p2d2 = new Vector( buyer.getShopCoordinate(), buyer.getHomeCoordinate() ).length() * ellipsis;
        double p2d3 = new Vector( buyer.getHomeCoordinate(), shopper.getShopCoordinate() ).length() * ellipsis;
        double p2d4 = new Vector( shopper.getShopCoordinate(), shopper.getHomeCoordinate() ).length() * ellipsis;
        double p2 = p2d1 + p2d2 + p2d3 + p2d4;

        //Path 3
        double p3d1 = new Vector( shopper.getShopCoordinate(), buyer.getShopCoordinate() ).length() * ellipsis;
        double p3d2 = new Vector( buyer.getShopCoordinate(), shopper.getHomeCoordinate() ).length() * ellipsis;
        double p3d3 = new Vector( shopper.getHomeCoordinate(), shopper.getShopCoordinate() ).length() * ellipsis;
        double p3d4 = new Vector( shopper.getShopCoordinate(), buyer.getHomeCoordinate() ).length() * ellipsis;
        double p3 = p3d1 + p3d2 + p3d3 + p3d4;

        return new double[]{p1, p2, p3};
    }
}
