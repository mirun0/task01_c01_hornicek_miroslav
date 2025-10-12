package controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import model.Line;
import model.Point;
import model.Polygon;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerTrivial;
import view.Panel;

public class Controller2D {
    private final Panel panel;

    private int w;
    private int h;

    private int color = 0xffffff;
    private int gradientColor = 0xff0000;

    private LineRasterizer lineRasterizer;

    private boolean polygonCreation = false;
    private boolean lineCreation = false;
    private boolean gradientCreation = false;

    private boolean shiftPressed = false;

    private double snapTolerance = Math.toRadians(10);
    private double[] snapAngles = {
        0,
        Math.PI / 4,
        Math.PI / 2,
        3 * Math.PI / 4,
        - Math.PI / 2,
        - Math.PI / 4,
        Math.PI,
        - 3 * Math.PI / 4
    };

    private int snappedX;
    private int snappedY;

    private ArrayList<Polygon> polygons = new ArrayList<Polygon>();
    private ArrayList<Line> lines = new ArrayList<Line>();

    private Polygon activePolygon;
    private Line activeLine;

    private Point movingPoint;
    private Graphics g;

    public Controller2D(Panel panel) {
        this.panel = panel;

        this.w = panel.getRaster().getWidth();
        this.h = panel.getRaster().getHeight();

        this.lineRasterizer = new LineRasterizerTrivial(panel.getRaster());
        lineRasterizer.setColor(color);

        g = panel.getRaster().getGraphics();

        initListeners();
        renderUI();
    }

