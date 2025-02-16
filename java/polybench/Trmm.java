import java.util.Arrays;

public class Trmm {
    private static final int M = 200;
    private static final int N = 240;
    
    private static void initArray(int m, int n, double[] alpha, double[][] A, double[][] B) {
        alpha[0] = 1.5;
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < i; j++) {
                A[i][j] = ((i + j) % m) / (double) m;
            }
            A[i][i] = 1.0;
            for (int j = 0; j < n; j++) {
                B[i][j] = ((n + (i - j)) % n) / (double) n;
            }
        }
    }
    
    private static void printArray(int m, int n, double[][] B) {
        System.out.println("B:");
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if ((i * m + j) % 20 == 0) System.out.println();
                System.out.printf("%.6f ", B[i][j]);
            }
        }
    }
    
    private static void kernelTrmm(int m, int n, double alpha, double[][] A, double[][] B) {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = i + 1; k < m; k++) {
                    B[i][j] += A[k][i] * B[k][j];
                }
                B[i][j] = alpha * B[i][j];
            }
        }
    }
    
    public static void main(String[] args) {
        int m = M;
        int n = N;
        
        double[] alpha = new double[1];
        double[][] A = new double[M][M];
        double[][] B = new double[M][N];
        
        initArray(m, n, alpha, A, B);
        
        long startTime = System.currentTimeMillis();
        kernelTrmm(m, n, alpha[0], A, B);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
        
        printArray(m, n, B);
    }
}
