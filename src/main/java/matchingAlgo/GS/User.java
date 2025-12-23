package matchingAlgo.GS;

import matchingAlgo.matching.MinVector;
import matchingAlgo.matching.Vector;


public class User {
    private String grid_id;
    private Coordinate homeCoordinate;
    private Coordinate shopCoordinate;
    private Coordinate endCoordinate;
    private String timeStart;
    private String timeEnd;
    private boolean sameShop;
    private double radius;
    private boolean reserved;
    private String reservedBy;

    private UserCore core;

    public User( UserCore core, Coordinate homeCoordinate, Coordinate shopCoordinate, Coordinate endCoordinate, String timeStart, String timeEnd, boolean sameShop, double radius ){
        this.core = core;

        this.grid_id = homeCoordinate.getLongtitudeId() + "_" + homeCoordinate.getLatitudeId();

        this.homeCoordinate = homeCoordinate;
        this.shopCoordinate = shopCoordinate;
        this.endCoordinate = endCoordinate;

        this.timeStart = timeStart;
        this.timeEnd = timeEnd;

        this.sameShop = sameShop;
        this.radius = radius;
    }

    public boolean setReserved(String reservedBy){
        if ( !this.core.reserved ){
            this.core.reserved = true;
            this.reservedBy = reservedBy;
            return true;
        }
        return false;
    }

    public boolean setUnreserved( String reservedBy ){
        if ( this.core.reserved && this.core.reservedBy.equals( reservedBy) ){
            this.core.reserved = false;
            return true;
        }
        return false;
    }

    public boolean delete(){
        return this.core.delete;
    }

    public boolean isReserved(){
        return this.core.reserved;
    }

    public boolean sameShop(){
        return this.sameShop;
    }

    public Coordinate getShopCoordinate(){
        return this.shopCoordinate;
    }

    public Coordinate getHomeCoordinate(){
        return this.homeCoordinate;
    }

    public Coordinate getEndCoordinate(){
        return this.endCoordinate;
    }

    public String getGrid_id(){
        return this.grid_id;
    }

    public String getId(){
        return this.core.id;
    }

    public String toString(){
        return "User:\n" +
                "  ID: " + this.core.id + "\n"+
                "  Home: \n  " + this.homeCoordinate.toString() +
                "  Shop: \n  " + this.shopCoordinate.toString() +
                "  End: \n  " + this.endCoordinate.toString();
    }

    public static int[][] mergeGrids( int[][] grid1, int[][] grid2 ){
        int[][] mergedGrid = new int[0][];
        for ( int i = 0; i < grid1.length; i++ ){
            int gridX = grid1[i][0];
            int gridY = grid1[i][1];

            boolean found = false;
            for ( int j = 0; j < mergedGrid.length; j++ ){
                if ( mergedGrid[j][0] == gridX && mergedGrid[j][1] == gridY ){
                    found = true;
                }
            }

            if ( !found ){
                int[][] newGrid = new int[mergedGrid.length + 1][];
                System.arraycopy(mergedGrid, 0, newGrid, 0, mergedGrid.length);
                newGrid[newGrid.length - 1] = grid1[i];
                mergedGrid = newGrid;
            }
        }

        for ( int i = 0; i < grid2.length; i++ ){
            int gridX = grid2[i][0];
            int gridY = grid2[i][1];

            boolean found = false;
            for ( int j = 0; j < mergedGrid.length; j++ ){
                if ( mergedGrid[j][0] == gridX && mergedGrid[j][1] == gridY ){
                    found = true;
                }
            }

            if ( !found ){
                int[][] newGrid = new int[mergedGrid.length + 1][];
                System.arraycopy(mergedGrid, 0, newGrid, 0, mergedGrid.length);
                newGrid[newGrid.length - 1] = grid2[i];
                mergedGrid = newGrid;
            }
        }
        return mergedGrid;
    }

