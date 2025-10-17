package controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import model.Line;
import model.Point;
import model.Polygon;

public class Renderer2D {

    private final Controller2D c;
    private Graphics g;

    public Renderer2D(Controller2D panel) {
        this.c = panel;
        this.g = c.panel.getRaster().getGraphics();
    }
    
    void render() {
        c.panel.getRaster().clear(Color.BLACK);

        // render active line
        if(c.lineCreation) {
            if(c.activeLine != null) {
                if(c.shiftPressed) {
                    Point snapPoint = c.snap(c.activeLine.getPointA().getX(), c.activeLine.getPointA().getY(), c.snappedX, c.snappedY);
                    c.snappedX = snapPoint.getX();
                    c.snappedY = snapPoint.getY();
                }

                c.activeLine.getPointB().setX(c.snappedX);
                c.activeLine.getPointB().setY(c.snappedY);

                if(c.activeLine.getPointB().getColor() == c.activeLine.getPointA().getColor()) {
                    c.lineRasterizerTrivial.setColor(c.activeLine.getPointA().getColor());
                    c.lineRasterizerTrivial.rasterize(c.activeLine.getPointA().getX(), c.activeLine.getPointA().getY(), c.snappedX, c.snappedY);
                } else {
                    c.lineRasterizerGradient.setColor(c.activeLine.getPointA().getColor());
                    c.lineRasterizerGradient.setGradient(c.activeLine.getPointB().getColor());
                    c.lineRasterizerGradient.rasterize(c.activeLine.getPointA().getX(), c.activeLine.getPointA().getY(), c.snappedX, c.snappedY);
                }


            }
        }

        // render all other lines
        for (Line line : c.lines) {
            if(line.getPointA().getColor() == line.getPointB().getColor()) {
                c.lineRasterizerTrivial.setColor(line.getPointA().getColor());
                c.lineRasterizerTrivial.rasterize(line.getPointA().getX(), line.getPointA().getY(), line.getPointB().getX(), line.getPointB().getY());
            } else {
                c.lineRasterizerGradient.setColor(line.getPointA().getColor());
                c.lineRasterizerGradient.setGradient(line.getPointB().getColor());
                c.lineRasterizerGradient.rasterize(line.getPointA().getX(), line.getPointA().getY(), line.getPointB().getX(), line.getPointB().getY());
            }
        }

        // render active polygon
        if(c.polygonCreation) {
            if(c.activePolygon != null) {
                c.activePolygon.getPoints().get(c.activePolygon.size() - 1).setX(c.snappedX);
                c.activePolygon.getPoints().get(c.activePolygon.size() - 1).setY(c.snappedY);

                renderPolygon(c.activePolygon);
            }
        }

        // render all other polygons
        for (Polygon polygon : c.polygons) {
            renderPolygon(polygon);
        }

        // render all points
        renderPoints();

        renderUI();

        c.panel.repaint();
    }


private void renderPolygon(Polygon polygon) {
        for (int i = 0; i < polygon.size() - 1; i++) {
            if(polygon.getPoints().get(i).getColor() == polygon.getPoints().get(i + 1).getColor()) {
                c.lineRasterizerTrivial.setColor(polygon.getPoints().get(i).getColor());
                c.lineRasterizerTrivial.rasterize(polygon.getPoints().get(i).getX(), polygon.getPoints().get(i).getY(), 
                polygon.getPoints().get(i + 1).getX(), polygon.getPoints().get(i + 1).getY());
            } else {
                c.lineRasterizerGradient.setColor(polygon.getPoints().get(i).getColor());
                c.lineRasterizerGradient.setGradient(polygon.getPoints().get(i + 1).getColor());
                c.lineRasterizerGradient.rasterize(polygon.getPoints().get(i).getX(), polygon.getPoints().get(i).getY(), 
                polygon.getPoints().get(i + 1).getX(), polygon.getPoints().get(i + 1).getY());
            }
        }

        if(polygon.getPoints().get(0).getColor() == polygon.getPoints().get(polygon.size() - 1).getColor()) {
            c.lineRasterizerTrivial.setColor(polygon.getPoints().get(0).getColor());
            c.lineRasterizerTrivial.rasterize(polygon.getPoints().get(0).getX(), polygon.getPoints().get(0).getY(), 
            polygon.getPoints().get(polygon.size() - 1).getX(), polygon.getPoints().get(polygon.size() - 1).getY());
        } else {
            c.lineRasterizerGradient.setColor(polygon.getPoints().get(0).getColor());
            c.lineRasterizerGradient.setGradient(polygon.getPoints().get(polygon.size() - 1).getColor());
            c.lineRasterizerGradient.rasterize(polygon.getPoints().get(0).getX(), polygon.getPoints().get(0).getY(), 
            polygon.getPoints().get(polygon.size() - 1).getX(), polygon.getPoints().get(polygon.size() - 1).getY());
        }
    }

    private void renderPoints() {
        for (Line line : c.lines) {
            for(int j = -2; j <= 2; j++) {
                for(int k = -2; k <= 2; k++) {
                    c.panel.getRaster().setPixel(line.getPointA().getX() + j, line.getPointA().getY() + k, line.getPointA().getColor());
                    c.panel.getRaster().setPixel(line.getPointB().getX() + j, line.getPointB().getY() + k, line.getPointB().getColor());
                }
            }
        }
        for (Polygon polygon : c.polygons) {
            for (Point point : polygon.getPoints()) {
                for(int j = -2; j <= 2; j++) {
                    for(int k = -2; k <= 2; k++) {
                        c.panel.getRaster().setPixel(point.getX() + j, point.getY() + k, point.getColor());
                    }
                }   
            }
        }
    }

    void renderUI() {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, c.h - 40, c.w, 40);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.PLAIN, 13));
        g.drawString("Point Select - Esc", 5, c.h - 25);
        g.drawString("Line - V", 170, c.h - 25);
        g.drawString("Polygon - B", 257, c.h - 25);
        g.drawString("Gradient - G", 370, c.h - 25);
        g.drawString("Clear - C", 490, c.h - 25);
        g.drawString("Snap - Hold Shift", 590, c.h - 25);

        g.drawString("New Point - N", 5, c.h - 5);
        g.drawString("Delete Point - D", 140, c.h - 5);

        g.setColor(Color.GRAY);
        g.drawString("(Confirm polygon creation with RMB)", 450, c.h - 5);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 13));
        g.setColor(Color.GREEN);
        if(c.lineCreation) {
            g.drawString("Line - V", 170, c.h - 25);
        } else if(c.polygonCreation) {
            g.drawString("Polygon - B", 257, c.h - 25);
        } else if(c.newPointCreation) {
            g.drawString("New Point - N", 5, c.h - 5);
        } else if(c.pointDeletion) {
            g.drawString("Delete Point - D", 140, c.h - 5);
        } else if(c.pointMoving) {
            g.drawString("Point Select - Esc", 5, c.h - 25);
        }
        g.setColor(Color.YELLOW);
        if(c.gradientCreation) {
            g.drawString("Gradient - G", 370, c.h - 25);
        }
        if(c.shiftPressed) {
            g.drawString("Snap - Hold Shift", 590, c.h - 25);
        }

    }
}
