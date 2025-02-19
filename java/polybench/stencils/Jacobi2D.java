import java.util.Arrays;

public class Jacobi2D {
    public static final int N = 250;
    public static final int TSTEPS = 100;
    
    public static void initArray(int n, double[][] A, double[][] B) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = ((double) i * (j + 2) + 2) / n;
                B[i][j] = ((double) i * (j + 3) + 3) / n;
            }
        }
    }
    
    public static void printArray(int n, double[][] A) {
        System.out.println("A:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                
                if ((i * n + j) % 20 == 0) System.out.println();
                System.out.printf("%.2f ", A[i][j]);
            }
        }
    }
    
    public static void kernelJacobi2D(int tsteps, int n, double[][] A, double[][] B) {
        for (int t = 0; t < tsteps; t++) {
            for (int i = 1; i < n - 1; i++) {
                for (int j = 1; j < n - 1; j++) {
                    B[i][j] = 0.2 * (A[i][j] + A[i][j - 1] + A[i][j + 1] + A[i + 1][j] + A[i - 1][j]);
                }
            }
            for (int i = 1; i < n - 1; i++) {
                for (int j = 1; j < n - 1; j++) {
                    A[i][j] = 0.2 * (B[i][j] + B[i][j - 1] + B[i][j + 1] + B[i + 1][j] + B[i - 1][j]);
                }
            }
        }
    }
    
    public static void main(String[] args) {
        int n = N;
        int tsteps = TSTEPS;
        
        double[][] A = new double[N][N];
        double[][] B = new double[N][N];
        
        initArray(n, A, B);
        
        long startTime = System.currentTimeMillis();
        kernelJacobi2D(tsteps, n, A, B);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
        
        printArray(n, A);
    }
}
