package model;

import java.util.ArrayList;

public class Polygon {
    
    private ArrayList<Point> points;
    private int color;

    public Polygon(ArrayList<Point> points, int color) {
        this.points = points;
        this.color = color;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public int getColor() {
        return color;
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
