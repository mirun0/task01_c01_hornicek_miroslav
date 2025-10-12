package rasterize;

import java.awt.Color;

public interface Raster {

    public void setPixel(int x, int y, int color);
    public int getPixel(int x, int y);
    public int getWidth();
    public int getHeight();
    public void clear(Color color);
}
