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
        return this.minNumber + grid * cellSize();
    }

    public double gridRightNumber(int grid){
        return this.minNumber + (grid + 1) * cellSize();
    }

    public static void main(String[] args) {
        GridSystem gs = new GridSystem(-180., 180., 1.);
        System.out.println(gs.numberToGrid(200));

        GridSystem gs2 = new GridSystem(-180., 180., 0.1);
        System.out.println(gs2.numberToGrid(200));
    }
}
