package matchingAlgo.matching;

import matchingAlgo.GS.User;
import matchingAlgo.GS.UserDatabase;



public class ShopperDatabaseSearcher {
    //Finds the best buyer for a given shopper
    private UserDatabase buyerDatabase;

    public ShopperDatabaseSearcher(UserDatabase buyerDatabase ){
        this.buyerDatabase = buyerDatabase;
    }


    public User[] l2DistanceApproximation( int nSamples, User A, int[][] pathGridCells ){
        User[] bestSamples = new User[nSamples];

        double[] minDistance = new double[nSamples];
        for ( int i = 0; i < nSamples; i++ ){
            minDistance[i] = Double.MAX_VALUE;
        }

        int foundElements = 0;
        int sameGrid = 0;
        //Min Distance array is a sorted array storing the minimum distance staring with the highest distance going to the lowest distance
        for ( int[] cell : pathGridCells ){
            int cellX = cell[0];
            int cellY = cell[1];

            for ( User u : this.buyerDatabase.usersOfCell( cellX, cellY ) ){
                double d = GraphSystem.threePointDistance( A, u, 1 );

                sameGrid++;
                if ( d < minDistance[0] ){
                    foundElements++;
                    minDistance[0] = d;
                    bestSamples[0] = u;

                    for ( int j = 0; j < minDistance.length - 1; j++ ){
                        if ( minDistance[j] < minDistance[j + 1] ){
                            double temp = minDistance[j];
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

    public User fourPointApproximation( User A, User[] possibleBuyers ){
        User minUser = null;
        double minDistance = Double.MAX_VALUE;
        for ( User u : possibleBuyers ){
            if ( u != null ){
                double[] paths = GraphSystem.fourPointDistance( A, u, 1 );
                for ( int i = 0; i < paths.length; i++ ){
                    if ( paths[i] < minDistance ){
                        minDistance = paths[i];
                        minUser = u;
                    }
                }
            }

        }
        return minUser;
    }

}
