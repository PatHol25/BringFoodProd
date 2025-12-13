package matchingAlgo.matching;

import matchingAlgo.GS.Coordinate;
import matchingAlgo.GS.GridSystem;
import matchingAlgo.GS.User;

public class GraphSystem {
    public static double[] minGraphDistance(User shopper, User buyer, double ellipsis, GridSystem gsLongtitude, GridSystem gsLatitude){
        //Path 1
        //Shopper -> Shopper Shop -> Buyer Shop -> Buyer Home -> Home
        //Path 2
        //Shopper -> Buyer Shop -> Buyer Home -> Shopper Shop -> Home
        //Path 3
        //Shopper -> Buyer Shop -> Shopper Shop -> Buyer Home -> Home

        //Path 1
        double p1d1 = MinVector.sphereDistance( shopper.getHomeCoordinate(), shopper.getShopCoordinate(), gsLongtitude, gsLatitude ).distance * ellipsis;
        double p1d2 = MinVector.sphereDistance( shopper.getShopCoordinate(), buyer.getShopCoordinate(), gsLongtitude, gsLatitude ).distance * ellipsis;
        double p1d3 = MinVector.sphereDistance( buyer.getShopCoordinate(), buyer.getHomeCoordinate(), gsLongtitude, gsLatitude ).distance * ellipsis;
        double p1d4 = MinVector.sphereDistance( buyer.getHomeCoordinate(), shopper.getEndCoordinate(), gsLongtitude, gsLatitude ).distance * ellipsis;
        double p1 = p1d1 + p1d2 + p1d3 + p1d4;


        //Path 2
        double p2d1 = MinVector.sphereDistance( shopper.getHomeCoordinate(), buyer.getShopCoordinate(), gsLongtitude, gsLatitude ).distance * ellipsis;
        double p2d2 = MinVector.sphereDistance( buyer.getShopCoordinate(), buyer.getHomeCoordinate(), gsLongtitude, gsLatitude ).distance * ellipsis;
        double p2d3 = MinVector.sphereDistance( buyer.getHomeCoordinate(), shopper.getShopCoordinate(), gsLongtitude, gsLatitude ).distance * ellipsis;
        double p2d4 = MinVector.sphereDistance( shopper.getShopCoordinate(), shopper.getEndCoordinate(), gsLongtitude, gsLatitude ).distance * ellipsis;
        double p2 = p2d1 + p2d2 + p2d3 + p2d4;


        //Path 3
        double p3d1 = MinVector.sphereDistance( shopper.getHomeCoordinate(), buyer.getShopCoordinate(), gsLongtitude, gsLatitude ).distance * ellipsis;
        double p3d2 = MinVector.sphereDistance( buyer.getShopCoordinate(), shopper.getShopCoordinate(), gsLongtitude, gsLatitude ).distance * ellipsis;
        double p3d3 = MinVector.sphereDistance( shopper.getShopCoordinate(), buyer.getHomeCoordinate(), gsLongtitude, gsLatitude ).distance * ellipsis;
        double p3d4 = MinVector.sphereDistance( buyer.getHomeCoordinate(), shopper.getEndCoordinate(), gsLongtitude, gsLatitude ).distance * ellipsis;

        double p3 = p3d1 + p3d2 + p3d3 + p3d4;

        return new double[]{p1, p2, p3};
    }
}
