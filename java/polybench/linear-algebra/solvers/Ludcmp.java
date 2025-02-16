import java.util.Arrays;

public class Ludcmp {
    public static final int N = 400;

    public static void initArray(int n, double[][] A, double[] b, double[] x, double[] y) {
        double fn = (double) n;
        for (int i = 0; i < n; i++) {
            x[i] = 0;
            y[i] = 0;
            b[i] = (i + 1) / fn / 2.0 + 4;
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                A[i][j] = (-j % n) / (double) n + 1;
            }
            for (int j = i + 1; j < n; j++) {
                A[i][j] = 0;
            }
            A[i][i] = 1;
        }
        
        double[][] B = new double[n][n];
        for (int t = 0; t < n; t++) {
            for (int r = 0; r < n; r++) {
                for (int s = 0; s < n; s++) {
                    B[r][s] += A[r][t] * A[s][t];
                }
            }
        }
        for (int r = 0; r < n; r++) {
            System.arraycopy(B[r], 0, A[r], 0, n);
        }
    }

    public static void printArray(int n, double[] x) {
        System.out.println("Array x:");
        for (int i = 0; i < n; i++) {
            if (i % 20 == 0) System.out.println();
            System.out.printf("%.2f ", x[i]);
        }
        System.out.println();
    }

    public static void kernelLudcmp(int n, double[][] A, double[] b, double[] x, double[] y) {
        double w;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                w = A[i][j];
                for (int k = 0; k < j; k++) {
                    w -= A[i][k] * A[k][j];
                }
                A[i][j] = w / A[j][j];
            }
            for (int j = i; j < n; j++) {
                w = A[i][j];
                for (int k = 0; k < i; k++) {
                    w -= A[i][k] * A[k][j];
                }
                A[i][j] = w;
            }
        }
        for (int i = 0; i < n; i++) {
            w = b[i];
            for (int j = 0; j < i; j++) {
                w -= A[i][j] * y[j];
            }
            y[i] = w;
        }
        for (int i = n - 1; i >= 0; i--) {
            w = y[i];
            for (int j = i + 1; j < n; j++) {
                w -= A[i][j] * x[j];
            }
            x[i] = w / A[i][i];
        }
    }

    public static void main(String[] args) {
        int n = N;
        double[][] A = new double[n][n];
        double[] b = new double[n];
        double[] x = new double[n];
        double[] y = new double[n];

        initArray(n, A, b, x, y);

        long startTime = System.nanoTime();
        kernelLudcmp(n, A, b, x, y);
        long endTime = System.nanoTime();

        System.out.println("Execution time: " + (endTime - startTime) / 1e6 + " ms");
        
        printArray(n, x);
    }
}
