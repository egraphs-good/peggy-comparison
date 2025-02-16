import java.util.Arrays;

public class Symm {
    private static final int M = 200;
    private static final int N = 240;
    
    private static void initArray(int m, int n, double[] alpha, double[] beta, double[][] C, double[][] A, double[][] B) {
        alpha[0] = 1.5;
        beta[0] = 1.2;
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = ((i + j) % 100) / (double) m;
                B[i][j] = ((n + i - j) % 100) / (double) m;
            }
        }
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j <= i; j++) {
                A[i][j] = ((i + j) % 100) / (double) m;
            }
            for (int j = i + 1; j < m; j++) {
                A[i][j] = -999;
            }
        }
    }
    
    private static void printArray(int m, int n, double[][] C) {
        System.out.println("C:");
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if ((i * m + j) % 20 == 0) System.out.println();
                System.out.printf("%.6f ", C[i][j]);
            }
        }
    }
    
    private static void kernelSymm(int m, int n, double alpha, double beta, double[][] C, double[][] A, double[][] B) {
        double temp2;
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                temp2 = 0;
                for (int k = 0; k < i; k++) {
                    C[k][j] += alpha * B[i][j] * A[i][k];
                    temp2 += B[k][j] * A[i][k];
                }
                C[i][j] = beta * C[i][j] + alpha * B[i][j] * A[i][i] + alpha * temp2;
            }
        }
    }
    
    public static void main(String[] args) {
        int m = M;
        int n = N;
        
        double[] alpha = new double[1];
        double[] beta = new double[1];
        double[][] C = new double[M][N];
        double[][] A = new double[M][M];
        double[][] B = new double[M][N];
        
        initArray(m, n, alpha, beta, C, A, B);
        
        long startTime = System.currentTimeMillis();
        kernelSymm(m, n, alpha[0], beta[0], C, A, B);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
        
        printArray(m, n, C);
    }
}