    private void initListeners() {
        panel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                if(e.getButton() == MouseEvent.BUTTON1) {
                    if(lineCreation) {
                        if(activeLine == null) {
                            activeLine = new Line(new Point(e.getX(), e.getY()), null, color);
                            if(gradientCreation) {
                                activeLine.setGradient(color, gradientColor);
                            }
                        } else {
                            activeLine.setB(new Point(snappedX, snappedY));
                            lines.add(activeLine);
                            activeLine = null;
                        }
                    } else if(polygonCreation) {
                        if(activePolygon == null) {
                            ArrayList<Point> points = new ArrayList<>();
                            points.add(new Point(e.getX(), e.getY()));
                            activePolygon = new Polygon(points, color);

                        } else {
                            activePolygon.addPoint(new Point(snappedX, snappedY));
                        }
                    } else {
                        Point find = findPoint(e.getX(), e.getY());
                        if(find != null) {
                            movingPoint = find;
                        } else {
                            movingPoint = null;
                        }
                    }
                }

                if(e.getButton() == MouseEvent.BUTTON3) {

                    if(lineCreation) {
                        activeLine = null;
                    }

                    if(polygonCreation) {
                        if(activePolygon != null && activePolygon.getPoints().size() > 2)
                            polygons.add(activePolygon);
                        activePolygon = null;
                    }
                }

                render();
                panel.repaint();
            }
        });

        panel.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shiftPressed = true;
                }

                if(e.getKeyCode() == KeyEvent.VK_C) {
                    clear();
                    panel.repaint();
                }

                // mod Line
                if(e.getKeyCode() == KeyEvent.VK_V) {
                    polygonCreation = false;
                    lineCreation = true;
                    gradientCreation = false;
                    activePolygon = null;

                    render();
                    panel.repaint();
                }

                // mod Polygon
                if(e.getKeyCode() == KeyEvent.VK_B) {
                    lineCreation = false;
                    polygonCreation = true;
                    gradientCreation = false;
                    activeLine = null;

                    render();
                    panel.repaint();
                }

                if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    gradientCreation = false;
                    lineCreation = false;
                    polygonCreation = false;
                    activeLine = null;
                    activePolygon = null;

                    render();
                    panel.repaint();
                }

                if(e.getKeyCode() == KeyEvent.VK_G) {
                    gradientCreation = true;
                    lineCreation = true;
                    polygonCreation = false;
                    activePolygon = null;

                    render();
                    panel.repaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                
                if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shiftPressed = false;
                }

            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);

                snappedX = e.getX();
                snappedY = e.getY();

                render();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);

                if(movingPoint != null && !lineCreation && !polygonCreation) {
                    movingPoint.setX(e.getX());
                    movingPoint.setY(e.getY());
                }

                render();
            }
        });
    }

    private void render() {
        panel.getRaster().clear(Color.BLACK);

        // render active line
        if(lineCreation) {
            if(activeLine != null) {
                if(shiftPressed) {
                    Point snapPoint = snap(activeLine.getPointA().getX(), activeLine.getPointA().getY(), snappedX, snappedY);
                    snappedX = snapPoint.getX();
                    snappedY = snapPoint.getY();
                }
                lineRasterizer.setColor(activeLine.getColor());
                lineRasterizer.setGradient(activeLine.getColor(), activeLine.getGradient());
                lineRasterizer.rasterize(activeLine.getPointA().getX(), activeLine.getPointA().getY(), snappedX, snappedY);
            }
        }

        // render all other lines
        for (Line line : lines) {
            lineRasterizer.setColor(line.getColor());
            lineRasterizer.setGradient(line.getColor(), line.getGradient());
            lineRasterizer.rasterize(line.getPointA().getX(), line.getPointA().getY(), line.getPointB().getX(), line.getPointB().getY());
        }

        // render active polygon
        if(polygonCreation) {
            if(activePolygon != null) {

                lineRasterizer.setColor(activePolygon.getColor());
                // renderActivePolygon()
                renderPolygon(activePolygon);

                // interactive lines
                lineRasterizer.rasterize(activePolygon.getPoints().get(0).getX() , activePolygon.getPoints().get(0).getY(), 
                snappedX, snappedY);

                lineRasterizer.rasterize(activePolygon.getPoints().get(activePolygon.size() - 1).getX(), 
                activePolygon.getPoints().get(activePolygon.size() - 1).getY(), snappedX, snappedY);

            }
        }

        // render all other polygons
        for (Polygon polygon : polygons) {
            lineRasterizer.setColor(polygon.getColor());
            renderPolygon(polygon);
            lineRasterizer.rasterize(polygon.getPoints().get(0).getX(), polygon.getPoints().get(0).getY(), 
            polygon.getPoints().get(polygon.size() - 1).getX(), polygon.getPoints().get(polygon.size() - 1).getY());
        }

        // render all points
        renderPoints();

        renderUI();

        panel.repaint();
    }

    private void renderUI() {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, h - 20, w, 20);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.PLAIN, 13));
        g.drawString("Point Select - Esc", 5, h - 5);
        g.drawString("Line - V", 170, h - 5);
        g.drawString("Polygon - B", 257, h - 5);
        g.drawString("Gradient - G", 370, h - 5);
        g.drawString("Clear - C", 490, h - 5);
        g.drawString("Snap - Hold Shift", 590, h - 5);

        g.setFont(new Font("Monospaced", Font.BOLD, 13));
        g.setColor(Color.GREEN);
        if(lineCreation & !gradientCreation) {
            g.drawString("Line - V", 170, h - 5);
        } else if(polygonCreation) {
            g.drawString("Polygon - B", 257, h - 5);
        } else if(gradientCreation) {
            g.drawString("Gradient - G", 370, h - 5);
        } else {
            g.drawString("Point Select - Esc", 5, h - 5);
        }

    }

    private void renderPolygon(Polygon polygon) {
        for (int i = 0; i < polygon.size() - 1; i++) {
            lineRasterizer.rasterize(polygon.getPoints().get(i).getX() , polygon.getPoints().get(i).getY(), 
            polygon.getPoints().get(i + 1).getX(), polygon.getPoints().get(i + 1).getY());
        }
    }

    private void renderPoints() {
        for (Line line : lines) {
            for(int j = -2; j <= 2; j++) {
                for(int k = -2; k <= 2; k++) {
                    panel.getRaster().setPixel(line.getPointA().getX() + j, line.getPointA().getY() + k, color);
                    panel.getRaster().setPixel(line.getPointB().getX() + j, line.getPointB().getY() + k, color);
                }
            }
        }
        for (Polygon polygon : polygons) {
            for (Point point : polygon.getPoints()) {
                for(int j = -2; j <= 2; j++) {
                    for(int k = -2; k <= 2; k++) {
                        panel.getRaster().setPixel(point.getX() + j, point.getY() + k, color);
                    }
                }   
            }
        }
    }

    private Point findPoint(int x, int y) {
        for (Line line : lines) {
            int pxa = line.getPointA().getX();
            int pya = line.getPointA().getY();
            double da = Math.hypot(x - pxa, y - pya);
            if (da <= 10) {
                return line.getPointA();
            }

            int pxb = line.getPointB().getX();
            int pyb = line.getPointB().getY();
            double db = Math.hypot(x - pxb, y - pyb);
            if (db <= 10) {
                return line.getPointB();
            }
        }

        for (Polygon polygon : polygons) {
            for (Point point : polygon.getPoints()) {
                double db = Math.hypot(x - point.getX(), y - point.getY());
                if (db <= 10) {
                    return point;
                }
            }
        }
        return null;
    }

    private Point snap(int startX, int startY, int nowX, int nowY) {
        double dx = nowX - startX;
        double dy = nowY - startY;
        double angle = Math.atan2(dy, dx);

        for (double a : snapAngles) {
            if (Math.abs(angle - a) < snapTolerance) {
                angle = a;
                break;
            }
        }

        double length = Math.hypot(dx, dy);
        int snappedX = (int) Math.round(startX + length * Math.cos(angle));
        int snappedY = (int) Math.round(startY + length * Math.sin(angle));

        return new Point(snappedX, snappedY);
    }

    public void clear() {
        panel.getRaster().clear(Color.BLACK);
        polygons.clear();
        lines.clear();
        activeLine = null;
        activePolygon = null;
        render();
    }

}
