package model;

public class Line {
    
    private Point a;
    private Point b;

    public Line(int x1, int x2, int y1, int y2, int c1, int c2) {
        this.a = new Point(x1, y1, c1);
        this.b = new Point(x2, y2, c2);
    }

    public Line(Point a, Point b) {
        this.a = a;
        this.b = b;
    }

    public void setB(Point b) {
        this.b = b;
    }

    public Point getPointA() {
        return a;
    }
    
    public Point getPointB() {
        return b;
    }

}