    public static int[][] getPossibleGrids( Coordinate startPoint, Coordinate endPoint, double approxDistanceWidth, double approxDistanceHeight, GridSystem gsLongtitude, GridSystem gsLatitude, boolean sphere ){
        //Get a list of users which are close enough to the user's shopping route
        //The approx distance is a factor which is relative to the distance between the user's home and shop coordinate
        //Is the approx distance 0 then the direct distance between the two coordinates is used

        //  p2--------------------------------p3
        //  |              Height              |
        //  |                                  |
        //  |<-AWidth, H ========== S, AWidth  |
        //  |                 |                |
        //  |              Height              |
        //  p1--------------------------------p4

        approxDistanceHeight = Math.max( approxDistanceHeight, 0.0 );
        approxDistanceWidth = Math.max( approxDistanceWidth, 0.0 );

        MinVector minVec = MinVector.sphereDistance( startPoint, endPoint, gsLongtitude, gsLatitude );

        Vector leftPoint = minVec.v1;
        Vector rightPoint = minVec.v2;

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

        int p1gridX = gsLongtitude.numberToGrid( p1.getX() );
        int p1gridY = gsLatitude.numberToGrid( p1.getY() );

        int p2gridX = gsLongtitude.numberToGrid( p2.getX() );
        int p2gridY = gsLatitude.numberToGrid( p2.getY() );

        int p3gridX = gsLongtitude.numberToGrid( p3.getX() );
        int p3gridY = gsLatitude.numberToGrid( p3.getY() );

        int p4gridX = gsLongtitude.numberToGrid( p4.getX() );
        int p4gridY = gsLatitude.numberToGrid( p4.getY() );

        //Check if all points are in the same Grid
        if ( ( p1gridX == p2gridX) && ( p1gridX == p3gridX ) && ( p1gridX == p4gridX ) ){
            if ( ( p1gridY == p2gridY) && ( p1gridY == p3gridY ) && ( p1gridY == p4gridY ) ){
                return new int[][]{{p1gridX, p1gridY}};
            }
        }


        //If not get all grids what the rectangle covers
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

                    //Better check for every Grid if the Grid is in the Rectangle
                    if ( isInRectangle( p1, p2, p3, p4, currentGridX, currentGridY, gsLongtitude, gsLatitude )){
                        int[][] newGridList = new int[gridList.length + 1][];
                        System.arraycopy(gridList, 0, newGridList, 0, gridList.length);

                        newGridList[newGridList.length - 1] = new int[]{currentGridX, currentGridY};
                        gridList = newGridList;
                    }
                }
            }
        }
        return gridList;
    }

    private static boolean isInRectangle(Vector p1, Vector p2, Vector p3, Vector p4, int gridX, int gridY, GridSystem gsLongtitude, GridSystem gsLatitude){
        //Get all four outher Points of the Grid
        Vector leftGridPoint = new Vector( gsLongtitude.gridLeftNumber(gridX), gsLatitude.gridLeftNumber(gridY) );
        Vector rightGridPoint = new Vector( gsLongtitude.gridRightNumber(gridX), gsLatitude.gridRightNumber(gridY) );
        Vector bottomGridPoint = new Vector( gsLongtitude.gridLeftNumber(gridX), gsLatitude.gridRightNumber(gridY) );
        Vector topGridPoint = new Vector( gsLongtitude.gridRightNumber(gridX), gsLatitude.gridLeftNumber(gridY) );

        Vector[] points = new Vector[]{leftGridPoint, rightGridPoint, bottomGridPoint, topGridPoint};
        for ( Vector point : points ){
            if ( pointInRectangle( p1, p2, p4, point ) ) { return true; }
        }
        return false;
    }

    private static boolean pointInRectangle(Vector p1, Vector p2, Vector p4, Vector point){
        Vector u = new Vector( p1, p4 );
        Vector v = new Vector( p1, p2 );

        Vector A = p1.copy();

        Vector E = point.copy();

        double t2 = ( A.x * u.x + E.x * u.y - A.x * u.y - E.y * u.x ) / ( v.y * u.x - v.x * u.y );
        double t1 = ( E.x - A.x + t2 * v.x ) / u.x;

        if ( t1 > 0 && t1 < 1 ){
            double t22 = ( A.y * v.x + v.y * E.x - A.x * v.y - E.y * v.x ) / ( v.y * u.x - v.x * u.y );
            double t12 = ( E.x + t22 * u.x - A.x ) / v.x;

            return t12 > 0 && t12 < 1;
        }
        return false;
    }
}
