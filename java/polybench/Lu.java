import java.util.Arrays;

public class Lu {
    private static final int N = 400; // Adjust size as needed

    public static void initArray(int n, double[][] A) {
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
        for (int r = 0; r < n; r++) {
            Arrays.fill(B[r], 0);
        }
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

    public static void printArray(int n, double[][] A) {
        System.out.println("Matrix A:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if ((i * n + j) % 20 == 0) System.out.println();
                System.out.printf("%.6f ", A[i][j]);
                
            }
            // System.out.println();
        }
    }

    public static void kernelLU(int n, double[][] A) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                for (int k = 0; k < j; k++) {
                    A[i][j] -= A[i][k] * A[k][j];
                }
                A[i][j] /= A[j][j];
            }
            for (int j = i; j < n; j++) {
                for (int k = 0; k < i; k++) {
                    A[i][j] -= A[i][k] * A[k][j];
                }
            }
        }
    }

    public static void main(String[] args) {
        int n = N;
        double[][] A = new double[n][n];

        initArray(n, A);

        long startTime = System.nanoTime();
        kernelLU(n, A);
        long endTime = System.nanoTime();

        System.out.println("Execution time: " + (endTime - startTime) / 1e6 + " ms");
        
        printArray(n, A);
    }
}
