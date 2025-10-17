package controller;

import java.awt.event.*;
import java.util.ArrayList;

import model.Line;
import model.Point;
import model.Polygon;
import utils.RandomColor;

public class MouseHandler extends MouseAdapter {

    private final Controller2D c;

    public MouseHandler(Controller2D c) {
        this.c = c;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (c.lineCreation) {
                handleLineCreation(e);
            } else if (c.polygonCreation) {
                handlePolygonCreation(e);
            } else if (c.newPointCreation) {
                c.movingPoint = null;
                c.createNewPoint();
            } else if(c.pointDeletion) {
                c.movingPoint = null;
                c.deletePoint();
            } else if(c.pointMoving) {
                Point find = c.findPoint(e.getX(), e.getY());
                c.movingPoint = find;
            }
        }

        if (e.getButton() == MouseEvent.BUTTON3) {
            handleRightClick();
        }

        c.render();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && c.lineCreation) {
            if (c.activeLine != null &&
                (c.activeLine.getPointA().getX() != c.activeLine.getPointB().getX() ||
                 c.activeLine.getPointA().getY() != c.activeLine.getPointB().getY())) {
                c.lines.add(c.activeLine);
                c.activeLine = null;
            }
        }

        c.render();
    }

    private void handleLineCreation(MouseEvent e) {
        if (c.activeLine == null) {
            c.activeLine = new Line(
                new Point(e.getX(), e.getY(), c.color),
                new Point(e.getX(), e.getY(), c.color)
            );
            if (c.gradientCreation) {
                c.activeLine.getPointA().setColor(RandomColor.create());
                c.activeLine.getPointB().setColor(RandomColor.create());
            }
        }
    }

    private void handlePolygonCreation(MouseEvent e) {
        if (c.activePolygon == null) {
            ArrayList<Point> points = new ArrayList<>();
            points.add(new Point(e.getX(), e.getY(), c.color));
            Point b = new Point(e.getX(), e.getY(), c.color);
            if (c.gradientCreation) {
                b.setColor(c.gradientColor);
            }
            points.add(b);
            c.activePolygon = new Polygon(points);
        } else {
            Point p = new Point(c.snappedX, c.snappedY, c.color);
            if (c.gradientCreation) {
                p.setColor(RandomColor.create());
            }
            c.activePolygon.addPoint(p);
        }
    }

    private void handleRightClick() {
        if (c.lineCreation) {
            c.activeLine = null;
        }

        if (c.polygonCreation) {
            if (c.activePolygon != null && c.activePolygon.getPoints().size() > 3) {
                c.activePolygon.getPoints().remove(c.activePolygon.size() - 1);
                c.polygons.add(c.activePolygon);
            }
            c.activePolygon = null;
        }
    }
}
