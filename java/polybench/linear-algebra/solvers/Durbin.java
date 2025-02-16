import java.util.Arrays;

public class Durbin {
    public static final int N = 400;

    public static void initArray(int n, double[] r) {
        for (int i = 0; i < n; i++) {
            r[i] = (n + 1 - i);
        }
    }

    public static void printArray(int n, double[] y) {
        System.out.println("y:");
        for (int i = 0; i < n; i++) {
            if (i % 20 == 0) System.out.println();
            System.out.printf("%.2f ", y[i]);
        }
        System.out.println();
    }

    public static void kernelDurbin(int n, double[] r, double[] y) {
        double[] z = new double[N];
        double alpha, beta, sum;

        y[0] = -r[0];
        beta = 1.0;
        alpha = -r[0];

        for (int k = 1; k < n; k++) {
            beta = (1 - alpha * alpha) * beta;
            sum = 0.0;

            for (int i = 0; i < k; i++) {
                sum += r[k - i - 1] * y[i];
            }

            alpha = - (r[k] + sum) / beta;

            for (int i = 0; i < k; i++) {
                z[i] = y[i] + alpha * y[k - i - 1];
            }

            System.arraycopy(z, 0, y, 0, k);
            y[k] = alpha;
        }
    }

    public static void main(String[] args) {
        int n = N;

        double[] r = new double[N];
        double[] y = new double[N];

        initArray(n, r);

        long startTime = System.currentTimeMillis();
        kernelDurbin(n, r, y);
        long endTime = System.currentTimeMillis();

        System.out.println("Execution time: " + (endTime - startTime) + "ms");

        printArray(n, y);
    }
}
