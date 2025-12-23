package matchingAlgo.Database;
import matchingAlgo.GS.User;

public class Cell{
    private User[] users;

    private int currentWritingIdx;
    private int chunkWritingSize;

    private boolean changing;

    private boolean working;

    private int id;

    public Cell( int id ){
        users = new User[1];
        chunkWritingSize = 5000;

        changing = false;
        working = false;

        this.id = id;
    }

    public User[] getUsersOfCell(){
        int currentLength = currentWritingIdx - 1;
        User[] output = new User[currentLength];
        System.arraycopy( users, 0, output, 0, currentLength );
        return output;
    }

    public void addUser(User u){
        while ( changing || working || currentWritingIdx >= users.length ){

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // restore interrupt flag
            }

            //System.out.println("Changing: " + changing + " Working: " + working + " currentWritingIdx: " + currentWritingIdx + " users.length: " + users.length);
        }
        working = true;
        users[currentWritingIdx] = u;
        currentWritingIdx++;
        working = false;
    }

    public void collector(){
        //Remove deleted users
        for ( int i = 0; i < currentWritingIdx; i++ ){
            if ( users[i] != null ){
                if ( users[i].delete() ){
                    working = true;
                    users[i] = users[currentWritingIdx - 1];
                    currentWritingIdx--;
                    working = false;
                    //System.out.println("Cell " + this.id + " deleted user " + users[i].getId());
                }
            }
        }

        //Increase the size of the Database
        if ( currentWritingIdx >= users.length * 0.9 ){
            int newSize = (int)(users.length * 1.2) + 1;
            newSize = Math.max( newSize, 1 );

            User[] newDatabase = new User[newSize];

            int currentProcessingIdx;
            for ( currentProcessingIdx = 0; currentProcessingIdx < (int)(currentWritingIdx / chunkWritingSize); currentProcessingIdx++ ){
                System.arraycopy( users, currentProcessingIdx * chunkWritingSize, newDatabase, currentProcessingIdx * chunkWritingSize, chunkWritingSize );
            }

            if ( currentProcessingIdx * chunkWritingSize < newDatabase.length ){
                if ( currentProcessingIdx * currentWritingIdx < users.length ){
                    if ( users.length - currentProcessingIdx * chunkWritingSize > 0 ){
                        System.arraycopy( users, currentProcessingIdx * chunkWritingSize, newDatabase, currentProcessingIdx * chunkWritingSize, users.length - currentProcessingIdx * chunkWritingSize );
                    }
                }
            }

            changing = true;
            users = newDatabase;
            changing = false;
            //System.out.println("Increasing... Cell " + this.id + " resized to " + users.length);
        }

        //Reduce the size of the Database
        else if ( currentWritingIdx < (int)(users.length * 0.7) && currentWritingIdx != 0 ){
            int newSize = (int)(users.length * 0.7);
            newSize = Math.max( newSize, 1 );

            User[] newDatabase = new User[newSize];
            int currentProcessingIdx;
            for ( currentProcessingIdx = 0; currentProcessingIdx < (int)(currentWritingIdx / chunkWritingSize); currentProcessingIdx++ ){
                System.arraycopy( users, currentProcessingIdx * chunkWritingSize, newDatabase, currentProcessingIdx * chunkWritingSize, chunkWritingSize );
            }

            if ( currentProcessingIdx * chunkWritingSize < newDatabase.length ){
                if ( currentProcessingIdx * currentWritingIdx < users.length ){
                    if ( users.length - currentProcessingIdx * chunkWritingSize > 0 ){
                        System.arraycopy( users, currentProcessingIdx * chunkWritingSize, newDatabase, currentProcessingIdx * chunkWritingSize, users.length - currentProcessingIdx * chunkWritingSize );
                    }
                }
            }

            changing = true;
            users = newDatabase;
            changing = false;
            //System.out.println("Decreasing..Cell " + this.id + " resized to " + users.length + " currentWritingIdx: " + currentWritingIdx + " Size: " + (int)(users.length * 0.7));
        }
    }
}