package rasterize;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class RasterBufferedImage implements Raster {

    private BufferedImage image;

    public RasterBufferedImage(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public void setPixel(int x, int y, int color) {
        if(image.getRaster().getWidth() <= x || x < 0 || image.getRaster().getHeight() <= y || y < 0) {
            return;
        }

        image.setRGB(x, y, color);
    }

    @Override
    public int getPixel(int x, int y) {
        // TODO: druha uloha
        throw new UnsupportedOperationException("Unimplemented method 'getPixel'");
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

    @Override
    public void clear(Color color) {
        Graphics g = image.getGraphics();
        g.setColor(color);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
    }

    public BufferedImage getImage() {
        return image;
    }

    public Graphics getGraphics() {
        return image.getGraphics();
    }
    
}
