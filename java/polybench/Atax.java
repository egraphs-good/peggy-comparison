import java.util.Arrays;

public class Atax {
    private static final int M = 390;
    private static final int N = 410;
    
    private static void initArray(int m, int n, double[][] A, double[] x) {
        double fn = (double) n;
        
        for (int i = 0; i < n; i++) {
            x[i] = 1 + (i / fn);
        }
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = ((i + j) % n) / (5.0 * m);
            }
        }
    }
    
    private static void printArray(int n, double[] y) {
        System.out.println("y:");
        for (int i = 0; i < n; i++) {
            if (i % 20 == 0) System.out.println();
            System.out.printf("%.2f ", y[i]);
        }
    }
    
    private static void kernelAtax(int m, int n, double[][] A, double[] x, double[] y, double[] tmp) {
        for (int i = 0; i < n; i++) {
            y[i] = 0;
        }
        
        for (int i = 0; i < m; i++) {
            tmp[i] = 0.0;
            for (int j = 0; j < n; j++) {
                tmp[i] += A[i][j] * x[j];
            }
            for (int j = 0; j < n; j++) {
                y[j] += A[i][j] * tmp[i];
            }
        }
    }
    
    public static void main(String[] args) {
        int m = M;
        int n = N;
        
        double[][] A = new double[M][N];
        double[] x = new double[N];
        double[] y = new double[N];
        double[] tmp = new double[M];
        
        initArray(m, n, A, x);
        
        long startTime = System.currentTimeMillis();
        kernelAtax(m, n, A, x, y, tmp);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
        
        printArray(n, y);
    }
}
