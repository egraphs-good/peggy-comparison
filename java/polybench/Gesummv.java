import java.util.Arrays;

public class Gesummv {
    private static final int N = 250;
    
    private static void initArray(int n, double[] alpha, double[] beta, double[][] A, double[][] B, double[] x) {
        alpha[0] = 1.5;
        beta[0] = 1.2;
        
        for (int i = 0; i < n; i++) {
            x[i] = (double) (i % n) / n;
            for (int j = 0; j < n; j++) {
                A[i][j] = (double) ((i * j + 1) % n) / n;
                B[i][j] = (double) ((i * j + 2) % n) / n;
            }
        }
    }
    
    private static void printArray(int n, double[] y) {
        System.out.println("y:");
        for (int i = 0; i < n; i++) {
            if (i % 20 == 0) System.out.println();
            System.out.printf("%.6f ", y[i]);
        }
    }
    
    private static void kernelGesummv(int n, double alpha, double beta, double[][] A, double[][] B, double[] tmp, double[] x, double[] y) {
        for (int i = 0; i < n; i++) {
            tmp[i] = 0.0;
            y[i] = 0.0;
            for (int j = 0; j < n; j++) {
                tmp[i] += A[i][j] * x[j];
                y[i] += B[i][j] * x[j];
            }
            y[i] = alpha * tmp[i] + beta * y[i];
        }
    }
    
    public static void main(String[] args) {
        int n = N;
        
        double[] alpha = new double[1];
        double[] beta = new double[1];
        double[][] A = new double[N][N];
        double[][] B = new double[N][N];
        double[] tmp = new double[N];
        double[] x = new double[N];
        double[] y = new double[N];
        
        initArray(n, alpha, beta, A, B, x);
        
        long startTime = System.currentTimeMillis();
        kernelGesummv(n, alpha[0], beta[0], A, B, tmp, x, y);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
        
        printArray(n, y);
    }
}
