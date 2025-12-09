package matchingAlgo.GS;

public class UserDatabase {
    private User[][][] userDatabase;

    public UserDatabase(int numLongTitudeCells, int numLatTitudeCells) {
        this.userDatabase = new User[numLongTitudeCells][][];
        for (int i = 0; i < numLongTitudeCells; i++) {
            this.userDatabase[i] = new User[numLatTitudeCells][];

            for ( int j = 0; j < numLatTitudeCells; j++ ) {
                this.userDatabase[i][j] = new User[0];
            }
        }
    }

    public void addUser(User b){
        int longtitudeId = b.getHomeCoordinate().getLongtitudeId();
        int latitudeId = b.getHomeCoordinate().getLatitudeId();

        if ( longtitudeId >= 0 && latitudeId >= 0 ){
            User[] buyers = userDatabase[longtitudeId][latitudeId];
            User[] newBuyers = new User[buyers.length + 1];

            System.arraycopy(buyers, 0, newBuyers, 0, buyers.length);
            newBuyers[buyers.length] = b;
            this.userDatabase[longtitudeId][latitudeId] = newBuyers;
        }
    }

    public User[] usersOfCell( int longtitudeId, int latitudeId ){
        return userDatabase[longtitudeId][latitudeId];
    }

    public void removeUser(User b){
        int longtitudeId = b.getHomeCoordinate().getLongtitudeId();
        int latitudeId = b.getHomeCoordinate().getLatitudeId();

        if ( longtitudeId >= 0 && latitudeId >= 0 ){
            User[] buyers = userDatabase[longtitudeId][latitudeId];
            User[] newBuyers = new User[buyers.length - 1];

            int i = 0;
            int j = 0;
            while ( i < newBuyers.length ){
                if ( !buyers[i].getId().equals(b.getId()) ){
                    newBuyers[i] = newBuyers[j];
                    i++;
                }
                j++;
            }

            this.userDatabase[longtitudeId][latitudeId] = newBuyers;
        }
    }

    public void printDatabase(){
        int gesUsers = 0;
        for (int i = 0; i < userDatabase.length; i++) {
            for (int j = 0; j < userDatabase[i].length; j++) {
                if ( userDatabase[i][j].length > 0 ){
                    gesUsers += userDatabase[i][j].length;
                    System.out.println("Cell (" + i + ", " + j + ") has " + userDatabase[i][j].length + " users.");
                }
            }
        }
        System.out.println("Total number of users: " + gesUsers);
    }
}
