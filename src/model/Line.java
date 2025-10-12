package model;

public class Line {
    
    private Point a;
    private Point b;
    private int color;
    private int colorGradient; // 2nd color for gradient


    public Line(int x1, int x2, int y1, int y2, int color) {
        this.a = new Point(x1, y1);
        this.b = new Point(x2, y2);
        this.color = color;
        this.colorGradient = -1;
    }

    public Line(Point a, Point b, int color) {
        this.a = a;
        this.b = b;
        this.color = color;
        this.colorGradient = -1;
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

    public int getColor() {
        return color;
    }

    public int getGradient() {
        return colorGradient;
    }

    public void setGradient(int color1, int color2) {
        this.color = color1;
        this.colorGradient = color2;
    }

    public void setColor(int color) {
        this.color = color;
        this.colorGradient = -1;
    }

}
