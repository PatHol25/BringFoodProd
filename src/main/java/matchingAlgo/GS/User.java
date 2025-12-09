package matchingAlgo.GS;

import matchingAlgo.matching.Vector;

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


    public int[][] getPossibleGrids( double approxDistanceWidth, double approxDistanceHeight, GridSystem gsLongtitude, GridSystem gsLatitude, boolean sphere ){
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

        Coordinate home = this.getHomeCoordinate();
        Coordinate shop = this.getShopCoordinate();

        Vector leftPoint, rightPoint;
        if ( home.getLongtitude() < shop.getLongtitude() ){
            leftPoint = new Vector( home );
            rightPoint = new Vector( shop );
        } else {
            leftPoint = new Vector( shop );
            rightPoint = new Vector( home );
        }

        double distanceLR = new Vector( leftPoint, rightPoint ).length();

        //Compute Edge Cases
        if ( sphere ){
            double minDistance = distanceLR;

            Vector shiftedLeftPoint = new Vector( ( gsLongtitude.getMaxNumber() + leftPoint.getX() ) + gsLongtitude.getMaxNumber(), leftPoint.getY() );
            double distanceRL = new Vector( rightPoint, shiftedLeftPoint ).length();


            if ( distanceRL < distanceLR ){
                leftPoint = rightPoint;
                rightPoint = shiftedLeftPoint;
                minDistance = distanceRL;
            }

            Vector northPole = new Vector( leftPoint.getX(), 2 * gsLatitude.getMaxNumber() - leftPoint.getY() );
            double distanceDN = new Vector( northPole, rightPoint ).length();

            if ( distanceDN < minDistance ){
                leftPoint = northPole;
                minDistance = distanceDN;
            }

            Vector southPole = new Vector( leftPoint.getX(), -rightPoint.getY() );
            double distanceSN = new Vector( southPole, rightPoint ).length();

            if ( distanceSN < minDistance ){
                leftPoint = southPole;
                minDistance = distanceSN;
            }
        }

        //Get outher borders of the possible ways
        Vector aWidth = new Vector( leftPoint, rightPoint ).multiply( approxDistanceWidth ).inverse();
        Vector aHeight = new Vector( leftPoint, rightPoint ).multiply( approxDistanceHeight ).rotate90degClockwise();

        Vector p1 = leftPoint.copy();
        p1.add( aWidth );
        p1.add( aHeight );

        Vector p2 = leftPoint.copy();
        p2.add( aWidth.inverse() );
        p2.add( aHeight );

        Vector p3 = rightPoint.copy();
        p3.add( aWidth.inverse() );
        p3.add( aHeight );

        Vector p4 = rightPoint.copy();
        p4.add( aWidth.inverse() );
        p4.add( aHeight.inverse() );


        //Implement with better vector mathematics
        double mostLeftX = Math.min( p1.getX(), Math.min( p2.getX(), Math.min( p3.getX(), p4.getX() ) ) );
        double mostRightX = Math.max( p1.getX(), Math.max( p2.getX(), Math.max( p3.getX(), p4.getX() ) ) );

        double mostBottomY = Math.min( p1.getY(), Math.min( p2.getY(), Math.min( p3.getY(), p4.getY() ) ) );
        double mostTopY = Math.max( p1.getY(), Math.max( p2.getY(), Math.max( p3.getY(), p4.getY() ) ) );


        int[][] gridList = new int[1][];

        int currentGridX = gsLongtitude.numberToGrid( mostLeftX );
        int currentGridY = gsLatitude.numberToGrid( mostBottomY );

        gridList[0] = new int[]{currentGridX, currentGridY};

        //Check if in same Grid then the for loops are useless
        double stepSizeX = gsLongtitude.cellSize() / 2;
        double stepSizeY = gsLatitude.cellSize() / 2;
        if ( gsLongtitude.numberToGrid(mostLeftX) == gsLongtitude.numberToGrid( mostRightX ) ) {
            stepSizeX = gsLongtitude.cellSize();
        }

        if ( gsLatitude.numberToGrid(mostBottomY) == gsLatitude.numberToGrid( mostTopY ) ) {
            stepSizeY = gsLatitude.cellSize();
        }

        for ( double x = mostLeftX; x <= mostRightX; x += stepSizeX ){
            for ( double y = mostBottomY; y <= mostTopY; y += stepSizeY ){

                int gridX = gsLongtitude.numberToGrid( x );
                int gridY = gsLatitude.numberToGrid( y );

                if ( currentGridX != gridX || currentGridY != gridY ){
                    currentGridX = gridX;
                    currentGridY = gridY;

                    int[][] newGridList = new int[gridList.length + 1][];
                    System.arraycopy(gridList, 0, newGridList, 0, gridList.length);


                    newGridList[newGridList.length - 1] = new int[]{currentGridX, currentGridY};
                    gridList = newGridList;
                }
            }
        }
        return gridList;
    }
}
