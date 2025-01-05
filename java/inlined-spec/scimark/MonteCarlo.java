/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * Copyright (c) 1997,1998 Sun Microsystems, Inc. All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */

import java.io.PrintStream;

/**
 * Estimate Pi by approximating the area of a circle.
 *
 * How: generate N random numbers in the unit square, (0,0) to (1,1)
 * and see how are within a radius of 1 or less, i.e.
 * 
 * <pre>
 *
 * sqrt(x ^ 2 + y ^ 2) < r
 *
 * </pre>
 * 
 * since the radius is 1.0, we can square both sides
 * and avoid a sqrt() computation:
 * 
 * <pre>
 *
 * x ^ 2 + y ^ 2 <= 1.0
 *
 * </pre>
 * 
 * this area under the curve is (Pi * r^2)/ 4.0,
 * and the area of the unit of square is 1.0,
 * so Pi can be approximated by
 * 
 * <pre>
 * # points with x^2+y^2 < 1
 * Pi =~ 		--------------------------  * 4.0
 * total # points
 *
 * </pre>
 *
 */
public class MonteCarlo {
    final static int SEED = 113;

    public static void main() {
        MonteCarlo mc = new MonteCarlo();
        double min_time = Constants.RESOLUTION_DEFAULT;
        double res = 0.0;
        Random R = new Random(Constants.RANDOM_SEED);
        PrintStream p = System.out;
        Stopwatch Q = new Stopwatch();
        int cycles = 16777216;
        double x = 0.0;
        if (!Q.running) {
            Q.running = true;
            Q.total = 0.0;
            Q.last_time = (System.currentTimeMillis() * 0.001);
        }
        Random R1 = new Random(SEED);
        int underCurve = 0;
        for (int count = 0; count < cycles; count++) {
            double x1;
            synchronized (R1) {
                int k1;
                double nextValue1;
                k1 = R1.m[R1.i] - R1.m[R1.j];
                if (k1 < 0)
                    k1 += Random.m1;
                R1.m[R1.j] = k1;
                if (R1.i == 0)
                    R1.i = 16;
                else
                    R1.i--;
                if (R1.j == 0)
                    R1.j = 16;
                else
                    R1.j--;
                if (R1.haveRange)
                    x1 = R1.left + R1.dm1 * (double) k1 * R1.width;
                else
                    x1 = R1.dm1 * (double) k1;
            }
            double y;
            synchronized (R1) {
                int k;
                double nextValue;
                k = R1.m[R1.i] - R1.m[R1.j];
                if (k < 0)
                    k += Random.m1;
                R1.m[R1.j] = k;
                if (R1.i == 0)
                    R1.i = 16;
                else
                    R1.i--;
                if (R1.j == 0)
                    R1.j = 16;
                else
                    R1.j--;
                if (R1.haveRange)
                    y = R1.left + R1.dm1 * (double) k * R1.width;
                else
                    y = R1.dm1 * (double) k;
            }
            if (x1 * x1 + y * y <= 1.0) {
                underCurve++;
            }
        }
        x = ((double) underCurve / cycles) * 4.0;
        if (Q.running) {
            Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
            Q.running = false;
        }
        p.println(x);
        if (Q.running) {
            Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
            Q.last_time = (System.currentTimeMillis() * 0.001);
        }
        res = ((double) cycles) * 4.0 / Q.total * 1.0e-6;
    }

    public final double num_flops(int Num_samples) {
        return ((double) Num_samples) * 4.0;
    }

