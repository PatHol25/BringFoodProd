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

    public double getMinNumber(){
        return this.minNumber;
    }

    public double getMaxNumber(){
        return this.maxNumber;
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
        int sub = ((int)( number - this.minNumber )) / ((int)( this.maxNumber - this.minNumber ));
        double v = number - sub * ( this.maxNumber - this.minNumber );
        return (int)(( v - this.minNumber) / cellSize());
    }
}
