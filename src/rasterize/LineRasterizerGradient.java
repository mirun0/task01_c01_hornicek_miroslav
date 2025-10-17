package rasterize;

import java.awt.Color;

import utils.MathUtils;

public class LineRasterizerGradient extends LineRasterizer {

    private float length;
    private int colorGradient;

    public LineRasterizerGradient(RasterBufferedImage raster) {
        super(raster);
        this.length = 0;
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2) {
        trivialRasterize(x1, y1, x2, y2);
    }

    private void trivialRasterize(int x1, int y1, int x2, int y2) {
        this.length = MathUtils.length(x1, y1, x2, y2);

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
            for (int y = y1; y < y2; y++) {
                //paintPixel(x1, y, MathUtils.length(x1, y1, x1, y));
                paintPixel2(x1, y, gradient2(y, y1, y2));
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
            for (int x = x1; x < x2; x++) {
                int y = Math.round(k * x + q);
                //paintPixel(x, y, MathUtils.length(x1, y1, x, y));
                paintPixel2(x, y, gradient2(x, x1, x2));
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
        for (int y = y1; y < y2; y++) {
            int x = Math.round((y - q) / k);
            //paintPixel(x, y, MathUtils.lenght(x1, y1, x, y));
            paintPixel2(x, y, gradient2(y, y1, y2));
        }
    }


    public void paintPixel2(int x, int y, int color) {
        raster.setPixel(x, y, color);
    }

    public int gradient2(int nowPixel, int min, int max) {
        Color color = new Color(this.color);
        Color grad = new Color(this.colorGradient);
        float[] colorComps = color.getComponents(null);
        float[] gradComps = grad.getComponents(null);
        float[] newColorComps = new float[3];

        float t = (nowPixel - min) / (float)(max - min);
        for (int i = 0; i < 3; i++) {
            newColorComps[i] = (1 - t) * colorComps[i] + (t * gradComps[i]);
        }
        Color newColor = new Color(color.getColorSpace(), newColorComps, 1);
        return newColor.getRGB();
    }

    public void paintPixel(int x, int y, float lineNowPixel) {
        raster.setPixel(x, y, gradient(lineNowPixel));
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
            if(newColorComps[i] > 1) { // obcas se buglo ze by vysel float 1.00001
                newColorComps[i] = 1;
            }
        }
        Color newColor = new Color(color.getColorSpace(), newColorComps, 1);
        return newColor.getRGB();
    }

    public void setGradient(int colorGradient) {
        this.colorGradient = colorGradient;
    }
}
