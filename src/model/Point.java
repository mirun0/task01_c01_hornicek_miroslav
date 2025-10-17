package model;

public class Point {
    
    private int x;
    private int y;
    private int color;

    public Point(int x, int y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public Point(double x, double y, int color) {
        this.x = (int) Math.round(x);
        this.y = (int) Math.round(y);
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getColor() {
        return color;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
