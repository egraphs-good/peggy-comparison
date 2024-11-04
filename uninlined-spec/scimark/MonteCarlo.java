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
        mc.run();
    }

    public final double num_flops(int Num_samples) {
        return ((double) Num_samples) * 4.0;
    }

    public final double integrate(int numSamples) {
        Random R = new Random(SEED);
        int underCurve = 0;
        for (int count = 0; count < numSamples; count++) {
            double x = R.nextDouble();
            double y = R.nextDouble();
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
        Q.start();
        x = integrate(cycles);
        Q.stop();
        p.println(x);
        return num_flops(cycles) / Q.read() * 1.0e-6;
    }

    public void run() {
        double min_time = Constants.RESOLUTION_DEFAULT;
        double res = 0.0;
        Random R = new Random(Constants.RANDOM_SEED);
        res = measureMonteCarlo(min_time, R);
    }
}
