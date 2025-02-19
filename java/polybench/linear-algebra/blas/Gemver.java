import java.util.Arrays;

public class Gemver {
    public static final int N = 400;
    
    public static void initArray(int n, double[] alpha, double[] beta, double[][] A, double[] u1, double[] v1, double[] u2, double[] v2, double[] w, double[] x, double[] y, double[] z) {
        alpha[0] = 1.5;
        beta[0] = 1.2;
        double fn = (double) n;
        
        for (int i = 0; i < n; i++) {
            u1[i] = i;
            u2[i] = ((i + 1) / fn) / 2.0;
            v1[i] = ((i + 1) / fn) / 4.0;
            v2[i] = ((i + 1) / fn) / 6.0;
            y[i] = ((i + 1) / fn) / 8.0;
            z[i] = ((i + 1) / fn) / 9.0;
            x[i] = 0.0;
            w[i] = 0.0;
            for (int j = 0; j < n; j++) {
                A[i][j] = (i * j % n) / (double) n;
            }
        }
    }
    
    public static void printArray(int n, double[] w) {
        System.out.println("w:");
        for (int i = 0; i < n; i++) {
            if (i % 20 == 0) System.out.println();
            System.out.printf("%.2f ", w[i]);
        }
    }
    
    public static void kernelGemver(int n, double alpha, double beta, double[][] A, double[] u1, double[] v1, double[] u2, double[] v2, double[] w, double[] x, double[] y, double[] z) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = A[i][j] + u1[i] * v1[j] + u2[i] * v2[j];
            }
        }
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                x[i] = x[i] + beta * A[j][i] * y[j];
            }
        }
        
        for (int i = 0; i < n; i++) {
            x[i] = x[i] + z[i];
        }
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                w[i] = w[i] + alpha * A[i][j] * x[j];
            }
        }
    }
    
    public static void main(String[] args) {
        int n = N;
        
        double[] alpha = new double[1];
        double[] beta = new double[1];
        double[][] A = new double[N][N];
        double[] u1 = new double[N];
        double[] v1 = new double[N];
        double[] u2 = new double[N];
        double[] v2 = new double[N];
        double[] w = new double[N];
        double[] x = new double[N];
        double[] y = new double[N];
        double[] z = new double[N];
        
        initArray(n, alpha, beta, A, u1, v1, u2, v2, w, x, y, z);
        
        long startTime = System.currentTimeMillis();
        kernelGemver(n, alpha[0], beta[0], A, u1, v1, u2, v2, w, x, y, z);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
        
        printArray(n, w);
    }
}
