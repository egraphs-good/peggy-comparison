import java.util.Arrays;

public class Deriche {
    private static final int W = 720;
    private static final int H = 480;

    public static void initArray(int w, int h, double[][] imgIn) {
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                imgIn[i][j] = ((313 * i + 991 * j) % 65536) / 65535.0;
            }
        }
    }

    public static void printArray(int w, int h, double[][] imgOut) {
        System.out.println("Output Image:");
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                
                if ((i * h + j) % 20 == 0) System.out.println();
                System.out.printf("%.6f ", imgOut[i][j]);
            }
            // System.out.println();
        }
    }

    public static void kernelDeriche(int w, int h, double alpha, double[][] imgIn, double[][] imgOut, double[][] y1, double[][] y2) {
        int i, j;
        double xm1, tm1, ym1, ym2;
        double xp1, xp2;
        double tp1, tp2;
        double yp1, yp2;

        double k = (1.0f - (double) Math.exp(-alpha)) * (1.0f - (double) Math.exp(-alpha)) /
                (1.0f + 2.0f * alpha * (double) Math.exp(-alpha) - (double) Math.exp(2.0f * alpha));
        double a1 = k, a5 = k;
        double a2 = k * (double) Math.exp(-alpha) * (alpha - 1.0f);
        double a6 = a2;
        double a3 = k * (double) Math.exp(-alpha) * (alpha + 1.0f);
        double a7 = a3;
        double a4 = -k * (double) Math.exp(-2.0f * alpha);
        double a8 = a4;
        double b1 = (double) Math.pow(2.0f, -alpha);
        double b2 = -(double) Math.exp(-2.0f * alpha);
        double c1 = 1, c2 = 1;

        for (i = 0; i < w; i++) {
            ym1 = ym2 = xm1 = 0.0f;
            for (j = 0; j < h; j++) {
                y1[i][j] = a1 * imgIn[i][j] + a2 * xm1 + b1 * ym1 + b2 * ym2;
                xm1 = imgIn[i][j];
                ym2 = ym1;
                ym1 = y1[i][j];
            }
        }

        for (i = 0; i < w; i++) {
            yp1 = yp2 = xp1 = xp2 = 0.0f;
            for (j = h - 1; j >= 0; j--) {
                y2[i][j] = a3 * xp1 + a4 * xp2 + b1 * yp1 + b2 * yp2;
                xp2 = xp1;
                xp1 = imgIn[i][j];
                yp2 = yp1;
                yp1 = y2[i][j];
            }
        }

        for (i = 0; i < w; i++) {
            for (j = 0; j < h; j++) {
                imgOut[i][j] = c1 * (y1[i][j] + y2[i][j]);
            }
        }

        for (j = 0; j < h; j++) {
            tm1 = ym1 = ym2 = 0.0f;
            for (i = 0; i < w; i++) {
                y1[i][j] = a5 * imgOut[i][j] + a6 * tm1 + b1 * ym1 + b2 * ym2;
                tm1 = imgOut[i][j];
                ym2 = ym1;
                ym1 = y1[i][j];
            }
        }

        for (j = 0; j < h; j++) {
            tp1 = tp2 = yp1 = yp2 = 0.0f;
            for (i = w - 1; i >= 0; i--) {
                y2[i][j] = a7 * tp1 + a8 * tp2 + b1 * yp1 + b2 * yp2;
                tp2 = tp1;
                tp1 = imgOut[i][j];
                yp2 = yp1;
                yp1 = y2[i][j];
            }
        }

        for (i = 0; i < w; i++) {
            for (j = 0; j < h; j++) {
                imgOut[i][j] = c2 * (y1[i][j] + y2[i][j]);
            }
        }
    }


    public static void main(String[] args) {
        int w = W;
        int h = H;

        double alpha = 0.25;
        double[][] imgIn = new double[w][h];
        double[][] imgOut = new double[w][h];
        double[][] y1 = new double[w][h];
        double[][] y2 = new double[w][h];

        initArray(w, h, imgIn);

        long startTime = System.nanoTime();
        kernelDeriche(w, h, alpha, imgIn, imgOut, y1, y2);
        long endTime = System.nanoTime();

        System.out.println("Execution time: " + (endTime - startTime) / 1e6 + " ms");
        
        printArray(w, h, imgOut);
    }
}
