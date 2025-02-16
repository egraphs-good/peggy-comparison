import java.util.Arrays;

public class Deriche {
    public static final int W = 64;
    public static final int H = 64;

    public static void initArray(int w, int h, double[][] imgIn, double[][] imgOut) {
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
            System.out.println();
        }
    }

    public static void kernelDeriche(int w, int h, double alpha, double[][] imgIn, double[][] imgOut, double[][] y1, double[][] y2) {
        double xm1, tm1, ym1, ym2;
        double xp1, xp2;
        double tp1, tp2;
        double yp1, yp2;
        double k = (1.0 - Math.exp(-alpha)) * (1.0 - Math.exp(-alpha)) / (1.0 + 2.0 * alpha * Math.exp(-alpha) - Math.exp(-2.0 * alpha));
        double a1 = k, a5 = k;
        double a2 = k * Math.exp(-alpha) * (alpha - 1.0);
        double a6 = a2;
        double a3 = k * Math.exp(-alpha) * (alpha + 1.0);
        double a7 = a3;
        double a4 = -k * Math.exp(-2.0 * alpha);
        double a8 = a4;
        double b1 = Math.pow(2.0, -alpha);
        double b2 = -Math.exp(-2.0 * alpha);
        double c1 = 1.0, c2 = 1.0;
        
        for (int i = 0; i < w; i++) {
            ym1 = 0.0;
            ym2 = 0.0;
            xm1 = 0.0;
            for (int j = 0; j < h; j++) {
                y1[i][j] = a1 * imgIn[i][j] + a2 * xm1 + b1 * ym1 + b2 * ym2;
                xm1 = imgIn[i][j];
                ym2 = ym1;
                ym1 = y1[i][j];
            }
        }
        
        for (int i = 0; i < w; i++) {
            yp1 = 0.0;
            yp2 = 0.0;
            xp1 = 0.0;
            xp2 = 0.0;
            for (int j = h - 1; j >= 0; j--) {
                y2[i][j] = a3 * xp1 + a4 * xp2 + b1 * yp1 + b2 * yp2;
                xp2 = xp1;
                xp1 = imgIn[i][j];
                yp2 = yp1;
                yp1 = y2[i][j];
            }
        }
        
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                imgOut[i][j] = c1 * (y1[i][j] + y2[i][j]);
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

        initArray(w, h, imgIn, imgOut);

        long startTime = System.nanoTime();
        // kernelDeriche(w, h, alpha, imgIn, imgOut, y1, y2);
        long endTime = System.nanoTime();

        System.out.println("Execution time: " + (endTime - startTime) / 1e6 + " ms");
        
        printArray(w, h, imgOut);
    }
}
