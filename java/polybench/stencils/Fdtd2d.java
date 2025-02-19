import java.util.Arrays;

public class Fdtd2d {
    public static final int TMAX = 100;
    public static final int NX = 200;
    public static final int NY = 240;
    
    public static void initArray(int tmax, int nx, int ny, double[][] ex, double[][] ey, double[][] hz, double[] fict) {
        for (int i = 0; i < tmax; i++) {
            fict[i] = (double) i;
        }
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                ex[i][j] = ((double) i * (j + 1)) / nx;
                ey[i][j] = ((double) i * (j + 2)) / ny;
                hz[i][j] = ((double) i * (j + 3)) / nx;
            }
        }
    }
    
    public static void printArray(int nx, int ny, double[][] ex, double[][] ey, double[][] hz) {
        System.out.println("==BEGIN DUMP_ARRAYS==");
        System.out.printf("begin dump: ex");
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                if ((i * nx + j) % 20 == 0) System.out.println();
                System.out.printf("%.6f ", ex[i][j]);
            }
        }
        System.out.println();
        System.out.println("end   dump: ex");
        System.out.println("==END   DUMP_ARRAYS==");
        
        System.out.printf("begin dump: ey");
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                if ((i * nx + j) % 20 == 0) System.out.println();
                System.out.printf("%.6f ", ey[i][j]);
            }
        }
        System.out.println();
        System.out.println("end   dump: ey");
        
        System.out.printf("begin dump: hz");
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                if ((i * nx + j) % 20 == 0) System.out.println();
                System.out.printf("%.6f ", hz[i][j]);
            }
        }
        System.out.println();
        System.out.println("end   dump: hz");
    }
    
    public static void kernelFDTD2D(int tmax, int nx, int ny, double[][] ex, double[][] ey, double[][] hz, double[] fict) {
        for (int t = 0; t < tmax; t++) {
            for (int j = 0; j < ny; j++) {
                ey[0][j] = fict[t];
            }
            for (int i = 1; i < nx; i++) {
                for (int j = 0; j < ny; j++) {
                    ey[i][j] = ey[i][j] - 0.5 * (hz[i][j] - hz[i - 1][j]);
                }
            }
            for (int i = 0; i < nx; i++) {
                for (int j = 1; j < ny; j++) {
                    ex[i][j] = ex[i][j] - 0.5 * (hz[i][j] - hz[i][j - 1]);
                }
            }
            for (int i = 0; i < nx - 1; i++) {
                for (int j = 0; j < ny - 1; j++) {
                    hz[i][j] = hz[i][j] - 0.7 * (ex[i][j + 1] - ex[i][j] + ey[i + 1][j] - ey[i][j]);
                }
            }
        }
    }
    
    public static void main(String[] args) {
        int tmax = TMAX;
        int nx = NX;
        int ny = NY;
        
        double[][] ex = new double[NX][NY];
        double[][] ey = new double[NX][NY];
        double[][] hz = new double[NX][NY];
        double[] fict = new double[TMAX];
        
        initArray(tmax, nx, ny, ex, ey, hz, fict);
        
        long startTime = System.currentTimeMillis();
        kernelFDTD2D(tmax, nx, ny, ex, ey, hz, fict);
        long endTime = System.currentTimeMillis();
        
        // System.out.println("Execution time: " + (endTime - startTime) + "ms");
        
        printArray(nx, ny, ex, ey, hz);
    }
}
