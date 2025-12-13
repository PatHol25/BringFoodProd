package matchingAlgo.matching;

import matchingAlgo.GS.Coordinate;
import matchingAlgo.GS.GridSystem;

public class MinVector {
    public Vector v1;
    public Vector v2;
    public double distance;

    public static MinVector sphereDistance(Coordinate startPoint, Coordinate endPoint, GridSystem gsLongtitude, GridSystem gsLatitude) {
        Vector leftPoint, rightPoint;
        if (startPoint.getLongtitude() < endPoint.getLongtitude()) {
            leftPoint = new Vector(startPoint);
            rightPoint = new Vector(endPoint);
        } else {
            leftPoint = new Vector(endPoint);
            rightPoint = new Vector(startPoint);
        }

        double minDistance = new Vector(leftPoint, rightPoint).length();

        Vector shiftedLeftPoint = new Vector((gsLongtitude.getMaxNumber() + leftPoint.getX()) + gsLongtitude.getMaxNumber(), leftPoint.getY());
        double distanceRL = new Vector(rightPoint, shiftedLeftPoint).length();

        if (distanceRL < minDistance) {
            leftPoint = rightPoint;
            rightPoint = shiftedLeftPoint;
            minDistance = distanceRL;
        }

        Vector northPole = new Vector(leftPoint.getX(), 2 * gsLatitude.getMaxNumber() - leftPoint.getY());
        double distanceDN = new Vector(northPole, rightPoint).length();

        if (distanceDN < minDistance) {
            leftPoint = northPole;
            minDistance = distanceDN;
        }

        Vector southPole = new Vector(leftPoint.getX(), -rightPoint.getY());
        double distanceSN = new Vector(southPole, rightPoint).length();

        if (distanceSN < minDistance) {
            leftPoint = southPole;
            minDistance = distanceSN;
        }

        MinVector MinVector = new MinVector();
        MinVector.v1 = leftPoint;
        MinVector.v2 = rightPoint;
        MinVector.distance = minDistance;
        return MinVector;
    }
}
