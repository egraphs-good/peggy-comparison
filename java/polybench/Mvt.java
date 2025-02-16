import java.util.Arrays;

public class Mvt {
    private static final int N = 400;

    private static void initArray(int n, double[] x1, double[] x2, double[] y1, double[] y2, double[][] A) {
        for (int i = 0; i < n; i++) {
            x1[i] = (double) (i % n) / n;
            x2[i] = (double) ((i + 1) % n) / n;
            y1[i] = (double) ((i + 3) % n) / n;
            y2[i] = (double) ((i + 4) % n) / n;
            for (int j = 0; j < n; j++) {
                A[i][j] = (double) (i * j % n) / n;
            }
        }
    }

    private static void printArray(int n, double[] x1, double[] x2) {
        System.out.println("x1:");
        for (int i = 0; i < n; i++) {
            if (i % 20 == 0) System.out.println();
            System.out.printf("%.6f ", x1[i]);
        }

        System.out.println("\nx2:");
        for (int i = 0; i < n; i++) {
            if (i % 20 == 0) System.out.println();
            System.out.printf("%.6f ", x2[i]);
        }
    }

    private static void kernelMvt(int n, double[] x1, double[] x2, double[] y1, double[] y2, double[][] A) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                x1[i] += A[i][j] * y1[j];
            }
        }
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                x2[i] += A[j][i] * y2[j];
            }
        }
    }

    public static void main(String[] args) {
        int n = N;

        double[][] A = new double[N][N];
        double[] x1 = new double[N];
        double[] x2 = new double[N];
        double[] y1 = new double[N];
        double[] y2 = new double[N];

        initArray(n, x1, x2, y1, y2, A);

        long startTime = System.currentTimeMillis();
        kernelMvt(n, x1, x2, y1, y2, A);
        long endTime = System.currentTimeMillis();

        System.out.println("Execution time: " + (endTime - startTime) + "ms");

        printArray(n, x1, x2);
    }
}
