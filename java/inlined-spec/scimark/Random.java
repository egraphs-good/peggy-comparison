/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */

/*
 * Random.java based on Java Numerical Toolkit (JNT) Random.UniformSequence
 * class.  We do not use Java's own java.util.Random so that we can compare
 * results with equivalent C and Fortran coces.
 */
public class Random {
    /*
     * -----------------------------------------------------------------------------
     * -
     * CLASS VARIABLES
     * -----------------------------------------------------------------------------
     * -
     */
    int seed = 0;
    int[] m;
    int i = 4;
    int j = 16;
    public static final int mdig = 32;
    public static final int one = 1;
    static final int m1 = (one << mdig - 2) + ((one << mdig - 2) - one);
    public static final int m2 = one << mdig / 2;
    /*
     * For mdig = 32 : m1 = 2147483647, m2 = 65536
     * For mdig = 64 : m1 = 9223372036854775807, m2 = 4294967296
     */
    double dm1 = 1.0 / (double) m1;
    boolean haveRange = false;
    double left = 0.0;
    public double right = 1.0;
    double width = 1.0;

    /*
     * -----------------------------------------------------------------------------
     * -
     * CONSTRUCTORS
     * -----------------------------------------------------------------------------
     * -
     */
    /**
     * Initializes a sequence of uniformly distributed quasi random numbers with a
     * seed based on the system clock.
     */
    public Random() {
        int seed1 = (int) System.currentTimeMillis();
        int jseed, k0, k1, j0, j1, iloop;
        this.seed = seed1;
        m = new int[17];
        jseed = Math.min(Math.abs(seed1), m1);
        if (jseed % 2 == 0)
            --jseed;
        k0 = 9069 % m2;
        k1 = 9069 / m2;
        j0 = jseed % m2;
        j1 = jseed / m2;
        for (iloop = 0; iloop < 17; ++iloop) {
            jseed = j0 * k0;
            j1 = (jseed / m2 + j0 * k1 + j1 * k0) % (m2 / 2);
            j0 = jseed % m2;
            m[iloop] = j0 + m2 * j1;
        }
        i = 4;
        j = 16;
    }

    /**
     *
     * Initializes a sequence of uniformly distributed quasi random numbers on a
     * given half-open interval [left,right) with a seed based on the system
     * clock.
     *
     * @param left  The left endpoint of the half-open interval [left,right).
     * @param right The right endpoint of the half-open interval [left,right).
     */
    public Random(double left, double right) {
        int seed1 = (int) System.currentTimeMillis();
        int jseed, k0, k1, j0, j1, iloop;
        this.seed = seed1;
        m = new int[17];
        jseed = Math.min(Math.abs(seed1), m1);
        if (jseed % 2 == 0)
            --jseed;
        k0 = 9069 % m2;
        k1 = 9069 / m2;
        j0 = jseed % m2;
        j1 = jseed / m2;
        for (iloop = 0; iloop < 17; ++iloop) {
            jseed = j0 * k0;
            j1 = (jseed / m2 + j0 * k1 + j1 * k0) % (m2 / 2);
            j0 = jseed % m2;
            m[iloop] = j0 + m2 * j1;
        }
        i = 4;
        j = 16;
        this.left = left;
        this.right = right;
        width = right - left;
        haveRange = true;
    }

    /**
     * Initializes a sequence of uniformly distributed quasi random numbers with a
     * given seed.
     *
     * @param seed The seed of the random number generator.
     *             Two sequences with the same seed will be identical.
     */
    public Random(int seed) {
        int jseed, k0, k1, j0, j1, iloop;
        this.seed = seed;
        m = new int[17];
        jseed = Math.min(Math.abs(seed), m1);
        if (jseed % 2 == 0)
            --jseed;
        k0 = 9069 % m2;
        k1 = 9069 / m2;
        j0 = jseed % m2;
        j1 = jseed / m2;
        for (iloop = 0; iloop < 17; ++iloop) {
            jseed = j0 * k0;
            j1 = (jseed / m2 + j0 * k1 + j1 * k0) % (m2 / 2);
            j0 = jseed % m2;
            m[iloop] = j0 + m2 * j1;
        }
        i = 4;
        j = 16;
    }

