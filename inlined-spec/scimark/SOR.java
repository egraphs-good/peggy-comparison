/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * Copyright (c) 1997,1998 Sun Microsystems, Inc. All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */

public class SOR {
    int id;

    public SOR(int id) {
        this.id = id;
    }

    public static void main(int id) {
        SOR sor = new SOR(id);
        double min_time = Constants.RESOLUTION_DEFAULT;
        int SOR_size = kernel.CURRENT_SOR_SIZE;
        double res = 0.0;
        Random R = new Random(Constants.RANDOM_SEED);
        double G[][];
        G = threadLocalMatrix.get();
        if (G.length != SOR_size) {
            System.out.println("G.length: " + G.length + " N: " + SOR_size);
            G = new double[SOR_size][SOR_size];
            threadLocalMatrix.set(G);
        }
        for (int i = 0; i < G.length; i++)
            for (int j = 0; j < G[i].length; j++) {
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
                G[i][j] = result;
            }
        G = G;
        Stopwatch Q = new Stopwatch();
        int cycles = 256;
        if (!Q.running) {
            Q.running = true;
            Q.total = 0.0;
            Q.last_time = (System.currentTimeMillis() * 0.001);
        }
        int M = G.length;
        int N1 = G[0].length;
        double omega_over_four = 1.25 * 0.25;
        double one_minus_omega = 1.0 - 1.25;
        double[] Gi = null;
        double Gi_Sum = 0.0;
        int Mm1 = M - 1;
        int Nm1 = N1 - 1;
        for (int p = 0; p < cycles; p++) {
            for (int i = 1; i < Mm1; i++) {
                Gi = G[i];
                double[] Gim1 = G[i - 1];
                double[] Gip1 = G[i + 1];
                for (int j = 1; j < Nm1; j++)
                    Gi[j] = omega_over_four * (Gim1[j] + Gip1[j] + Gi[j - 1]
                            + Gi[j + 1]) + one_minus_omega * Gi[j];
            }
        }
        for (int k = 0; k < Gi.length; k++)
            Gi_Sum += Gi[k];
        double x = Gi_Sum;
        if (Q.running) {
            Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
            Q.running = false;
        }
        if (kernel.CURRENT_SOR_RESULT.equals("" + x)) {
            if (sor.id == 1) {
                System.out.println(Constants.VERIFICATION_PASSED_MESSAGE);
            }
        } else {
            System.out.println(Constants.VERIFICATION_FAILED_MESSAGE
                    + "The result is " + "" + x + "  instead of " + kernel.CURRENT_SOR_RESULT);
        }
        double Md = (double) SOR_size;
        double Nd = (double) SOR_size;
        double num_iterD = (double) cycles;
        if (Q.running) {
            Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
            Q.last_time = (System.currentTimeMillis() * 0.001);
        }
        res = (Md - 1) * (Nd - 1) * num_iterD * 6.0 / Q.total * 1.0e-6;
    }

    public final double num_flops(int M, int N, int num_iterations) {
        double Md = (double) M;
        double Nd = (double) N;
        double num_iterD = (double) num_iterations;
        return (Md - 1) * (Nd - 1) * num_iterD * 6.0;
    }

    public final double execute(double omega, double G[][], int num_iterations) {
        int M = G.length;
        int N = G[0].length;
        double omega_over_four = omega * 0.25;
        double one_minus_omega = 1.0 - omega;
        double[] Gi = null;
        double Gi_Sum = 0.0;
        int Mm1 = M - 1;
        int Nm1 = N - 1;
        for (int p = 0; p < num_iterations; p++) {
            for (int i = 1; i < Mm1; i++) {
                Gi = G[i];
                double[] Gim1 = G[i - 1];
                double[] Gip1 = G[i + 1];
                for (int j = 1; j < Nm1; j++)
                    Gi[j] = omega_over_four * (Gim1[j] + Gip1[j] + Gi[j - 1]
                            + Gi[j + 1]) + one_minus_omega * Gi[j];
            }
        }
        for (int k = 0; k < Gi.length; k++)
            Gi_Sum += Gi[k];
        return Gi_Sum;
    }

    private static final ThreadLocal<double[][]> threadLocalMatrix = new ThreadLocal<double[][]>() {
        @Override
        protected double[][] initialValue() {
            return new double[kernel.CURRENT_SOR_SIZE][kernel.CURRENT_SOR_SIZE];
        }
    };

