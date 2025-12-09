package matchingAlgo.matching;

import matchingAlgo.GS.Coordinate;

public class Vector {
    private double x;
    private double y;

    public Vector(){
        this(0,0);
    }

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector(Coordinate c){
        this.x = c.getLongtitude();
        this.y = c.getLatitude();
    }

    public Vector( Vector v1, Vector v2 ){
        this.x = v2.getX() - v1.getX();
        this.y = v2.getY() - v1.getY();
    }
    
    public Vector(Coordinate c1, Coordinate c2){
        this.x = c2.getLatitude() - c1.getLatitude();
        this.y = c2.getLongtitude() - c1.getLongtitude();
    }

    public Vector( double distance, Vector direction ){
        this.x = distance * direction.getX();
        this.y = distance * direction.getY();
    }
    
    public Vector copy(){
        return new Vector(this.x, this.y);
    }

    public double length(){
        return Math.sqrt( Math.pow(this.x, 2) + Math.pow(this.y, 2) );
    }
    
    public Vector normalize(){
        return this.multiply( 1.0 / this.length() );
    }

    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }

    public Vector add(Vector other){
        return new Vector( this.x + other.x, this.y + other.y );
    }

    public Vector multiply(double factor){
        return new Vector( this.x * factor, this.y * factor );
    }

    public Vector inverse(){
        return new Vector( -this.x, -this.y );
    }

    public Vector rotate90degClockwise(){
        return new Vector( this.y, -this.x );
    }

    public Vector rotate90degCounterClockwise(){
        return new Vector( -this.y, this.x );
    }
    
    public Coordinate add( Coordinate c ){
        return new Coordinate( c.getLatitude() + this.x, c.getLongtitude() + this.y );
    }
    
    public String toString(){
        return "Vector: (" + this.x + ", " + this.y + ")";
    }
}
