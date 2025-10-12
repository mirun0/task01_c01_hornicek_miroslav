package rasterize;

import java.awt.Color;

public class LineRasterizerTrivial extends LineRasterizer {

    private float length;

    public LineRasterizerTrivial(RasterBufferedImage raster) {
        super(raster);
        this.length = 0;
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2) {

        if(super.colorGradient != -1 & super.color != -1) {
            trivialRasterize(x1, y1, x2, y2, true);
        } else {
            trivialRasterize(x1, y1, x2, y2, false);
        }
    }

    private float lenght(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private void trivialRasterize(int x1, int y1, int x2, int y2, boolean grad) {
        this.length = lenght(x1, y1, x2, y2);

        // pokud jsou x bodu stejne => vertikalni line
        if (x1 == x2) { // line smerem dolu
            if (y1 > y2) { // line smerem nahoru
                int temp = y1;
                int tempC = color;
                y1 = y2;
                color = colorGradient;
                y2 = temp;
                colorGradient = tempC;
            }
            // vykreslit vsechny y pro x
            for (int y = y1; y <= y2; y++) {
                paintPixel(x1, y, lenght(x1, y1, x1, y), grad);
            }
            return;
        }

        // vypocet smernice usecky
        float k = (y2 - y1) / (float)(x2 - x1);

        // vypocet posunu na ose y
        float q = y1 - (k * x1);

        // pokud je k z intervalu (-1, 1)
        if(-1 < k && k < 1) { // levy kvadrant
            if (x1 > x2) { // pravy kvadrant
                int tempX = x1;
                int tempY = y1;
                int tempC = color;
                x1 = x2;
                y1 = y2;
                color = colorGradient;
                x2 = tempX;
                y2 = tempY;
                colorGradient = tempC;
            }

            // vypocitat y pro kazde x a vykrelit
            for (int x = x1; x <= x2; x++) {
                int y = Math.round(k * x + q);
                paintPixel(x, y, lenght(x1, y1, x, y), grad);
            }
            return;
        }

        // pokud je k z intervalu mimo (-1, 1)
        // dolni kvadrant 
        if (y1 > y2) { // horni kvadrant
            int tempX = x1;
            int tempY = y1;
            int tempC = color;
            x1 = x2;
            y1 = y2;
            color = colorGradient;
            x2 = tempX;
            y2 = tempY;
            colorGradient = tempC;
        }

        // vypocitat x pro kazde y a vykreslit
        for (int y = y1; y <= y2; y++) {
            int x = Math.round((y - q) / k);
            paintPixel(x, y, lenght(x1, y1, x, y), grad);
        }
    }

    public void paintPixel(int x, int y, float lineNowPixel, boolean grad) {
        if(grad) {
            raster.setPixel(x, y, gradient(lineNowPixel));
        } else {
            raster.setPixel(x, y, super.color);
        }
    }

    public int gradient(float lineNowPixel) {
        Color color = new Color(this.color);
        Color grad = new Color(this.colorGradient);
        float[] colorComps = color.getComponents(null);
        float[] gradComps = grad.getComponents(null);
        float[] newColorComps = new float[3];

        for (int i = 0; i < 3; i++) {
            float diff = gradComps[i] - colorComps[i];
            newColorComps[i] = (diff / length) * lineNowPixel + colorComps[i];
        }
        Color newColor = new Color(color.getColorSpace(), newColorComps, 1);
        return newColor.getRGB();
    }
}
