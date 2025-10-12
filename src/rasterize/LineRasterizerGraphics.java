package rasterize;

import java.awt.Color;
import java.awt.Graphics;

public class LineRasterizerGraphics extends LineRasterizer {

    public LineRasterizerGraphics(RasterBufferedImage raster) {
        super(raster);
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2) {
        Graphics g = raster.getImage().getGraphics();
        g.setColor(Color.GREEN);
        g.drawLine(x1, y1, x2, y2);
    }
    
}
