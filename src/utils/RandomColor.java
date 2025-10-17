package utils;

import java.awt.Color;

public class RandomColor {
    public static int create() {
        return new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)).getRGB();
    }
}
