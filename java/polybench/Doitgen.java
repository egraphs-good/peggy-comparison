import java.util.Arrays;

public class Doitgen {
    private static final int NR = 50;
    private static final int NQ = 40;
    private static final int NP = 60;

    private static void initArray(int nr, int nq, int np, double[][][] A, double[][] C4) {
        for (int i = 0; i < nr; i++) {
            for (int j = 0; j < nq; j++) {
                for (int k = 0; k < np; k++) {
                    A[i][j][k] = (double) ((i * j + k) % np) / np;
                }
            }
        }

        for (int i = 0; i < np; i++) {
            for (int j = 0; j < np; j++) {
                C4[i][j] = (double) (i * j % np) / np;
            }
        }
    }

    private static void printArray(int nr, int nq, int np, double[][][] A) {
        System.out.println("A:");
        for (int i = 0; i < nr; i++) {
            for (int j = 0; j < nq; j++) {
                for (int k = 0; k < np; k++) {
                    if ((i * nq * np + j * np + k) % 20 == 0) System.out.println();
                    System.out.printf("%.6f ", A[i][j][k]);
                }
            }
        }
    }

    private static void kernelDoitgen(int nr, int nq, int np, double[][][] A, double[][] C4, double[] sum) {
        for (int r = 0; r < nr; r++) {
            for (int q = 0; q < nq; q++) {
                Arrays.fill(sum, 0.0);

                for (int p = 0; p < np; p++) {
                    for (int s = 0; s < np; s++) {
                        sum[p] += A[r][q][s] * C4[s][p];
                    }
                }

                for (int p = 0; p < np; p++) {
                    A[r][q][p] = sum[p];
                }
            }
        }
    }

    public static void main(String[] args) {
        int nr = NR;
        int nq = NQ;
        int np = NP;

        double[][][] A = new double[NR][NQ][NP];
        double[][] C4 = new double[NP][NP];
        double[] sum = new double[NP];

        initArray(nr, nq, np, A, C4);

        long startTime = System.currentTimeMillis();
        kernelDoitgen(nr, nq, np, A, C4, sum);
        long endTime = System.currentTimeMillis();

        System.out.println("Execution time: " + (endTime - startTime) + "ms");

        printArray(nr, nq, np, A);
    }
}
