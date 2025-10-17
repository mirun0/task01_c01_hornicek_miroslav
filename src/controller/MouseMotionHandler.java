package controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class MouseMotionHandler extends MouseMotionAdapter {

    private final Controller2D c;

    public MouseMotionHandler(Controller2D c) {
        this.c = c;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);

        c.snappedX = e.getX();
        c.snappedY = e.getY();

        c.render();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);

        c.snappedX = e.getX();
        c.snappedY = e.getY();

        if (c.movingPoint != null && !c.lineCreation && !c.polygonCreation) {
            c.movingPoint.setX(e.getX());
            c.movingPoint.setY(e.getY());
        }

        c.render();
    }
}
