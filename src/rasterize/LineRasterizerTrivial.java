package rasterize;

import utils.MathUtils;

public class LineRasterizerTrivial extends LineRasterizer {

    public LineRasterizerTrivial(RasterBufferedImage raster) {
        super(raster);
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2) {
        trivialRasterize(x1, y1, x2, y2);
    }

    private void trivialRasterize(int x1, int y1, int x2, int y2) {

        // pokud jsou x bodu stejne => vertikalni line
        if (x1 == x2) { // line smerem dolu
            if (y1 > y2) { // line smerem nahoru
                int temp = y1;
                y1 = y2;
                y2 = temp;
            }
            // vykreslit vsechny y pro x
            for (int y = y1; y <= y2; y++) {
                paintPixel(x1, y, MathUtils.length(x1, y1, x1, y));
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
                x1 = x2;
                y1 = y2;
                x2 = tempX;
                y2 = tempY;
            }

            // vypocitat y pro kazde x a vykrelit
            for (int x = x1; x <= x2; x++) {
                int y = Math.round(k * x + q);
                paintPixel(x, y, MathUtils.length(x1, y1, x, y));
            }
            return;
        }

        // pokud je k z intervalu mimo (-1, 1)
        // dolni kvadrant 
        if (y1 > y2) { // horni kvadrant
            int tempX = x1;
            int tempY = y1;
            x1 = x2;
            y1 = y2;
            x2 = tempX;
            y2 = tempY;
        }

        // vypocitat x pro kazde y a vykreslit
        for (int y = y1; y <= y2; y++) {
            int x = Math.round((y - q) / k);
            paintPixel(x, y, MathUtils.length(x1, y1, x, y));
        }
    }

    public void paintPixel(int x, int y, float lineNowPixel) {
        raster.setPixel(x, y, super.color);
    }

}
