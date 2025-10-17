package controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class KeyHandler extends KeyAdapter {

    private final Controller2D c;
    private final ArrayList<Integer> validModeKeys; // klavesy co meni ruzne mody
    private final ArrayList<Integer> validNormalKeys; // klavesy co delaji veci, ktere neprepnou mod

    public KeyHandler(Controller2D c) {
        this.c = c;
        this.validModeKeys = new ArrayList<>();
        this.validNormalKeys = new ArrayList<>();

        validModeKeys.addAll(Arrays.asList(KeyEvent.VK_B, KeyEvent.VK_ESCAPE, KeyEvent.VK_V, KeyEvent.VK_N, KeyEvent.VK_D));
        validNormalKeys.addAll(Arrays.asList(KeyEvent.VK_SHIFT, KeyEvent.VK_C, KeyEvent.VK_G));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);

        if(validNormalKeys.contains(e.getKeyCode())) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_SHIFT: {
                    c.shiftPressed = true;
                    break;
                }
                case KeyEvent.VK_G: {
                    c.gradientCreation = c.gradientCreation ? false : true;
                    break;
                }
                case KeyEvent.VK_C: {
                    c.clear();
                    break;
                }
            }

            c.render();
            return;
        }

        if(validModeKeys.contains(e.getKeyCode())) {
            c.activeLine = null;
            c.activePolygon = null;
            c.lineCreation = false;
            c.polygonCreation = false;
            c.pointMoving = false;
            c.newPointCreation = false;
            c.pointDeletion = false;

            switch (e.getKeyCode()) {
                case KeyEvent.VK_V: {
                    c.lineCreation = true;
                    break;
                }
                case KeyEvent.VK_B: {
                    c.polygonCreation = true;
                    break;
                }
                case KeyEvent.VK_ESCAPE: {
                    c.pointMoving = true;
                    break;
                }
                case KeyEvent.VK_N: {
                    c.newPointCreation = true;
                    break;
                }
                case KeyEvent.VK_D: {
                    c.pointDeletion = true;
                    break;
                }
                
            }
            c.render();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        super.keyReleased(e);
        if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
            c.shiftPressed = false;
            c.render();
        }
    }

}
