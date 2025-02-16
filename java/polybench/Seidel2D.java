import java.util.Arrays;

public class Seidel2D {
    private static final int N = 400;
    private static final int TSTEPS = 100;
    
    private static void initArray(int n, double[][] A) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = ((double) i * (j + 2) + 2) / n;
            }
        }
    }
    
    private static void printArray(int n, double[][] A) {
        System.out.println("A:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                
                if ((i * n + j) % 20 == 0) System.out.println();
                System.out.printf("%.5f ", A[i][j]);
            }
        }
    }
    
    private static void kernelSeidel2D(int tsteps, int n, double[][] A) {
        for (int t = 0; t <= tsteps - 1; t++) {
            for (int i = 1; i <= n - 2; i++) {
                for (int j = 1; j <= n - 2; j++) {
                    A[i][j] = (A[i - 1][j - 1] + A[i - 1][j] + A[i - 1][j + 1]
                             + A[i][j - 1] + A[i][j] + A[i][j + 1]
                             + A[i + 1][j - 1] + A[i + 1][j] + A[i + 1][j + 1]) / 9.0;
                }
            }
        }
    }
    
    public static void main(String[] args) {
        int n = N;
        int tsteps = TSTEPS;
        
        double[][] A = new double[N][N];
        
        initArray(n, A);
        
        long startTime = System.currentTimeMillis();
        kernelSeidel2D(tsteps, n, A);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
        
        printArray(n, A);
    }
}
