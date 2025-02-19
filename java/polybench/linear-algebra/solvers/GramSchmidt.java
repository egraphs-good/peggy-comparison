import java.util.Arrays;

public class GramSchmidt {
    public static final int M = 200;
    public static final int N = 240;

    public static void initArray(int m, int n, double[][] A, double[][] R, double[][] Q) {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = (((i * j) % m) / (double) m) * 100 + 10;
                Q[i][j] = 0.0;
            }
        }
        for (int i = 0; i < n; i++) {
            Arrays.fill(R[i], 0.0);
        }
    }

    public static void printArray(int m, int n, double[][] R, double[][] Q) {
        System.out.println("Matrix R:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if ((i * n + j) % 20 == 0) System.out.println();
                System.out.printf("%.6f ", R[i][j]);
                
            }
            // System.out.println();
        }

        System.out.println("Matrix Q:");
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if ((i * n + j) % 20 == 0) System.out.println();
                System.out.printf("%.6f ", Q[i][j]);
                
            }
            // System.out.println();
        }
    }

    public static void kernelGramSchmidt(int m, int n, double[][] A, double[][] R, double[][] Q) {
        for (int k = 0; k < n; k++) {
            double nrm = 0.0;
            for (int i = 0; i < m; i++) {
                nrm += A[i][k] * A[i][k];
            }
            R[k][k] = Math.sqrt(nrm);
            for (int i = 0; i < m; i++) {
                Q[i][k] = A[i][k] / R[k][k];
            }
            for (int j = k + 1; j < n; j++) {
                R[k][j] = 0.0;
                for (int i = 0; i < m; i++) {
                    R[k][j] += Q[i][k] * A[i][j];
                }
                for (int i = 0; i < m; i++) {
                    A[i][j] -= Q[i][k] * R[k][j];
                }
            }
        }
    }

    public static void main(String[] args) {
        int m = M;
        int n = N;
        double[][] A = new double[m][n];
        double[][] R = new double[n][n];
        double[][] Q = new double[m][n];

        initArray(m, n, A, R, Q);

        long startTime = System.nanoTime();
        kernelGramSchmidt(m, n, A, R, Q);
        long endTime = System.nanoTime();

        System.out.println("Execution time: " + (endTime - startTime) / 1e6 + " ms");
        
        printArray(m, n, R, Q);
    }
}
