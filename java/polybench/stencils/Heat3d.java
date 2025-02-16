import java.util.Arrays;

public class Heat3d {
    public static final int N = 40;
    public static final int TSTEPS = 100;
    
    public static void initArray(int n, double[][][] A, double[][][] B) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    A[i][j][k] = B[i][j][k] = (double) (i + j + (n - k)) * 10 / n;
                }
            }
        }
    }
    
    public static void printArray(int n, double[][][] A) {
        System.out.println("A:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    
                    if ((i * n * n + j * n + k) % 20 == 0) System.out.println();
                    System.out.printf("%.2f ", A[i][j][k]);
                }
            }
        }
    }
    
    public static void kernelHeat3d(int tsteps, int n, double[][][] A, double[][][] B) {
        for (int t = 1; t <= tsteps; t++) {
            for (int i = 1; i < n - 1; i++) {
                for (int j = 1; j < n - 1; j++) {
                    for (int k = 1; k < n - 1; k++) {
                        B[i][j][k] = 0.125 * (A[i+1][j][k] - 2.0 * A[i][j][k] + A[i-1][j][k])
                                   + 0.125 * (A[i][j+1][k] - 2.0 * A[i][j][k] + A[i][j-1][k])
                                   + 0.125 * (A[i][j][k+1] - 2.0 * A[i][j][k] + A[i][j][k-1])
                                   + A[i][j][k];
                    }
                }
            }
            for (int i = 1; i < n - 1; i++) {
                for (int j = 1; j < n - 1; j++) {
                    for (int k = 1; k < n - 1; k++) {
                        A[i][j][k] = 0.125 * (B[i+1][j][k] - 2.0 * B[i][j][k] + B[i-1][j][k])
                                   + 0.125 * (B[i][j+1][k] - 2.0 * B[i][j][k] + B[i][j-1][k])
                                   + 0.125 * (B[i][j][k+1] - 2.0 * B[i][j][k] + B[i][j][k-1])
                                   + B[i][j][k];
                    }
                }
            }
        }
    }
    
    public static void main(String[] args) {
        int n = N;
        int tsteps = TSTEPS;
        
        double[][][] A = new double[N][N][N];
        double[][][] B = new double[N][N][N];
        
        initArray(n, A, B);
        
        long startTime = System.currentTimeMillis();
        kernelHeat3d(tsteps, n, A, B);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
        
        printArray(n, A);
    }
}
