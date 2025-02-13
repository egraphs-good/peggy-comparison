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
        double min_time = Constants.RESOLUTION_DEFAULT;
        int LU_size = kernel.CURRENT_LU_SIZE;
        double res = 0.0;
        Random R = new Random(Constants.RANDOM_SEED);
        double result = 0.0;
        double A1[][] = new double[LU_size][LU_size];
        for (int i2 = 0; i2 < LU_size; i2++)
            for (int j1 = 0; j1 < LU_size; j1++) {
                double result1;
                synchronized (R) {
                    int k;
                    double nextValue;
                    k = R.m[R.i] - R.m[R.j];
                    if (k < 0)
                        k += Random.m1;
                    R.m[R.j] = k;
                    if (R.i == 0)
                        R.i = 16;
                    else
                        R.i--;
                    if (R.j == 0)
                        R.j = 16;
                    else
                        R.j--;
                    if (R.haveRange)
                        result1 = R.left + R.dm1 * (double) k * R.width;
                    else
                        result1 = R.dm1 * (double) k;
                }
                A1[i2][j1] = result1;
            }
        double A[][] = A1;
        double lu1[][] = new double[LU_size][LU_size];
        int pivot[] = new int[LU_size];
        Stopwatch Q = new Stopwatch();
        int cycles = 2;
        if (!Q.running) {
            Q.running = true;
            Q.total = 0.0;
            Q.last_time = (System.currentTimeMillis() * 0.001);
        }
        for (int i = 0; i < cycles; i++) {
            int M = A.length;
            int N1 = A[0].length;
            int remainder = N1 & 3;
            for (int i1 = 0; i1 < M; i1++) {
                double Bi[] = lu1[i1];
                double Ai[] = A[i1];
                for (int j = 0; j < remainder; j++)
                    Bi[j] = Ai[j];
                for (int j = remainder; j < N1; j += 4) {
                    Bi[j] = Ai[j];
                    Bi[j + 1] = Ai[j + 1];
                    Bi[j + 2] = Ai[j + 2];
                    Bi[j + 3] = Ai[j + 3];
                }
            }
            int N2 = lu1.length;
            int M1 = lu1[0].length;
            int minMN = Math.min(M1, N2);
            for (int j = 0; j < minMN; j++) {
                int jp = j;
                double t = Math.abs(lu1[j][j]);
                for (int i1 = j + 1; i1 < M1; i1++) {
                    double ab = Math.abs(lu1[i1][j]);
                    if (ab > t) {
                        jp = i1;
                        t = ab;
                    }
                }
                pivot[j] = jp;
                if (lu1[jp][j] == 0) {
                    break;
                }
                if (jp != j) {
                    double tA[] = lu1[j];
                    lu1[j] = lu1[jp];
                    lu1[jp] = tA;
                }
                if (j < M1 - 1) {
                    double recp = 1.0 / lu1[j][j];
                    for (int k = j + 1; k < M1; k++)
                        lu1[k][j] *= recp;
                }
                if (j < minMN - 1) {
                    for (int ii = j + 1; ii < M1; ii++) {
                        double Aii[] = lu1[ii];
                        double Aj[] = lu1[j];
                        double AiiJ = Aii[j];
                        for (int jj = j + 1; jj < N2; jj++)
                            Aii[jj] -= AiiJ * Aj[jj];
                    }
                }
            }
        }
        if (Q.running) {
            Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
            Q.running = false;
        }
        double A2[] = new double[LU_size];
        for (int i2 = 0; i2 < LU_size; i2++) {
            double result1;
            synchronized (R) {
                int k;
                double nextValue;
                k = R.m[R.i] - R.m[R.j];
                if (k < 0)
                    k += Random.m1;
                R.m[R.j] = k;
                if (R.i == 0)
                    R.i = 16;
                else
                    R.i--;
                if (R.j == 0)
                    R.j = 16;
                else
                    R.j--;
                if (R.haveRange)
                    result1 = R.left + R.dm1 * (double) k * R.width;
                else
                    result1 = R.dm1 * (double) k;
            }
            A2[i2] = result1;
        }
        double b[] = A2;
        int N1 = b.length;
        double y[] = new double[N1];
        for (int i = 0; i < N1; i++)
            y[i] = b[i];
        double x[] = y;
        int M2 = lu1.length;
        int N8 = lu1[0].length;
        int ii = 0;
        for (int i3 = 0; i3 < M2; i3++) {
            int ip = pivot[i3];
            double sum4 = x[ip];
            x[ip] = x[i3];
            if (ii == 0)
                for (int j2 = ii; j2 < i3; j2++)
                    sum4 -= lu1[i3][j2] * x[j2];
            else if (sum4 == 0.0)
                ii = i3;
            x[i3] = sum4;
        }
        for (int i3 = N8 - 1; i3 >= 0; i3--) {
            double sum4 = x[i3];
            for (int j2 = i3 + 1; j2 < N8; j2++)
                sum4 -= lu1[i3][j2] * x[j2];
            x[i3] = sum4 / lu1[i3][i3];
        }
        final double EPS = 1.0e-12;
        int N5 = x.length;
        double y4[] = new double[N5];
        int M1 = A.length;
        int N7 = A[0].length;
        for (int i2 = 0; i2 < M1; i2++) {
            double sum3 = 0.0;
            double Ai1[] = A[i2];
            for (int j1 = 0; j1 < N7; j1++)
                sum3 += Ai1[j1] * x[j1];
            y4[i2] = sum3;
        }
        double[] y2 = y4;
        int N3 = b.length;
        double sum1 = 0.0;
        for (int i1 = 0; i1 < N3; i1++)
            sum1 += Math.abs(b[i1] - y2[i1]);
        if (kernel.CURRENT_LU_RESULT.equals("" + sum1)) {
            if (lu.id == 1) {
                System.out.println(Constants.VERIFICATION_PASSED_MESSAGE);
            }
        } else {
            System.out.println(Constants.VERIFICATION_FAILED_MESSAGE
                    + "The result is " + "" + sum1 + "  instead of " + kernel.CURRENT_LU_RESULT);
        }
        int N4 = x.length;
        double y3[] = new double[N4];
        int M = A.length;
        int N6 = A[0].length;
        for (int i1 = 0; i1 < M; i1++) {
            double sum2 = 0.0;
            double Ai[] = A[i1];
            for (int j = 0; j < N6; j++)
                sum2 += Ai[j] * x[j];
            y3[i1] = sum2;
        }
        double[] y1 = y3;
        int N2 = b.length;
        double sum = 0.0;
        for (int i = 0; i < N2; i++)
            sum += Math.abs(b[i] - y1[i]);
        if (!(sum / LU_size > EPS)) {
            double Nd = (double) LU_size;
            if (Q.running) {
                Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
                Q.last_time = (System.currentTimeMillis() * 0.001);
            }
            result = (2.0 * Nd * Nd * Nd / 3.0) * cycles / Q.total * 1.0e-6;
        }
        res = result;
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
        int M = LU_.length;
        int N = LU_[0].length;
        double T[][] = new double[M][N];
        for (int i = 0; i < M; i++) {
            double Ti[] = T[i];
            double Ai[] = LU_[i];
            for (int j = 0; j < N; j++)
                Ti[j] = Ai[j];
        }
        return T;
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
        int N = pivot_.length;
        int T[] = new int[N];
        for (int i = 0; i < N; i++)
            T[i] = pivot_[i];
        return T;
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
        int N = b.length;
        double T[] = new double[N];
        for (int i = 0; i < N; i++)
            T[i] = b[i];
        double x[] = T;
        int M = LU_.length;
        int N1 = LU_[0].length;
        int ii = 0;
        for (int i = 0; i < M; i++) {
            int ip = pivot_[i];
            double sum = x[ip];
            x[ip] = x[i];
            if (ii == 0)
                for (int j = ii; j < i; j++)
                    sum -= LU_[i][j] * x[j];
            else if (sum == 0.0)
                ii = i;
            x[i] = sum;
        }
        for (int i = N1 - 1; i >= 0; i--) {
            double sum = x[i];
            for (int j = i + 1; j < N1; j++)
                sum -= LU_[i][j] * x[j];
            x[i] = sum / LU_[i][i];
        }
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
        double A1[][] = new double[N][N];
        for (int i2 = 0; i2 < N; i2++)
            for (int j1 = 0; j1 < N; j1++) {
                double result;
                synchronized (R) {
                    int k;
                    double nextValue;
                    k = R.m[R.i] - R.m[R.j];
                    if (k < 0)
                        k += Random.m1;
                    R.m[R.j] = k;
                    if (R.i == 0)
                        R.i = 16;
                    else
                        R.i--;
                    if (R.j == 0)
                        R.j = 16;
                    else
                        R.j--;
                    if (R.haveRange)
                        result = R.left + R.dm1 * (double) k * R.width;
                    else
                        result = R.dm1 * (double) k;
                }
                A1[i2][j1] = result;
            }
        double A[][] = A1;
        double lu[][] = new double[N][N];
        int pivot[] = new int[N];
        Stopwatch Q = new Stopwatch();
        int cycles = 2;
        if (!Q.running) {
            Q.running = true;
            Q.total = 0.0;
            Q.last_time = (System.currentTimeMillis() * 0.001);
        }
        for (int i = 0; i < cycles; i++) {
            int M = A.length;
            int N1 = A[0].length;
            int remainder = N1 & 3;
            for (int i1 = 0; i1 < M; i1++) {
                double Bi[] = lu[i1];
                double Ai[] = A[i1];
                for (int j = 0; j < remainder; j++)
                    Bi[j] = Ai[j];
                for (int j = remainder; j < N1; j += 4) {
                    Bi[j] = Ai[j];
                    Bi[j + 1] = Ai[j + 1];
                    Bi[j + 2] = Ai[j + 2];
                    Bi[j + 3] = Ai[j + 3];
                }
            }
            int N2 = lu.length;
            int M1 = lu[0].length;
            int minMN = Math.min(M1, N2);
            for (int j = 0; j < minMN; j++) {
                int jp = j;
                double t = Math.abs(lu[j][j]);
                for (int i1 = j + 1; i1 < M1; i1++) {
                    double ab = Math.abs(lu[i1][j]);
                    if (ab > t) {
                        jp = i1;
                        t = ab;
                    }
                }
                pivot[j] = jp;
                if (lu[jp][j] == 0) {
                    break;
                }
                if (jp != j) {
                    double tA[] = lu[j];
                    lu[j] = lu[jp];
                    lu[jp] = tA;
                }
                if (j < M1 - 1) {
                    double recp = 1.0 / lu[j][j];
                    for (int k = j + 1; k < M1; k++)
                        lu[k][j] *= recp;
                }
                if (j < minMN - 1) {
                    for (int ii = j + 1; ii < M1; ii++) {
                        double Aii[] = lu[ii];
                        double Aj[] = lu[j];
                        double AiiJ = Aii[j];
                        for (int jj = j + 1; jj < N2; jj++)
                            Aii[jj] -= AiiJ * Aj[jj];
                    }
                }
            }
        }
        if (Q.running) {
            Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
            Q.running = false;
        }
        double A2[] = new double[N];
        for (int i2 = 0; i2 < N; i2++) {
            double result;
            synchronized (R) {
                int k;
                double nextValue;
                k = R.m[R.i] - R.m[R.j];
                if (k < 0)
                    k += Random.m1;
                R.m[R.j] = k;
                if (R.i == 0)
                    R.i = 16;
                else
                    R.i--;
                if (R.j == 0)
                    R.j = 16;
                else
                    R.j--;
                if (R.haveRange)
                    result = R.left + R.dm1 * (double) k * R.width;
                else
                    result = R.dm1 * (double) k;
            }
            A2[i2] = result;
        }
        double b[] = A2;
        int N1 = b.length;
        double y[] = new double[N1];
        for (int i = 0; i < N1; i++)
            y[i] = b[i];
        double x[] = y;
        int M2 = lu.length;
        int N8 = lu[0].length;
        int ii = 0;
        for (int i3 = 0; i3 < M2; i3++) {
            int ip = pivot[i3];
            double sum4 = x[ip];
            x[ip] = x[i3];
            if (ii == 0)
                for (int j2 = ii; j2 < i3; j2++)
                    sum4 -= lu[i3][j2] * x[j2];
            else if (sum4 == 0.0)
                ii = i3;
            x[i3] = sum4;
        }
        for (int i3 = N8 - 1; i3 >= 0; i3--) {
            double sum4 = x[i3];
            for (int j2 = i3 + 1; j2 < N8; j2++)
                sum4 -= lu[i3][j2] * x[j2];
            x[i3] = sum4 / lu[i3][i3];
        }
        final double EPS = 1.0e-12;
        int N5 = x.length;
        double y4[] = new double[N5];
        int M1 = A.length;
        int N7 = A[0].length;
        for (int i2 = 0; i2 < M1; i2++) {
            double sum3 = 0.0;
            double Ai1[] = A[i2];
            for (int j1 = 0; j1 < N7; j1++)
                sum3 += Ai1[j1] * x[j1];
            y4[i2] = sum3;
        }
        double[] y2 = y4;
        int N3 = b.length;
        double sum1 = 0.0;
        for (int i1 = 0; i1 < N3; i1++)
            sum1 += Math.abs(b[i1] - y2[i1]);
        if (kernel.CURRENT_LU_RESULT.equals("" + sum1)) {
            if (id == 1) {
                System.out.println(Constants.VERIFICATION_PASSED_MESSAGE);
            }
        } else {
            System.out.println(Constants.VERIFICATION_FAILED_MESSAGE
                    + "The result is " + "" + sum1 + "  instead of " + kernel.CURRENT_LU_RESULT);
        }
        int N4 = x.length;
        double y3[] = new double[N4];
        int M = A.length;
        int N6 = A[0].length;
        for (int i1 = 0; i1 < M; i1++) {
            double sum2 = 0.0;
            double Ai[] = A[i1];
            for (int j = 0; j < N6; j++)
                sum2 += Ai[j] * x[j];
            y3[i1] = sum2;
        }
        double[] y1 = y3;
        int N2 = b.length;
        double sum = 0.0;
        for (int i = 0; i < N2; i++)
            sum += Math.abs(b[i] - y1[i]);
        if (sum / N > EPS)
            return 0.0;
        double Nd = (double) N;
        if (Q.running) {
            Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
            Q.last_time = (System.currentTimeMillis() * 0.001);
        }
        return (2.0 * Nd * Nd * Nd / 3.0) * cycles / Q.total * 1.0e-6;
    }

    public void run() {
        double min_time = Constants.RESOLUTION_DEFAULT;
        int LU_size = kernel.CURRENT_LU_SIZE;
        double res = 0.0;
        Random R = new Random(Constants.RANDOM_SEED);
        double result = 0.0;
        double A1[][] = new double[LU_size][LU_size];
        for (int i2 = 0; i2 < LU_size; i2++)
            for (int j1 = 0; j1 < LU_size; j1++) {
                double result1;
                synchronized (R) {
                    int k;
                    double nextValue;
                    k = R.m[R.i] - R.m[R.j];
                    if (k < 0)
                        k += Random.m1;
                    R.m[R.j] = k;
                    if (R.i == 0)
                        R.i = 16;
                    else
                        R.i--;
                    if (R.j == 0)
                        R.j = 16;
                    else
                        R.j--;
                    if (R.haveRange)
                        result1 = R.left + R.dm1 * (double) k * R.width;
                    else
                        result1 = R.dm1 * (double) k;
                }
                A1[i2][j1] = result1;
            }
        double A[][] = A1;
        double lu[][] = new double[LU_size][LU_size];
        int pivot[] = new int[LU_size];
        Stopwatch Q = new Stopwatch();
        int cycles = 2;
        if (!Q.running) {
            Q.running = true;
            Q.total = 0.0;
            Q.last_time = (System.currentTimeMillis() * 0.001);
        }
        for (int i = 0; i < cycles; i++) {
            int M = A.length;
            int N1 = A[0].length;
            int remainder = N1 & 3;
            for (int i1 = 0; i1 < M; i1++) {
                double Bi[] = lu[i1];
                double Ai[] = A[i1];
                for (int j = 0; j < remainder; j++)
                    Bi[j] = Ai[j];
                for (int j = remainder; j < N1; j += 4) {
                    Bi[j] = Ai[j];
                    Bi[j + 1] = Ai[j + 1];
                    Bi[j + 2] = Ai[j + 2];
                    Bi[j + 3] = Ai[j + 3];
                }
            }
            int N2 = lu.length;
            int M1 = lu[0].length;
            int minMN = Math.min(M1, N2);
            for (int j = 0; j < minMN; j++) {
                int jp = j;
                double t = Math.abs(lu[j][j]);
                for (int i1 = j + 1; i1 < M1; i1++) {
                    double ab = Math.abs(lu[i1][j]);
                    if (ab > t) {
                        jp = i1;
                        t = ab;
                    }
                }
                pivot[j] = jp;
                if (lu[jp][j] == 0) {
                    break;
                }
                if (jp != j) {
                    double tA[] = lu[j];
                    lu[j] = lu[jp];
                    lu[jp] = tA;
                }
                if (j < M1 - 1) {
                    double recp = 1.0 / lu[j][j];
                    for (int k = j + 1; k < M1; k++)
                        lu[k][j] *= recp;
                }
                if (j < minMN - 1) {
                    for (int ii = j + 1; ii < M1; ii++) {
                        double Aii[] = lu[ii];
                        double Aj[] = lu[j];
                        double AiiJ = Aii[j];
                        for (int jj = j + 1; jj < N2; jj++)
                            Aii[jj] -= AiiJ * Aj[jj];
                    }
                }
            }
        }
        if (Q.running) {
            Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
            Q.running = false;
        }
        double A2[] = new double[LU_size];
        for (int i2 = 0; i2 < LU_size; i2++) {
            double result1;
            synchronized (R) {
                int k;
                double nextValue;
                k = R.m[R.i] - R.m[R.j];
                if (k < 0)
                    k += Random.m1;
                R.m[R.j] = k;
                if (R.i == 0)
                    R.i = 16;
                else
                    R.i--;
                if (R.j == 0)
                    R.j = 16;
                else
                    R.j--;
                if (R.haveRange)
                    result1 = R.left + R.dm1 * (double) k * R.width;
                else
                    result1 = R.dm1 * (double) k;
            }
            A2[i2] = result1;
        }
        double b[] = A2;
        int N1 = b.length;
        double y[] = new double[N1];
        for (int i = 0; i < N1; i++)
            y[i] = b[i];
        double x[] = y;
        int M2 = lu.length;
        int N8 = lu[0].length;
        int ii = 0;
        for (int i3 = 0; i3 < M2; i3++) {
            int ip = pivot[i3];
            double sum4 = x[ip];
            x[ip] = x[i3];
            if (ii == 0)
                for (int j2 = ii; j2 < i3; j2++)
                    sum4 -= lu[i3][j2] * x[j2];
            else if (sum4 == 0.0)
                ii = i3;
            x[i3] = sum4;
        }
        for (int i3 = N8 - 1; i3 >= 0; i3--) {
            double sum4 = x[i3];
            for (int j2 = i3 + 1; j2 < N8; j2++)
                sum4 -= lu[i3][j2] * x[j2];
            x[i3] = sum4 / lu[i3][i3];
        }
        final double EPS = 1.0e-12;
        int N5 = x.length;
        double y4[] = new double[N5];
        int M1 = A.length;
        int N7 = A[0].length;
        for (int i2 = 0; i2 < M1; i2++) {
            double sum3 = 0.0;
            double Ai1[] = A[i2];
            for (int j1 = 0; j1 < N7; j1++)
                sum3 += Ai1[j1] * x[j1];
            y4[i2] = sum3;
        }
        double[] y2 = y4;
        int N3 = b.length;
        double sum1 = 0.0;
        for (int i1 = 0; i1 < N3; i1++)
            sum1 += Math.abs(b[i1] - y2[i1]);
        if (kernel.CURRENT_LU_RESULT.equals("" + sum1)) {
            if (id == 1) {
                System.out.println(Constants.VERIFICATION_PASSED_MESSAGE);
            }
        } else {
            System.out.println(Constants.VERIFICATION_FAILED_MESSAGE
                    + "The result is " + "" + sum1 + "  instead of " + kernel.CURRENT_LU_RESULT);
        }
        int N4 = x.length;
        double y3[] = new double[N4];
        int M = A.length;
        int N6 = A[0].length;
        for (int i1 = 0; i1 < M; i1++) {
            double sum2 = 0.0;
            double Ai[] = A[i1];
            for (int j = 0; j < N6; j++)
                sum2 += Ai[j] * x[j];
            y3[i1] = sum2;
        }
        double[] y1 = y3;
        int N2 = b.length;
        double sum = 0.0;
        for (int i = 0; i < N2; i++)
            sum += Math.abs(b[i] - y1[i]);
        if (!(sum / LU_size > EPS)) {
            double Nd = (double) LU_size;
            if (Q.running) {
                Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
                Q.last_time = (System.currentTimeMillis() * 0.001);
            }
            result = (2.0 * Nd * Nd * Nd / 3.0) * cycles / Q.total * 1.0e-6;
        }
        res = result;
    }
}