    public final double integrate(int numSamples) {
        Random R = new Random(SEED);
        int underCurve = 0;
        for (int count = 0; count < numSamples; count++) {
            double x;
            synchronized (R) {
                int k1;
                double nextValue1;
                k1 = R.m[R.i] - R.m[R.j];
                if (k1 < 0)
                    k1 += Random.m1;
                R.m[R.j] = k1;
                if (R.i == 0)
                    R.i = 16;
                else
                    R.i--;
                if (R.j == 0)
                    R.j = 16;
                else
                    R.j--;
                if (R.haveRange)
                    x = R.left + R.dm1 * (double) k1 * R.width;
                else
                    x = R.dm1 * (double) k1;
            }
            double y;
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
                    y = R.left + R.dm1 * (double) k * R.width;
                else
                    y = R.dm1 * (double) k;
            }
            if (x * x + y * y <= 1.0) {
                underCurve++;
            }
        }
        return ((double) underCurve / numSamples) * 4.0;
    }

    public double measureMonteCarlo(double min_time, Random R) {
        PrintStream p = System.out;
        Stopwatch Q = new Stopwatch();
        int cycles = 16777216;
        double x = 0.0;
        if (!Q.running) {
            Q.running = true;
            Q.total = 0.0;
            Q.last_time = (System.currentTimeMillis() * 0.001);
        }
        Random R1 = new Random(SEED);
        int underCurve = 0;
        for (int count = 0; count < cycles; count++) {
            double x1;
            synchronized (R1) {
                int k1;
                double nextValue1;
                k1 = R1.m[R1.i] - R1.m[R1.j];
                if (k1 < 0)
                    k1 += Random.m1;
                R1.m[R1.j] = k1;
                if (R1.i == 0)
                    R1.i = 16;
                else
                    R1.i--;
                if (R1.j == 0)
                    R1.j = 16;
                else
                    R1.j--;
                if (R1.haveRange)
                    x1 = R1.left + R1.dm1 * (double) k1 * R1.width;
                else
                    x1 = R1.dm1 * (double) k1;
            }
            double y;
            synchronized (R1) {
                int k;
                double nextValue;
                k = R1.m[R1.i] - R1.m[R1.j];
                if (k < 0)
                    k += Random.m1;
                R1.m[R1.j] = k;
                if (R1.i == 0)
                    R1.i = 16;
                else
                    R1.i--;
                if (R1.j == 0)
                    R1.j = 16;
                else
                    R1.j--;
                if (R1.haveRange)
                    y = R1.left + R1.dm1 * (double) k * R1.width;
                else
                    y = R1.dm1 * (double) k;
            }
            if (x1 * x1 + y * y <= 1.0) {
                underCurve++;
            }
        }
        x = ((double) underCurve / cycles) * 4.0;
        if (Q.running) {
            Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
            Q.running = false;
        }
        p.println(x);
        if (Q.running) {
            Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
            Q.last_time = (System.currentTimeMillis() * 0.001);
        }
        return ((double) cycles) * 4.0 / Q.total * 1.0e-6;
    }

    public void run() {
        double min_time = Constants.RESOLUTION_DEFAULT;
        double res = 0.0;
        Random R = new Random(Constants.RANDOM_SEED);
        PrintStream p = System.out;
        Stopwatch Q = new Stopwatch();
        int cycles = 16777216;
        double x = 0.0;
        if (!Q.running) {
            Q.running = true;
            Q.total = 0.0;
            Q.last_time = (System.currentTimeMillis() * 0.001);
        }
        Random R1 = new Random(SEED);
        int underCurve = 0;
        for (int count = 0; count < cycles; count++) {
            double x1;
            synchronized (R1) {
                int k1;
                double nextValue1;
                k1 = R1.m[R1.i] - R1.m[R1.j];
                if (k1 < 0)
                    k1 += Random.m1;
                R1.m[R1.j] = k1;
                if (R1.i == 0)
                    R1.i = 16;
                else
                    R1.i--;
                if (R1.j == 0)
                    R1.j = 16;
                else
                    R1.j--;
                if (R1.haveRange)
                    x1 = R1.left + R1.dm1 * (double) k1 * R1.width;
                else
                    x1 = R1.dm1 * (double) k1;
            }
            double y;
            synchronized (R1) {
                int k;
                double nextValue;
                k = R1.m[R1.i] - R1.m[R1.j];
                if (k < 0)
                    k += Random.m1;
                R1.m[R1.j] = k;
                if (R1.i == 0)
                    R1.i = 16;
                else
                    R1.i--;
                if (R1.j == 0)
                    R1.j = 16;
                else
                    R1.j--;
                if (R1.haveRange)
                    y = R1.left + R1.dm1 * (double) k * R1.width;
                else
                    y = R1.dm1 * (double) k;
            }
            if (x1 * x1 + y * y <= 1.0) {
                underCurve++;
            }
        }
        x = ((double) underCurve / cycles) * 4.0;
        if (Q.running) {
            Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
            Q.running = false;
        }
        p.println(x);
        if (Q.running) {
            Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
            Q.last_time = (System.currentTimeMillis() * 0.001);
        }
        res = ((double) cycles) * 4.0 / Q.total * 1.0e-6;
    }
}
