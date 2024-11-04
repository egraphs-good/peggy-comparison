/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * Copyright (c) 1997,1998 Sun Microsystems, Inc. All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */

public class SparseCompRow {
    int id;

    public SparseCompRow(int id) {
        this.id = id;
    }

    public static void main(int id) {
        SparseCompRow sparse = new SparseCompRow(id);
        double min_time = Constants.RESOLUTION_DEFAULT;
        int Sparse_size_M = kernel.CURRENT_SPARSE_SIZE_M;
        int Sparse_size_nz = kernel.CURRENT_SPARSE_SIZE_nz;
        double res = 0.0;
        Random R = new Random(Constants.RANDOM_SEED);
        double A1[] = new double[Sparse_size_M];
        for (int i2 = 0; i2 < Sparse_size_M; i2++) {
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
            A1[i2] = result;
        }
        double x[] = A1;
        double y[] = new double[Sparse_size_M];
        int nr = Sparse_size_nz / Sparse_size_M;
        int anz = nr * Sparse_size_M;
        double A[] = new double[anz];
        for (int i1 = 0; i1 < anz; i1++) {
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
            A[i1] = result;
        }
        double val[] = A;
        int col[] = new int[anz];
        int row[] = new int[Sparse_size_M + 1];
        row[0] = 0;
        for (int r = 0; r < Sparse_size_M; r++) {
            int rowr = row[r];
            row[r + 1] = rowr + nr;
            int step = r / nr;
            if (step < 1)
                step = 1;
            for (int i = 0; i < nr; i++)
                col[rowr + i] = i * step;
        }
        Stopwatch Q = new Stopwatch();
        int cycles = 512;
        int count = 1;
        if (!Q.running) {
            Q.running = true;
            Q.total = 0.0;
            Q.last_time = (System.currentTimeMillis() * 0.001);
        }
        int call_count = count++;
        double total = 0.0;
        int M = row.length - 1;
        for (int reps = 0; reps < cycles; reps++) {
            for (int r = 0; r < M; r++) {
                double sum = 0.0;
                int rowR = row[r];
                int rowRp1 = row[r + 1];
                for (int i = rowR; i < rowRp1; i++)
                    sum += x[col[i]] * val[i];
                y[r] = sum;
            }
        }
        if (call_count == 1) {
            for (int i = 0; i < y.length; i++)
                total += y[i];
            if (kernel.CURRENT_SPARSE_RESULT.equals("" + total)) {
                if (sparse.id == 1) {
                    System.out.println(Constants.VERIFICATION_PASSED_MESSAGE);
                }
            } else {
                System.out.println(Constants.VERIFICATION_FAILED_MESSAGE
                        + "The result is " + "" + total + "  instead of " + kernel.CURRENT_SPARSE_RESULT);
            }
        }
        if (Q.running) {
            Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
            Q.running = false;
        }
        /*
         * Note that if nz does not divide N evenly, then the
         * actual number of nonzeros used is adjusted slightly.
         */
        int actual_nz = (Sparse_size_nz / Sparse_size_M) * Sparse_size_M;
        if (Q.running) {
            Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
            Q.last_time = (System.currentTimeMillis() * 0.001);
        }
        res = ((double) actual_nz) * 2.0 * ((double) cycles) / Q.total * 1.0e-6;
    }

    /*
     * multiple iterations used to make kernel have roughly
     * same granulairty as other Scimark kernels.
     */
    public double num_flops(int N, int nz, int num_iterations) {
        /*
         * Note that if nz does not divide N evenly, then the
         * actual number of nonzeros used is adjusted slightly.
         */
        int actual_nz = (nz / N) * N;
        return ((double) actual_nz) * 2.0 * ((double) num_iterations);
    }

    /*
     * computes a matrix-vector multiply with a sparse matrix
     * held in compress-row format. If the size of the matrix
     * in MxN with nz nonzeros, then the val[] is the nz nonzeros,
     * with its ith entry in column col[i]. The integer vector row[]
     * is of size M+1 and row[i] points to the begining of the
     * ith row in col[].
     */
    public void matmult(double y[], double val[], int row[],
            int col[], double x[], int NUM_ITERATIONS, int call_count) {
        double total = 0.0;
        int M = row.length - 1;
        for (int reps = 0; reps < NUM_ITERATIONS; reps++) {
            for (int r = 0; r < M; r++) {
                double sum = 0.0;
                int rowR = row[r];
                int rowRp1 = row[r + 1];
                for (int i = rowR; i < rowRp1; i++)
                    sum += x[col[i]] * val[i];
                y[r] = sum;
            }
        }
        if (call_count == 1) {
            for (int i = 0; i < y.length; i++)
                total += y[i];
            if (kernel.CURRENT_SPARSE_RESULT.equals("" + total)) {
                if (id == 1) {
                    System.out.println(Constants.VERIFICATION_PASSED_MESSAGE);
                }
            } else {
                System.out.println(Constants.VERIFICATION_FAILED_MESSAGE
                        + "The result is " + "" + total + "  instead of " + kernel.CURRENT_SPARSE_RESULT);
            }
        }
    }

