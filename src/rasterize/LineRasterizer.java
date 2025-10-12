package rasterize;

public abstract class LineRasterizer {
    protected RasterBufferedImage raster;
    protected int color;
    protected int colorGradient;

    public void setGradient(int color1, int color2) {
        this.color = color1;
        this.colorGradient = color2;
    }

    public void setColor(int color) {
        this.color = color;
        this.colorGradient = -1;
    }

    public LineRasterizer(RasterBufferedImage raster) {
        this.raster = raster;
        this.color = -1;
        this.colorGradient = -1;
    }

    public void rasterize(int x1, int y1, int x2, int y2) {

    }

}