    /**
     * Initializes a sequence of uniformly distributed quasi random numbers
     * with a given seed on a given half-open interval [left,right).
     * 
     * @param seed  The seed of the random number generator. Two sequences
     *              with the same seed will be identical.
     * @param left  The left endpoint of the half-open interval [left,right).
     * @param right The right endpoint of the half-open interval [left,right).
     */
    public Random(int seed, double left, double right) {
        int jseed, k0, k1, j0, j1, iloop;
        this.seed = seed;
        m = new int[17];
        jseed = Math.min(Math.abs(seed), m1);
        if (jseed % 2 == 0)
            --jseed;
        k0 = 9069 % m2;
        k1 = 9069 / m2;
        j0 = jseed % m2;
        j1 = jseed / m2;
        for (iloop = 0; iloop < 17; ++iloop) {
            jseed = j0 * k0;
            j1 = (jseed / m2 + j0 * k1 + j1 * k0) % (m2 / 2);
            j0 = jseed % m2;
            m[iloop] = j0 + m2 * j1;
        }
        i = 4;
        j = 16;
        this.left = left;
        this.right = right;
        width = right - left;
        haveRange = true;
    }

    /*
     * -----------------------------------------------------------------------------
     * -
     * PUBLIC METHODS
     * -----------------------------------------------------------------------------
     * -
     */
    /**
     * Returns the next random number in the sequence.
     */
    public final synchronized double nextDouble() {
        int k;
        double nextValue;
        k = m[i] - m[j];
        if (k < 0)
            k += m1;
        m[j] = k;
        if (i == 0)
            i = 16;
        else
            i--;
        if (j == 0)
            j = 16;
        else
            j--;
        if (haveRange)
            return left + dm1 * (double) k * width;
        else
            return dm1 * (double) k;
    }

    /**
     * Returns the next N random numbers in the sequence, as
     * a vector.
     */
    public final synchronized void nextDoubles(double x[]) {
        int N = x.length;
        int remainder = N & 3;
        if (haveRange) {
            for (int count = 0; count < N; count++) {
                int k = m[i] - m[j];
                if (i == 0)
                    i = 16;
                else
                    i--;
                if (k < 0)
                    k += m1;
                m[j] = k;
                if (j == 0)
                    j = 16;
                else
                    j--;
                x[count] = left + dm1 * (double) k * width;
            }
        } else {
            for (int count = 0; count < remainder; count++) {
                int k = m[i] - m[j];
                if (i == 0)
                    i = 16;
                else
                    i--;
                if (k < 0)
                    k += m1;
                m[j] = k;
                if (j == 0)
                    j = 16;
                else
                    j--;
                x[count] = dm1 * (double) k;
            }
            for (int count = remainder; count < N; count += 4) {
                int k = m[i] - m[j];
                if (i == 0)
                    i = 16;
                else
                    i--;
                if (k < 0)
                    k += m1;
                m[j] = k;
                if (j == 0)
                    j = 16;
                else
                    j--;
                x[count] = dm1 * (double) k;
                k = m[i] - m[j];
                if (i == 0)
                    i = 16;
                else
                    i--;
                if (k < 0)
                    k += m1;
                m[j] = k;
                if (j == 0)
                    j = 16;
                else
                    j--;
                x[count + 1] = dm1 * (double) k;
                k = m[i] - m[j];
                if (i == 0)
                    i = 16;
                else
                    i--;
                if (k < 0)
                    k += m1;
                m[j] = k;
                if (j == 0)
                    j = 16;
                else
                    j--;
                x[count + 2] = dm1 * (double) k;
                k = m[i] - m[j];
                if (i == 0)
                    i = 16;
                else
                    i--;
                if (k < 0)
                    k += m1;
                m[j] = k;
                if (j == 0)
                    j = 16;
                else
                    j--;
                x[count + 3] = dm1 * (double) k;
            }
        }
    }

    /*----------------------------------------------------------------------------
     public METHODS
     ------------------------------------------------------------------------ */
    public void initialize(int seed) {
        int jseed, k0, k1, j0, j1, iloop;
        this.seed = seed;
        m = new int[17];
        jseed = Math.min(Math.abs(seed), m1);
        if (jseed % 2 == 0)
            --jseed;
        k0 = 9069 % m2;
        k1 = 9069 / m2;
        j0 = jseed % m2;
        j1 = jseed / m2;
        for (iloop = 0; iloop < 17; ++iloop) {
            jseed = j0 * k0;
            j1 = (jseed / m2 + j0 * k1 + j1 * k0) % (m2 / 2);
            j0 = jseed % m2;
            m[iloop] = j0 + m2 * j1;
        }
        i = 4;
        j = 16;
    }
}
