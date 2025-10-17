package controller;

import java.awt.Color;
import java.util.ArrayList;

import model.Line;
import model.Point;
import model.Polygon;
import rasterize.LineRasterizerGradient;
import rasterize.LineRasterizerTrivial;
import utils.MathUtils;
import utils.RandomColor;
import view.Panel;

public class Controller2D {
    Panel panel;

    int w;
    int h;

    int color = 0xffffff;
    int gradientColor = 0xff0000;

    LineRasterizerTrivial lineRasterizerTrivial;
    LineRasterizerGradient lineRasterizerGradient;

    boolean polygonCreation = false;
    boolean lineCreation = false;
    boolean gradientCreation = false;
    boolean newPointCreation = false;
    boolean pointDeletion = false;
    boolean pointMoving = false;
    
    Polygon deletingPointFrom;

    boolean shiftPressed = false;

    double snapTolerance = Math.toRadians(15);
    double[] snapAngles = {
        0,
        Math.PI / 4,
        Math.PI / 2,
        3 * Math.PI / 4,
        - Math.PI / 2,
        - Math.PI / 4,
        Math.PI,
        - 3 * Math.PI / 4
    };

    int snappedX;
    int snappedY;

    ArrayList<Polygon> polygons;
    ArrayList<Line> lines;

    Polygon activePolygon;
    Line activeLine;

    Point movingPoint;

    float findPointPrecision = 10;
    float createNewPointPrecision = 30;

    MouseHandler mouseHandler;
    KeyHandler keyHandler;
    MouseMotionHandler mouseMotionHandler;

    Renderer2D renderer2d;

    public Controller2D(Panel panel) {
        this.panel = panel;

        this.w = panel.getRaster().getWidth();
        this.h = panel.getRaster().getHeight();

        this.polygons = new ArrayList<Polygon>();
        this.lines = new ArrayList<Line>();

        this.lineRasterizerTrivial = new LineRasterizerTrivial(panel.getRaster());
        this.lineRasterizerGradient = new LineRasterizerGradient(panel.getRaster());

        this.renderer2d = new Renderer2D(this);
        this.mouseHandler = new MouseHandler(this);
        this.keyHandler = new KeyHandler(this);
        this.mouseMotionHandler = new MouseMotionHandler(this);

        initListeners();
        renderer2d.renderUI();
    }

    void initListeners() {
        panel.addMouseListener(mouseHandler);
        panel.addKeyListener(keyHandler);
        panel.addMouseMotionListener(mouseMotionHandler);
    }

    void render() {
        renderer2d.render();
    }


    Point findPoint(int x, int y) {
        Point closest = null;
        float closestLength = findPointPrecision;
        float l;

        for (Line line : lines) {
            l = MathUtils.length(x, y, line.getPointA().getX(), line.getPointA().getY());
            if (l <= findPointPrecision) {
                if(closestLength > l) {
                    closestLength = l;
                    closest = line.getPointA();
                }
            }

            l = MathUtils.length(x, y, line.getPointB().getX(), line.getPointB().getY());
            if (l <= findPointPrecision) {
                if(closestLength > l) {
                    closestLength = l;
                    closest = line.getPointB();
                }
            }
        }

        for (Polygon polygon : polygons) {
            for (Point point : polygon.getPoints()) {
                l = MathUtils.length(x, y, point.getX(), point.getY());
                if (l <= findPointPrecision) {
                    if(closestLength > l) {
                        closestLength = l;
                        closest = point;
                        deletingPointFrom = polygon;
                    }
                }
            }
        }

        return closest;
    }

    void deletePoint() {
        Point p = findPoint(snappedX, snappedY);
        if(deletingPointFrom == null) return;
        if(deletingPointFrom.size() < 4) {
            polygons.remove(deletingPointFrom);
            deletingPointFrom = null;
        } else {
            deletingPointFrom.getPoints().remove(p);
        }
    }

    void createNewPoint() {
        float closestLength = createNewPointPrecision;
        int closestPointIndex = -1;
        int closestPolygonIndex = -1;

        int x = 0;
        int y = 0;

        for (int i = 0; i < polygons.size(); i++) {

            Polygon polygon = polygons.get(i);
            polygon.addPoint(polygon.getPoints().get(0));
            for (int j = 0; j < polygon.getPoints().size() - 1; j++) {
                Point pa = polygon.getPoints().get(j);
                Point pb = polygon.getPoints().get(j + 1);

                // obecny tvar primky (ax + by + c = 0) (kde zde x a y jsou souradnice noveho bodu)
                // a = y2 - y1
                // b = -(x2 - x1)
                // c = (x2 - x1)*y1 - (y2 - y1)*x1
                float a = pb.getY() - pa.getY();
                float b = -(pb.getX() - pa.getX());
                float c = -b * pa.getY() - (float)a * pa.getX();
                // kolmy posun rika jak daleko je bod od primky ve smeru normaly k usecce (potreba normalizovat delkou vydelenim)
                float l = Math.abs(a * snappedX + b * snappedY + c) / (float)Math.sqrt(a*a + b*b); // kolmy posun / delka

                // pomoci skalaru (dot product) vypocitame uhel mezi bodem mysi (P) a pointem A a useckou A B, tento uhel rika jak moc je
                // usecka AP rovnobezna s AB. t je pomer mezi skalarnim soucinem vektoru AP a AB a celkovou delkou usecky (cimz rekne zda 
                // lezi projekce na usecce)
                // pokud na usecku ani bod P "nedopadne", tak t < 0 a pokud ji "prepadne" tak t > 1

                //     P
                //    /|
                //   / |
                //  /  |
                // A---|------- B     (AP' je projekce vektoru AP na usecce AB, zde je t cca 0.4, takze lezi uvnitr usecky a muzem s nim dal pocitat)
                //     P'

                float dx = pb.getX() - pa.getX();
                float dy = pb.getY() - pa.getY();
                float len = dx*dx + dy*dy;
                float t = ((snappedX - pa.getX()) * dx + (snappedY - pa.getY()) * dy) / len;   // skalarni soucin AP * AB / delka na 2

                if(t >= 0 && t <= 1) {
                    if(l <= createNewPointPrecision) {
                        if(closestLength > l) {
                            closestLength = l;
                            closestPolygonIndex = i;
                            closestPointIndex = j + 1; // chceme na B

                            // projected souradnice primo na te usecce (aby bod byl na usecce, ne na pozici mysi)
                            // A ----- P (t) -------B
                            x = (int)(pa.getX() + t * (pb.getX() - pa.getX()));
                            y = (int)(pa.getY() + t * (pb.getY() - pa.getY()));
                        }
                    }
                }
            }
            polygon.getPoints().remove(polygon.size() - 1);
        }

        if(closestPolygonIndex != -1) {
            int c = color;
            if(gradientCreation) {
                c = RandomColor.create();
            }
            polygons.get(closestPolygonIndex).getPoints().add(closestPointIndex, new Point(x, y, c));
        }
    }

    Point snap(int startX, int startY, int nowX, int nowY) {
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

        return new Point(snappedX, snappedY, 1);
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