    public double measureSOR(int N, double min_time, Random R) {
        double G[][];
        G = threadLocalMatrix.get();
        if (G.length != N) {
            System.out.println("G.length: " + G.length + " N: " + N);
            G = new double[N][N];
            threadLocalMatrix.set(G);
        }
        for (int i = 0; i < G.length; i++)
            for (int j = 0; j < G[i].length; j++) {
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
                G[i][j] = result;
            }
        G = G;
        Stopwatch Q = new Stopwatch();
        int cycles = 256;
        if (!Q.running) {
            Q.running = true;
            Q.total = 0.0;
            Q.last_time = (System.currentTimeMillis() * 0.001);
        }
        int M = G.length;
        int N1 = G[0].length;
        double omega_over_four = 1.25 * 0.25;
        double one_minus_omega = 1.0 - 1.25;
        double[] Gi = null;
        double Gi_Sum = 0.0;
        int Mm1 = M - 1;
        int Nm1 = N1 - 1;
        for (int p = 0; p < cycles; p++) {
            for (int i = 1; i < Mm1; i++) {
                Gi = G[i];
                double[] Gim1 = G[i - 1];
                double[] Gip1 = G[i + 1];
                for (int j = 1; j < Nm1; j++)
                    Gi[j] = omega_over_four * (Gim1[j] + Gip1[j] + Gi[j - 1]
                            + Gi[j + 1]) + one_minus_omega * Gi[j];
            }
        }
        for (int k = 0; k < Gi.length; k++)
            Gi_Sum += Gi[k];
        double x = Gi_Sum;
        if (Q.running) {
            Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
            Q.running = false;
        }
        if (kernel.CURRENT_SOR_RESULT.equals("" + x)) {
            if (id == 1) {
                System.out.println(Constants.VERIFICATION_PASSED_MESSAGE);
            }
        } else {
            System.out.println(Constants.VERIFICATION_FAILED_MESSAGE
                    + "The result is " + "" + x + "  instead of " + kernel.CURRENT_SOR_RESULT);
        }
        double Md = (double) N;
        double Nd = (double) N;
        double num_iterD = (double) cycles;
        if (Q.running) {
            Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
            Q.last_time = (System.currentTimeMillis() * 0.001);
        }
        return (Md - 1) * (Nd - 1) * num_iterD * 6.0 / Q.total * 1.0e-6;
    }

    public void run() {
        double min_time = Constants.RESOLUTION_DEFAULT;
        int SOR_size = kernel.CURRENT_SOR_SIZE;
        double res = 0.0;
        Random R = new Random(Constants.RANDOM_SEED);
        double G[][];
        G = threadLocalMatrix.get();
        if (G.length != SOR_size) {
            System.out.println("G.length: " + G.length + " N: " + SOR_size);
            G = new double[SOR_size][SOR_size];
            threadLocalMatrix.set(G);
        }
        for (int i = 0; i < G.length; i++)
            for (int j = 0; j < G[i].length; j++) {
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
                G[i][j] = result;
            }
        G = G;
        Stopwatch Q = new Stopwatch();
        int cycles = 256;
        if (!Q.running) {
            Q.running = true;
            Q.total = 0.0;
            Q.last_time = (System.currentTimeMillis() * 0.001);
        }
        int M = G.length;
        int N1 = G[0].length;
        double omega_over_four = 1.25 * 0.25;
        double one_minus_omega = 1.0 - 1.25;
        double[] Gi = null;
        double Gi_Sum = 0.0;
        int Mm1 = M - 1;
        int Nm1 = N1 - 1;
        for (int p = 0; p < cycles; p++) {
            for (int i = 1; i < Mm1; i++) {
                Gi = G[i];
                double[] Gim1 = G[i - 1];
                double[] Gip1 = G[i + 1];
                for (int j = 1; j < Nm1; j++)
                    Gi[j] = omega_over_four * (Gim1[j] + Gip1[j] + Gi[j - 1]
                            + Gi[j + 1]) + one_minus_omega * Gi[j];
            }
        }
        for (int k = 0; k < Gi.length; k++)
            Gi_Sum += Gi[k];
        double x = Gi_Sum;
        if (Q.running) {
            Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
            Q.running = false;
        }
        if (kernel.CURRENT_SOR_RESULT.equals("" + x)) {
            if (id == 1) {
                System.out.println(Constants.VERIFICATION_PASSED_MESSAGE);
            }
        } else {
            System.out.println(Constants.VERIFICATION_FAILED_MESSAGE
                    + "The result is " + "" + x + "  instead of " + kernel.CURRENT_SOR_RESULT);
        }
        double Md = (double) SOR_size;
        double Nd = (double) SOR_size;
        double num_iterD = (double) cycles;
        if (Q.running) {
            Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
            Q.last_time = (System.currentTimeMillis() * 0.001);
        }
        res = (Md - 1) * (Nd - 1) * num_iterD * 6.0 / Q.total * 1.0e-6;
    }
}
