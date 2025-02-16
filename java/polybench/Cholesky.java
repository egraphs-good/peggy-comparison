import java.util.Arrays;

public class Cholesky {
    private static final int N = 400;

    private static void initArray(int n, double[][] A) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                A[i][j] = (-j % n) / (double) n + 1;
            }
            for (int j = i + 1; j < n; j++) {
                A[i][j] = 0;
            }
            A[i][i] = 1;
        }

        // Make the matrix positive semi-definite
        double[][] B = new double[n][n];
        for (int t = 0; t < n; t++) {
            for (int r = 0; r < n; r++) {
                for (int s = 0; s < n; s++) {
                    B[r][s] += A[r][t] * A[s][t];
                }
            }
        }

        for (int r = 0; r < n; r++) {
            for (int s = 0; s < n; s++) {
                A[r][s] = B[r][s];
            }
        }
    }

    private static void printArray(int n, double[][] A) {
        System.out.println("A:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                if ((i * n + j) % 20 == 0) System.out.println();
                System.out.printf("%.6f ", A[i][j]);
            }
        }
        System.out.println();
    }

    private static void kernelCholesky(int n, double[][] A) {
        for (int i = 0; i < n; i++) {
            // j < i
            for (int j = 0; j < i; j++) {
                for (int k = 0; k < j; k++) {
                    A[i][j] -= A[i][k] * A[j][k];
                }
                A[i][j] /= A[j][j];
            }
            // i == j case
            for (int k = 0; k < i; k++) {
                A[i][i] -= A[i][k] * A[i][k];
            }
            A[i][i] = Math.sqrt(A[i][i]);
        }
    }

    public static void main(String[] args) {
        int n = N;

        double[][] A = new double[N][N];

        initArray(n, A);

        long startTime = System.currentTimeMillis();
        kernelCholesky(n, A);
        long endTime = System.currentTimeMillis();

        System.out.println("Execution time: " + (endTime - startTime) + "ms");

        printArray(n, A);
    }
}
