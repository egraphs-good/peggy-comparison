/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * Copyright (c) 1997,1998 Sun Microsystems, Inc. All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */

/**
 * LU matrix factorization. (Based on TNT implementation.)
 * Decomposes a matrix A into a triangular lower triangular
 * factor (L) and an upper triangular factor (U) such that
 * A = L*U. By convnetion, the main diagonal of L consists
 * of 1's so that L and U can be stored compactly in
 * a NxN matrix.
 *
 */
public class LU {
    int id;

    public LU(int id) {
        this.id = id;
    }

    /**
     * Returns a <em>copy</em> of the compact LU factorization.
     * (useful mainly for debugging.)
     *
     * Retrieves the compact LU factorization. The U factor
     * is stored in the upper triangular portion, and the L
     * factor is stored in the lower triangular portion.
     * The main diagonal of L consists (by convention) of
     * ones, and is not explicitly stored.
     */
    public static void main(int id) {
        LU lu = new LU(id);
        lu.run();
    }

    public static final double num_flops(int N) {
        double Nd = (double) N;
        return (2.0 * Nd * Nd * Nd / 3.0);
    }

    protected static double[] new_copy(double x[]) {
        int N = x.length;
        double T[] = new double[N];
        for (int i = 0; i < N; i++)
            T[i] = x[i];
        return T;
    }

    protected static double[][] new_copy(double A[][]) {
        int M = A.length;
        int N = A[0].length;
        double T[][] = new double[M][N];
        for (int i = 0; i < M; i++) {
            double Ti[] = T[i];
            double Ai[] = A[i];
            for (int j = 0; j < N; j++)
                Ti[j] = Ai[j];
        }
        return T;
    }

    public static int[] new_copy(int x[]) {
        int N = x.length;
        int T[] = new int[N];
        for (int i = 0; i < N; i++)
            T[i] = x[i];
        return T;
    }

    protected static final void insert_copy(double B[][], double A[][]) {
        int M = A.length;
        int N = A[0].length;
        int remainder = N & 3;
        for (int i = 0; i < M; i++) {
            double Bi[] = B[i];
            double Ai[] = A[i];
            for (int j = 0; j < remainder; j++)
                Bi[j] = Ai[j];
            for (int j = remainder; j < N; j += 4) {
                Bi[j] = Ai[j];
                Bi[j + 1] = Ai[j + 1];
                Bi[j + 2] = Ai[j + 2];
                Bi[j + 3] = Ai[j + 3];
            }
        }
    }

    public double[][] getLU() {
        return new_copy(LU_);
    }

    /**
     * Returns a <em>copy</em> of the pivot vector.
     *
     * @return the pivot vector used in obtaining the
     *         LU factorzation. Subsequent solutions must
     *         permute the right-hand side by this vector.
     *
     */
    public int[] getPivot() {
        return new_copy(pivot_);
    }

    /**
     * Initalize LU factorization from matrix.
     *
     * @param A (in) the matrix to associate with this
     *          factorization.
     */
    /*
     * public LU( double A[][] )
     * {
     * int M = A.length;
     * int N = A[0].length;
     * 
     * 
     * LU_ = new double[M][N];
     * 
     * insert_copy(LU_, A);
     * 
     * 
     * pivot_ = new int[M];
     * 
     * factor(LU_, pivot_);
     * }
     */
    /**
     * Solve a linear system, with pre-computed factorization.
     *
     * @param b (in) the right-hand side.
     * @return solution vector.
     */
    public double[] solve(double b[]) {
        double x[] = new_copy(b);
        solve(LU_, pivot_, x);
        return x;
    }

    /**
     * LU factorization (in place).
     *
     * @param A     (in/out) On input, the matrix to be factored.
     *              On output, the compact LU factorization.
     *
     * @param pivot (out) The pivot vector records the
     *              reordering of the rows of A during factorization.
     *
     * @return 0, if OK, nozero value, othewise.
     */
    public int factor(double A[][], int pivot[]) {
        int N = A.length;
        int M = A[0].length;
        int minMN = Math.min(M, N);
        for (int j = 0; j < minMN; j++) {
            int jp = j;
            double t = Math.abs(A[j][j]);
            for (int i = j + 1; i < M; i++) {
                double ab = Math.abs(A[i][j]);
                if (ab > t) {
                    jp = i;
                    t = ab;
                }
            }
            pivot[j] = jp;
            if (A[jp][j] == 0)
                return 1;
            if (jp != j) {
                double tA[] = A[j];
                A[j] = A[jp];
                A[jp] = tA;
            }
            if (j < M - 1) {
                double recp = 1.0 / A[j][j];
                for (int k = j + 1; k < M; k++)
                    A[k][j] *= recp;
            }
            if (j < minMN - 1) {
                for (int ii = j + 1; ii < M; ii++) {
                    double Aii[] = A[ii];
                    double Aj[] = A[j];
                    double AiiJ = Aii[j];
                    for (int jj = j + 1; jj < N; jj++)
                        Aii[jj] -= AiiJ * Aj[jj];
                }
            }
        }
        return 0;
    }

    /**
     * Solve a linear system, using a prefactored matrix
     * in LU form.
     *
     * @param LU  (in) the factored matrix in LU form.
     * @param pvt (in) the pivot vector which lists
     *            the reordering used during the factorization
     *            stage.
     * @param b   (in/out) On input, the right-hand side.
     *            On output, the solution vector.
     */
    public void solve(double LU[][], int pvt[], double b[]) {
        int M = LU.length;
        int N = LU[0].length;
        int ii = 0;
        for (int i = 0; i < M; i++) {
            int ip = pvt[i];
            double sum = b[ip];
            b[ip] = b[i];
            if (ii == 0)
                for (int j = ii; j < i; j++)
                    sum -= LU[i][j] * b[j];
            else if (sum == 0.0)
                ii = i;
            b[i] = sum;
        }
        for (int i = N - 1; i >= 0; i--) {
            double sum = b[i];
            for (int j = i + 1; j < N; j++)
                sum -= LU[i][j] * b[j];
            b[i] = sum / LU[i][i];
        }
    }

    public double LU_[][];
    public int pivot_[];

    public double measureLU(int N, double min_time, Random R) {
        double A[][] = kernel.RandomMatrix(N, N, R);
        double lu[][] = new double[N][N];
        int pivot[] = new int[N];
        Stopwatch Q = new Stopwatch();
        int cycles = 2;
        Q.start();
        for (int i = 0; i < cycles; i++) {
            kernel.CopyMatrix(lu, A);
            factor(lu, pivot);
        }
        Q.stop();
        double b[] = kernel.RandomVector(N, R);
        double x[] = kernel.NewVectorCopy(b);
        solve(lu, pivot, x);
        final double EPS = 1.0e-12;
        kernel.checkResults(kernel.CURRENT_LU_RESULT,
                "" + kernel.normabs(b, kernel.matvec(A, x)), id);
        if (kernel.normabs(b, kernel.matvec(A, x)) / N > EPS)
            return 0.0;
        return LU.num_flops(N) * cycles / Q.read() * 1.0e-6;
    }

    public void run() {
        double min_time = Constants.RESOLUTION_DEFAULT;
        int LU_size = kernel.CURRENT_LU_SIZE;
        double res = 0.0;
        Random R = new Random(Constants.RANDOM_SEED);
        res = measureLU(LU_size, min_time, R);
    }
}
