package matchingAlgo.Database;
import matchingAlgo.GS.GridSystem;
import matchingAlgo.GS.User;



public class UserDatabase implements Runnable {
    public String name;
    public Cell[][] cells;

    private final Thread collectorThread;
    private volatile boolean running = true;

    public UserDatabase(String databaseName, GridSystem lontitude, GridSystem latitude ) {
        name = databaseName;
        cells = new Cell[lontitude.maxNumberToGrid()][latitude.maxNumberToGrid()];

        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][j] = new Cell( i * cells[i].length + j );
            }
        }

        collectorThread = new Thread(this, "UserDatabase-Collector");
        collectorThread.start(); // starts inside constructor
    }

    public void addUser( User u ) {
        int lonId = u.getHomeCoordinate().getLongtitudeId();
        int latId = u.getHomeCoordinate().getLatitudeId();

        cells[ lonId ][ latId ].addUser( u );
    }

    public User[] usersOfCell(int lonId, int latId) {
        return cells[ lonId ][ latId ].getUsersOfCell();
    }

    @Override
    public void run() {
        collector();
    }

    private void collector() {
        while (running) {
            for (Cell[] row : cells) {
                for (Cell c : row) {
                    if (c != null) {
                        c.collector();
                    }
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }
}
