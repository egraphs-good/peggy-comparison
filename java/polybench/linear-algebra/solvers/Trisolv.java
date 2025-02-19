import java.util.Arrays;

public class Trisolv {
    public static final int N = 400;

    public static void initArray(int n, double[][] L, double[] x, double[] b) {
        for (int i = 0; i < n; i++) {
            x[i] = -999;
            b[i] = i;
            for (int j = 0; j <= i; j++) {
                L[i][j] = (double) (i + n - j + 1) * 2 / n;
            }
        }
    }

    public static void printArray(int n, double[] x) {
        System.out.println("Array x:");
        for (int i = 0; i < n; i++) {
            if (i % 20 == 0) System.out.println();
            System.out.printf("%.6f ", x[i]);
        }
        System.out.println();
    }

    public static void kernelTriSolv(int n, double[][] L, double[] x, double[] b) {
        for (int i = 0; i < n; i++) {
            x[i] = b[i];
            for (int j = 0; j < i; j++) {
                x[i] -= L[i][j] * x[j];
            }
            x[i] = x[i] / L[i][i];
        }
    }

    public static void main(String[] args) {
        int n = N;
        double[][] L = new double[n][n];
        double[] x = new double[n];
        double[] b = new double[n];

        initArray(n, L, x, b);

        long startTime = System.nanoTime();
        kernelTriSolv(n, L, x, b);
        long endTime = System.nanoTime();

        System.out.println("Execution time: " + (endTime - startTime) / 1e6 + " ms");
        
        printArray(n, x);
    }
}
