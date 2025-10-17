package rasterize;

public abstract class LineRasterizer {
    protected RasterBufferedImage raster;
    protected int color;

    public void setColor(int color) {
        this.color = color;
    }

    public LineRasterizer(RasterBufferedImage raster) {
        this.raster = raster;
        this.color = 1;
    }

    public void rasterize(int x1, int y1, int x2, int y2) {

    }

}
