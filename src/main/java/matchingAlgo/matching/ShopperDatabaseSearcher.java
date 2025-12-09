package matchingAlgo.matching;

import matchingAlgo.GS.User;
import matchingAlgo.GS.UserDatabase;

class DistanceStruct{
    final double distance;
    final User user;

    public DistanceStruct( double distance, User user ){
        this.distance = distance;
        this.user = user;
    }
}

public class ShopperDatabaseSearcher {
    //Finds the best buyer for a given shopper
    private UserDatabase buyerDatabase;

    public ShopperDatabaseSearcher(UserDatabase buyerDatabase ){
        this.buyerDatabase = buyerDatabase;
    }

    private void sortDistanceList( DistanceStruct[] distanceList ){
        for ( int i = 0; i < distanceList.length - 1; i++ ){
            for ( int j = i + 1; j < distanceList.length; j++ ){
                if ( distanceList[i].distance > distanceList[j].distance ){
                    DistanceStruct tmp = distanceList[i];
                    distanceList[i] = distanceList[j];
                    distanceList[j] = tmp;
                }
            }
        }
    }

    public User[] l2DistanceApproximation( int nSamples, User A, int[][] pathGridCells ){
        DistanceStruct[] distanceList = new DistanceStruct[0];
        for ( int[] cell : pathGridCells ){
            int cellX = cell[0];
            int cellY = cell[1];

            for ( User u : this.buyerDatabase.usersOfCell( cellX, cellY ) ){
                double d = GraphSystem.threePointDistance( A, u, 1 );
                DistanceStruct ds = new DistanceStruct( d, u );

                DistanceStruct[] newDistanceList = new DistanceStruct[distanceList.length + 1];
                System.arraycopy(distanceList, 0, newDistanceList, 0, distanceList.length);
                newDistanceList[newDistanceList.length - 1] = ds;
                distanceList = newDistanceList;
            }
        }

        this.sortDistanceList( distanceList );

        User[] bestSamples = new User[Math.min( nSamples, distanceList.length)];
        for( int i = 0; i < Math.min( nSamples, distanceList.length); i++){
            bestSamples[i] = distanceList[i].user;
        }
        return bestSamples;
    }

    public User fourPointApproximation( User A, User[] possibleBuyers ){
        User minUser = null;
        double minDistance = Double.MAX_VALUE;
        for ( User u : possibleBuyers ){
            double[] paths = GraphSystem.fourPointDistance( A, u, 1 );
            for ( int i = 0; i < paths.length; i++ ){
                if ( paths[i] < minDistance ){
                    minDistance = paths[i];
                    minUser = u;
                }
            }
        }
        return minUser;
    }

}
