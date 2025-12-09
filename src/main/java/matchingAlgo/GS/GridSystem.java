package matchingAlgo.GS;


public class GridSystem {
    private int numCells;

    final double minNumber;
    final double maxNumber;

    //Different constructors
    //First one is constructing with a number of cells
    //Second one is constructing with a distance between cells
    public GridSystem(double minNumber, double maxNumber, int numCells){
        this( minNumber, maxNumber );
        this.numCells = numCells;
    }

    public GridSystem(double minNumber, double maxNumber, double distance){
        this( minNumber, maxNumber );
        this.numCells = (int)(( maxNumber - minNumber ) / distance);
    }

    public GridSystem( double minNumber, double maxNumber ){
        this.minNumber = Math.min( minNumber, maxNumber );
        this.maxNumber = Math.max( maxNumber, minNumber );
    }

    public int minNumberToGrid(){
        //Get minimum grid id
        return 0;
    }

    public int maxNumberToGrid(){
        //Get maximum grid id
        return numCells;
    }

    public double cellSize(){
        //Get the size of one cell
        return (this.maxNumber - this.minNumber) / this.numCells;
    }

    public int numberToGrid(double number){
        //Converts a number into a value of the grid
        int value = (int)((number - this.minNumber)/this.cellSize());
        return Math.max( minNumberToGrid(), Math.min( maxNumberToGrid(), value ) );
    }
}
