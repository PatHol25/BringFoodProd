package matchingAlgo.matching;

import matchingAlgo.GS.GridSystem;
import matchingAlgo.GS.User;
import matchingAlgo.GS.UserDatabase;



public class ShopperDatabaseSearcher {
    //Finds the best buyer for a given shopper
    private final UserDatabase buyerDatabase;
    private final GridSystem gsLongtitude;
    private final GridSystem gsLatitude;

    public ShopperDatabaseSearcher(UserDatabase buyerDatabase, GridSystem gsLongtitude, GridSystem gsLatitude ){
        this.buyerDatabase = buyerDatabase;
        this.gsLongtitude = gsLongtitude;
        this.gsLatitude = gsLatitude;
    }

    public User[] minDistanceUsers( int nSamples, User A, int[][] pathGridCells, boolean shopper ){
        User[] bestSamples = new User[nSamples];

        double[][] minDistance = new double[nSamples][2];
        for ( int i = 0; i < nSamples; i++ ){
            minDistance[i][0] = Double.MAX_VALUE;
            minDistance[i][1] = 0;
        }

        int foundElements = 0;
        int sameGrid = 0;
        //Min Distance array is a sorted array storing the minimum distance staring with the highest distance going to the lowest distance
        for ( int[] cell : pathGridCells ){
            int cellX = cell[0];
            int cellY = cell[1];

            for ( User u : this.buyerDatabase.usersOfCell( cellX, cellY ) ){
                double[] returnType;
                double d;
                double pathID;
                if (shopper){
                    returnType = GraphSystem.minGraphDistance( A, u,1, this.gsLongtitude, this.gsLatitude );
                }
                else{
                    returnType = GraphSystem.minGraphDistance( u, A, 1,  this.gsLongtitude, this.gsLatitude );
                }

                d = returnType[0];
                pathID = 0;
                for ( int i = 0; i < returnType.length; i++ ){
                    if ( returnType[i] < d ) {
                        pathID = i;
                        d = returnType[i];
                    };

                }

                sameGrid++;
                if ( d < minDistance[0][0] ){
                    foundElements++;

                    minDistance[0][0] = d;
                    minDistance[0][1] = pathID;

                    bestSamples[0] = u;

                    for ( int j = 0; j < minDistance.length - 1; j++ ){
                        if ( minDistance[j][0] < minDistance[j + 1][0] ){

                            double[] temp = minDistance[j];
                            minDistance[j] = minDistance[j + 1];
                            minDistance[ j + 1 ] = temp;

                            User tempUser = bestSamples[j];
                            bestSamples[j] = bestSamples[j + 1];
                            bestSamples[j + 1] = tempUser;
                        }
                    }
                }
            }
        }
        System.out.println( "Found " + foundElements + " possible buyers." );
        System.out.println( "Same Grid: " + sameGrid );
        return bestSamples;
    }
}
