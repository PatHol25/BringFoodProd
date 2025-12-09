package matchingAlgo.GS;

class Cache{
    private int size;
    private int capacity;
    private User[] users;

    public Cache(int capacity){
        this.capacity = capacity;
        this.users = new User[capacity];
        this.size = 0;
    }

    public User[] getUsers(){
        return users;
    }

    public boolean addUser(User user){
        if ( size >= capacity ) return false;
        users[size++] = user;
        return true;
    }

    public boolean removeUser(User user){
        for ( int i = 0; i < size; i++ ){
            if ( users[i].getId().equals(user.getId()) ){
                users[i] = users[size - 1];
                size--;
                return true;
            }
        }
        return false;
    }

    public int getSize(){
        return size;
    }
}

public class UserDatabase {
    private Cache[][][] userDatabase;

    public UserDatabase(int numLongTitudeCells, int numLatTitudeCells) {
        this.userDatabase = new Cache[numLongTitudeCells][][];
        for (int i = 0; i < numLongTitudeCells; i++) {
            this.userDatabase[i] = new Cache[numLatTitudeCells][];
            for ( int j = 0; j < numLatTitudeCells; j++ ) {
                this.userDatabase[i][j] = new Cache[0];
            }
        }
    }

    public void addUser(User b){
        int longtitudeId = b.getHomeCoordinate().getLongtitudeId();
        int latitudeId = b.getHomeCoordinate().getLatitudeId();

        if ( longtitudeId >= 0 && latitudeId >= 0 ){
            int numCaches = this.userDatabase[longtitudeId][latitudeId].length;
            for ( int i = 0; i < numCaches; i++ ){
                if ( this.userDatabase[longtitudeId][latitudeId][i].addUser(b) ) return;
            }

            Cache[] newCache = new Cache[numCaches + 1];
            for ( int i = 0; i < numCaches; i++ ){
                newCache[i] = this.userDatabase[longtitudeId][latitudeId][i];
            }
            Cache newC = new Cache(100);
            newC.addUser(b);

            newCache[newCache.length - 1] = newC;
            this.userDatabase[longtitudeId][latitudeId] = newCache;
        }
    }

    public User[] usersOfCell( int longtitudeId, int latitudeId ){
        Cache[] cells = this.userDatabase[longtitudeId][latitudeId];

        int numOutputLength = 0;
        for ( int i = 0; i < cells.length; i++ ) {
            numOutputLength += cells[i].getSize();
        }

        User[] output = new User[numOutputLength];
        int currentIndex = 0;
        for ( int j = 0; j < cells.length; j++ ) {
            System.arraycopy( cells[ j ].getUsers(), 0, output, currentIndex, cells[ j ].getSize()  );
            currentIndex += cells[ j ].getSize();
        }

        return output;
    }

    public void removeUser(User b){
        int longtitudeId = b.getHomeCoordinate().getLongtitudeId();
        int latitudeId = b.getHomeCoordinate().getLatitudeId();

        if ( longtitudeId >= 0 && latitudeId >= 0 ){
            Cache[] cells = this.userDatabase[longtitudeId][latitudeId];
            for ( int i = 0; i < cells.length; i++ ) {
                cells[i].removeUser(b);

                if (cells[i].getSize() == 0) {
                    Cache[] newCells = new Cache[cells.length - 1];

                    int j = 0;
                    for (int i1 = 0; i1 < cells.length; i1++) {
                        if (cells[i1].getSize() > 0) {
                            newCells[j] = cells[i1];
                            j++;
                        }
                    }
                    this.userDatabase[longtitudeId][latitudeId] = newCells;
                }
            }
        }
    }
}
