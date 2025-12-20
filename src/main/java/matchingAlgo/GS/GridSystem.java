package matchingAlgo.GS;

/*
To handle multiple Users faster the world as a glob is divided into a Grid of cells where each cell represents the Users with their home address.
The division into the Grid happends through the splitting of the Longtitude and Latitude.
To handle world coordinates more easily the Longtitude is reshaped from West to East from 0 representing W180 to 360 representing E180.
The Latitude is reshaped where N90 represents 0 and S90 represents 180 degrees.
This system does split the world into a not exactly rectangular Gridsystem.
Yet it simplifies the process of faster User handling, and the rectangular error can be left behind.

The class can be constructed in two different ways.
First way is with the number of Grid cells between its minimum and maximum value.
Another way is with the length of one cell.

When converting a number into a grid cell residual class calculation is used.
To show that in an example:
Gridsystem
    - Maxvalue = 10, Minvalue = 0, NumCells = 9
    - Number 11 to the gridSystem is like number 1 to the gridSystem
 */

public class GridSystem {

    private int numCells;

    final double minNumber;
    final double maxNumber;

    //Different constructor
    public GridSystem(double minNumber, double maxNumber, int numCells){
        this( minNumber, maxNumber );
        this.numCells = numCells;
    }

    public GridSystem(double minNumber, double maxNumber, double distance){
        this( minNumber, maxNumber );
        this.numCells = (int)(( maxNumber - minNumber ) / distance);
    }

    private GridSystem( double minNumber, double maxNumber ){
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
        double origin = ( number - this.minNumber );
        double maxValue = ( this.maxNumber - this.minNumber );

        int inside = (int)(origin / maxValue);
        double v = origin - inside * maxValue;

        if ( v < 0 ){
            return (int)(( v + maxValue ) / cellSize());
        } else {
            return (int)(v / cellSize());
        }
    }

    public double gridLeftNumber(int grid){
        //Returns the left side number of one grid cell
        return this.minNumber + grid * cellSize();
    }

    public double gridRightNumber(int grid){
        //Returns the right side number of one grid cell
        return this.minNumber + (grid + 1) * cellSize();
    }

    public static void main(String[] args) {
        GridSystem gs = new GridSystem(-180., 180., 1.);
        System.out.println(gs.numberToGrid(200));

        GridSystem gs2 = new GridSystem(-180., 180., 0.1);
        System.out.println(gs2.numberToGrid(200));
    }
}