    public double measureSparseMatmult(int N, int nz,
            double min_time, Random R) {
        double A1[] = new double[N];
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
            A1[i2] = result;
        }
        double x[] = A1;
        double y[] = new double[N];
        int nr = nz / N;
        int anz = nr * N;
        double A[] = new double[anz];
        for (int i1 = 0; i1 < anz; i1++) {
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
            A[i1] = result;
        }
        double val[] = A;
        int col[] = new int[anz];
        int row[] = new int[N + 1];
        row[0] = 0;
        for (int r = 0; r < N; r++) {
            int rowr = row[r];
            row[r + 1] = rowr + nr;
            int step = r / nr;
            if (step < 1)
                step = 1;
            for (int i = 0; i < nr; i++)
                col[rowr + i] = i * step;
        }
        Stopwatch Q = new Stopwatch();
        int cycles = 512;
        int count = 1;
        if (!Q.running) {
            Q.running = true;
            Q.total = 0.0;
            Q.last_time = (System.currentTimeMillis() * 0.001);
        }
        int call_count = count++;
        double total = 0.0;
        int M = row.length - 1;
        for (int reps = 0; reps < cycles; reps++) {
            for (int r = 0; r < M; r++) {
                double sum = 0.0;
                int rowR = row[r];
                int rowRp1 = row[r + 1];
                for (int i = rowR; i < rowRp1; i++)
                    sum += x[col[i]] * val[i];
                y[r] = sum;
            }
        }
        if (call_count == 1) {
            for (int i = 0; i < y.length; i++)
                total += y[i];
            if (kernel.CURRENT_SPARSE_RESULT.equals("" + total)) {
                if (id == 1) {
                    System.out.println(Constants.VERIFICATION_PASSED_MESSAGE);
                }
            } else {
                System.out.println(Constants.VERIFICATION_FAILED_MESSAGE
                        + "The result is " + "" + total + "  instead of " + kernel.CURRENT_SPARSE_RESULT);
            }
        }
        if (Q.running) {
            Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
            Q.running = false;
        }
        /*
         * Note that if nz does not divide N evenly, then the
         * actual number of nonzeros used is adjusted slightly.
         */
        int actual_nz = (nz / N) * N;
        if (Q.running) {
            Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
            Q.last_time = (System.currentTimeMillis() * 0.001);
        }
        return ((double) actual_nz) * 2.0 * ((double) cycles) / Q.total * 1.0e-6;
    }

    public void run() {
        double min_time = Constants.RESOLUTION_DEFAULT;
        int Sparse_size_M = kernel.CURRENT_SPARSE_SIZE_M;
        int Sparse_size_nz = kernel.CURRENT_SPARSE_SIZE_nz;
        double res = 0.0;
        Random R = new Random(Constants.RANDOM_SEED);
        double A1[] = new double[Sparse_size_M];
        for (int i2 = 0; i2 < Sparse_size_M; i2++) {
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
            A1[i2] = result;
        }
        double x[] = A1;
        double y[] = new double[Sparse_size_M];
        int nr = Sparse_size_nz / Sparse_size_M;
        int anz = nr * Sparse_size_M;
        double A[] = new double[anz];
        for (int i1 = 0; i1 < anz; i1++) {
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
            A[i1] = result;
        }
        double val[] = A;
        int col[] = new int[anz];
        int row[] = new int[Sparse_size_M + 1];
        row[0] = 0;
        for (int r = 0; r < Sparse_size_M; r++) {
            int rowr = row[r];
            row[r + 1] = rowr + nr;
            int step = r / nr;
            if (step < 1)
                step = 1;
            for (int i = 0; i < nr; i++)
                col[rowr + i] = i * step;
        }
        Stopwatch Q = new Stopwatch();
        int cycles = 512;
        int count = 1;
        if (!Q.running) {
            Q.running = true;
            Q.total = 0.0;
            Q.last_time = (System.currentTimeMillis() * 0.001);
        }
        int call_count = count++;
        double total = 0.0;
        int M = row.length - 1;
        for (int reps = 0; reps < cycles; reps++) {
            for (int r = 0; r < M; r++) {
                double sum = 0.0;
                int rowR = row[r];
                int rowRp1 = row[r + 1];
                for (int i = rowR; i < rowRp1; i++)
                    sum += x[col[i]] * val[i];
                y[r] = sum;
            }
        }
        if (call_count == 1) {
            for (int i = 0; i < y.length; i++)
                total += y[i];
            if (kernel.CURRENT_SPARSE_RESULT.equals("" + total)) {
                if (id == 1) {
                    System.out.println(Constants.VERIFICATION_PASSED_MESSAGE);
                }
            } else {
                System.out.println(Constants.VERIFICATION_FAILED_MESSAGE
                        + "The result is " + "" + total + "  instead of " + kernel.CURRENT_SPARSE_RESULT);
            }
        }
        if (Q.running) {
            Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
            Q.running = false;
        }
        /*
         * Note that if nz does not divide N evenly, then the
         * actual number of nonzeros used is adjusted slightly.
         */
        int actual_nz = (Sparse_size_nz / Sparse_size_M) * Sparse_size_M;
        if (Q.running) {
            Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
            Q.last_time = (System.currentTimeMillis() * 0.001);
        }
        res = ((double) actual_nz) * 2.0 * ((double) cycles) / Q.total * 1.0e-6;
    }
}
