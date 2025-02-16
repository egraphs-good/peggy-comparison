import java.util.Arrays;

public class Syr2k {
    private static final int N = 240;
    private static final int M = 200;
    
    private static void initArray(int n, int m, double[] alpha, double[] beta, double[][] C, double[][] A, double[][] B) {
        alpha[0] = 1.5;
        beta[0] = 1.2;
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                A[i][j] = ((i * j + 1) % n) / (double) n;
                B[i][j] = ((i * j + 2) % m) / (double) m;
            }
        }
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = ((i * j + 3) % n) / (double) m;
            }
        }
    }
    
    private static void printArray(int n, double[][] C) {
        System.out.println("C:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if ((i * n + j) % 20 == 0) System.out.println();
                System.out.printf("%.6f ", C[i][j]);
            }
        }
    }
    
    private static void kernelSyr2k(int n, int m, double alpha, double beta, double[][] C, double[][] A, double[][] B) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                C[i][j] *= beta;
            }
            for (int k = 0; k < m; k++) {
                for (int j = 0; j <= i; j++) {
                    C[i][j] += A[j][k] * alpha * B[i][k] + B[j][k] * alpha * A[i][k];
                }
            }
        }
    }
    
    public static void main(String[] args) {
        int n = N;
        int m = M;
        
        double[] alpha = new double[1];
        double[] beta = new double[1];
        double[][] C = new double[N][N];
        double[][] A = new double[N][M];
        double[][] B = new double[N][M];
        
        initArray(n, m, alpha, beta, C, A, B);
        
        long startTime = System.currentTimeMillis();
        kernelSyr2k(n, m, alpha[0], beta[0], C, A, B);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
        
        printArray(n, C);
    }
}
