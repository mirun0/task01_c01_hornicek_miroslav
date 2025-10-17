package model;

import java.util.ArrayList;

public class Polygon {
    
    private ArrayList<Point> points;

    public Polygon(ArrayList<Point> points) {
        this.points = points;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public int size() {
        return points.size();
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public void clear() {
        points.clear();
    }

}
