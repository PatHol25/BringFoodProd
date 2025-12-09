package com.example.demo;


public class GridSystem {
    private int numCells;

    final double minNumber;
    final double maxNumber;

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
        return 0;
    }

    public int maxNumberToGrid(){
        return numCells;
    }

    public double cellSize(){
        return (this.maxNumber - this.minNumber) / this.numCells;
    }

    public int numberToGrid(double number){
        return (int)((number - this.minNumber)/this.cellSize());
    }
}
