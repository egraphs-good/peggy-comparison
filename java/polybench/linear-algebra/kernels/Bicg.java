import java.util.Arrays;

public class Bicg {
    public static final int M = 390;
    public static final int N = 410;

    public static void initArray(int m, int n, double[][] A, double[] r, double[] p) {
        for (int i = 0; i < m; i++) {
            p[i] = (double) (i % m) / m;
        }
        for (int i = 0; i < n; i++) {
            r[i] = (double) (i % n) / n;
            for (int j = 0; j < m; j++) {
                A[i][j] = (double) (i * (j + 1) % n) / n;
            }
        }
    }

    public static void printArray(int m, int n, double[] s, double[] q) {
        System.out.println("s:");
        for (int i = 0; i < m; i++) {
            if (i % 20 == 0) System.out.println();
            System.out.printf("%.2f ", s[i]);
        }

        System.out.println("\nq:");
        for (int i = 0; i < n; i++) {
            if (i % 20 == 0) System.out.println();
            System.out.printf("%.2f ", q[i]);
        }
    }

    public static void kernelBiCG(int m, int n, double[][] A, double[] s, double[] q, double[] p, double[] r) {
        Arrays.fill(s, 0);

        for (int i = 0; i < n; i++) {
            q[i] = 0.0;
            for (int j = 0; j < m; j++) {
                s[j] += r[i] * A[i][j];
                q[i] += A[i][j] * p[j];
            }
        }
    }

    public static void main(String[] args) {
        int m = M;
        int n = N;

        double[][] A = new double[N][M];
        double[] s = new double[M];
        double[] q = new double[N];
        double[] p = new double[M];
        double[] r = new double[N];

        initArray(m, n, A, r, p);

        long startTime = System.currentTimeMillis();
        kernelBiCG(m, n, A, s, q, p, r);
        long endTime = System.currentTimeMillis();

        System.out.println("Execution time: " + (endTime - startTime) + "ms");

        printArray(m, n, s, q);
    }
}
