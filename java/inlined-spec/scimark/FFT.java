/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * Copyright (c) 1997,1998 Sun Microsystems, Inc. All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */

/**
 * Computes FFT's of complex, double precision data where n is an integer power
 * of 2.
 * This appears to be slower than the Radix2 method,
 * but the code is smaller and simpler, and it requires no extra storage.
 * <P>
 *
 * @author Bruce R. Miller bruce.miller@nist.gov,
 * @author Derived from GSL (Gnu Scientific Library),
 * @author GSL's FFT Code by Brian Gough bjg@vvv.lanl.gov
 */
/*
 * See {@link ComplexDoubleFFT ComplexDoubleFFT} for details of data layout.
 */
public class FFT {
    int id;

    public FFT(int id) {
        this.id = id;
    }

    public final double num_flops(int N) {
        double Nd = (double) N;
        int log = 0;
        for (int k = 1; k < N; k *= 2, log++)
            ;
        if (N != (1 << log))
            throw new Error("FFT: Data length is not a power of 2!: " + N);
        double logN = (double) log;
        return (5.0 * Nd - 2) * logN + 2 * (Nd + 1);
    }

    public long inst_main(String[] argv) {
        try {
            double min_time = Constants.RESOLUTION_DEFAULT;
            int FFT_size = kernel.CURRENT_FFT_SIZE;
            double res = 0.0;
            Random R = new Random(Constants.RANDOM_SEED);
            double result = 0.0;
            double x[];
            x = threadLocalVector.get();
            if (x.length != FFT_size) {
                x = new double[FFT_size];
                threadLocalVector.set(x);
            }
            for (int i6 = 0; i6 < x.length; i6++) {
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
                x[i6] = result1;
            }
            x = x;
            long cycles = 1;
            Stopwatch Q = new Stopwatch();
            if (!Q.running) {
                Q.running = true;
                Q.total = 0.0;
                Q.last_time = (System.currentTimeMillis() * 0.001);
            }
            if (x.length != 0) {
                int n5 = x.length / 2;
                if (n5 != 1) {
                    int log3 = 0;
                    for (int k3 = 1; k3 < n5; k3 *= 2, log3++)
                        ;
                    if (n5 != (1 << log3))
                        throw new Error("FFT: Data length is not a power of 2!: " + n5);
                    int logn3 = log3;/* bit reverse the input data for decimation in time algorithm */
                    /* apply fft recursion */
                    /* this loop executed log2(N) times */
                    /* This is the Goldrader bit-reversal algorithm */
                    int n = x.length / 2;
                    int nm1 = n - 1;
                    int i = 0;
                    int j = 0;
                    for (; i < nm1; i++) {
                        int ii = i << 1;
                        int jj = j << 1;
                        int k = n >> 1;
                        if (i < j) {
                            double tmp_real = x[ii];
                            double tmp_imag = x[ii + 1];
                            x[ii] = x[jj];
                            x[ii + 1] = x[jj + 1];
                            x[jj] = tmp_real;
                            x[jj + 1] = tmp_imag;
                        }
                        while (k <= j) {
                            j -= k;
                            k >>= 1;
                        }
                        j += k;
                    }
                    for (int bit3 = 0, dual3 = 1; bit3 < logn3; bit3++, dual3 *= 2) {
                        double w_real3 = 1.0;
                        double w_imag3 = 0.0;
                        double theta3 = 2.0 * -1 * Math.PI / (2.0 * (double) dual3);
                        double s6 = Math.sin(theta3);
                        double t3 = Math.sin(theta3 / 2.0);
                        double s7 = 2.0 * t3 * t3;
                        /* a = 0 */
                        for (int b3 = 0; b3 < n5; b3 += 2 * dual3) {
                            int i5 = 2 * b3;
                            int j3 = 2 * (b3 + dual3);
                            double wd_real3 = x[j3];
                            double wd_imag3 = x[j3 + 1];
                            x[j3] = x[i5] - wd_real3;
                            x[j3 + 1] = x[i5 + 1] - wd_imag3;
                            x[i5] += wd_real3;
                            x[i5 + 1] += wd_imag3;
                        }
                        /* a = 1 .. (dual-1) */
                        for (int a3 = 1; a3 < dual3; a3++) {
                            /* trignometric recurrence for w-> exp(i theta) w */
                            {
                                double tmp_real3 = w_real3 - s6 * w_imag3 - s7 * w_real3;
                                double tmp_imag3 = w_imag3 + s6 * w_real3 - s7 * w_imag3;
                                w_real3 = tmp_real3;
                                w_imag3 = tmp_imag3;
                            }
                            for (int b3 = 0; b3 < n5; b3 += 2 * dual3) {
                                int i5 = 2 * (b3 + a3);
                                int j3 = 2 * (b3 + a3 + dual3);
                                double z1_real3 = x[j3];
                                double z1_imag3 = x[j3 + 1];
                                double wd_real3 = w_real3 * z1_real3 - w_imag3 * z1_imag3;
                                double wd_imag3 = w_real3 * z1_imag3 + w_imag3 * z1_real3;
                                x[j3] = x[i5] - wd_real3;
                                x[j3 + 1] = x[i5 + 1] - wd_imag3;
                                x[i5] += wd_real3;
                                x[i5 + 1] += wd_imag3;
                            }
                        }
                    }
                }
            }
            if (x.length != 0) {
                int n4 = x.length / 2;
                if (n4 != 1) {
                    int log2 = 0;
                    for (int k2 = 1; k2 < n4; k2 *= 2, log2++)
                        ;
                    if (n4 != (1 << log2))
                        throw new Error("FFT: Data length is not a power of 2!: " + n4);
                    int logn2 = log2;/* bit reverse the input data for decimation in time algorithm */
                    /* apply fft recursion */
                    /* this loop executed log2(N) times */
                    /* This is the Goldrader bit-reversal algorithm */
                    int n = x.length / 2;
                    int nm1 = n - 1;
                    int i = 0;
                    int j = 0;
                    for (; i < nm1; i++) {
                        int ii = i << 1;
                        int jj = j << 1;
                        int k = n >> 1;
                        if (i < j) {
                            double tmp_real = x[ii];
                            double tmp_imag = x[ii + 1];
                            x[ii] = x[jj];
                            x[ii + 1] = x[jj + 1];
                            x[jj] = tmp_real;
                            x[jj + 1] = tmp_imag;
                        }
                        while (k <= j) {
                            j -= k;
                            k >>= 1;
                        }
                        j += k;
                    }
                    for (int bit2 = 0, dual2 = 1; bit2 < logn2; bit2++, dual2 *= 2) {
                        double w_real2 = 1.0;
                        double w_imag2 = 0.0;
                        double theta2 = 2.0 * +1 * Math.PI / (2.0 * (double) dual2);
                        double s4 = Math.sin(theta2);
                        double t2 = Math.sin(theta2 / 2.0);
                        double s5 = 2.0 * t2 * t2;
                        /* a = 0 */
                        for (int b2 = 0; b2 < n4; b2 += 2 * dual2) {
                            int i4 = 2 * b2;
                            int j2 = 2 * (b2 + dual2);
                            double wd_real2 = x[j2];
                            double wd_imag2 = x[j2 + 1];
                            x[j2] = x[i4] - wd_real2;
                            x[j2 + 1] = x[i4 + 1] - wd_imag2;
                            x[i4] += wd_real2;
                            x[i4 + 1] += wd_imag2;
                        }
                        /* a = 1 .. (dual-1) */
                        for (int a2 = 1; a2 < dual2; a2++) {
                            /* trignometric recurrence for w-> exp(i theta) w */
                            {
                                double tmp_real2 = w_real2 - s4 * w_imag2 - s5 * w_real2;
                                double tmp_imag2 = w_imag2 + s4 * w_real2 - s5 * w_imag2;
                                w_real2 = tmp_real2;
                                w_imag2 = tmp_imag2;
                            }
                            for (int b2 = 0; b2 < n4; b2 += 2 * dual2) {
                                int i4 = 2 * (b2 + a2);
                                int j2 = 2 * (b2 + a2 + dual2);
                                double z1_real2 = x[j2];
                                double z1_imag2 = x[j2 + 1];
                                double wd_real2 = w_real2 * z1_real2 - w_imag2 * z1_imag2;
                                double wd_imag2 = w_real2 * z1_imag2 + w_imag2 * z1_real2;
                                x[j2] = x[i4] - wd_real2;
                                x[j2 + 1] = x[i4 + 1] - wd_imag2;
                                x[i4] += wd_real2;
                                x[i4 + 1] += wd_imag2;
                            }
                        }
                    }
                }
            }
            int nd = x.length;
            int n = nd / 2;
            double norm = 1 / ((double) n);
            for (int i = 0; i < nd; i++)
                x[i] *= norm;
            if (Q.running) {
                Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
                Q.running = false;
            }
            final double EPS = 1.0e-10;
            int nd2 = x.length;
            double copy[] = new double[nd2];
            System.arraycopy(x, 0, copy, 0, nd2);
            if (x.length != 0) {
                int n3 = x.length / 2;
                if (n3 != 1) {
                    int log1 = 0;
                    for (int k1 = 1; k1 < n3; k1 *= 2, log1++)
                        ;
                    if (n3 != (1 << log1))
                        throw new Error("FFT: Data length is not a power of 2!: " + n3);
                    int logn1 = log1;/* bit reverse the input data for decimation in time algorithm */
                    /* apply fft recursion */
                    /* this loop executed log2(N) times */
                    /* This is the Goldrader bit-reversal algorithm */
                    int n1 = x.length / 2;
                    int nm1 = n1 - 1;
                    int i = 0;
                    int j = 0;
                    for (; i < nm1; i++) {
                        int ii = i << 1;
                        int jj = j << 1;
                        int k = n1 >> 1;
                        if (i < j) {
                            double tmp_real = x[ii];
                            double tmp_imag = x[ii + 1];
                            x[ii] = x[jj];
                            x[ii + 1] = x[jj + 1];
                            x[jj] = tmp_real;
                            x[jj + 1] = tmp_imag;
                        }
                        while (k <= j) {
                            j -= k;
                            k >>= 1;
                        }
                        j += k;
                    }
                    for (int bit1 = 0, dual1 = 1; bit1 < logn1; bit1++, dual1 *= 2) {
                        double w_real1 = 1.0;
                        double w_imag1 = 0.0;
                        double theta1 = 2.0 * -1 * Math.PI / (2.0 * (double) dual1);
                        double s1 = Math.sin(theta1);
                        double t1 = Math.sin(theta1 / 2.0);
                        double s3 = 2.0 * t1 * t1;
                        /* a = 0 */
                        for (int b1 = 0; b1 < n3; b1 += 2 * dual1) {
                            int i3 = 2 * b1;
                            int j1 = 2 * (b1 + dual1);
                            double wd_real1 = x[j1];
                            double wd_imag1 = x[j1 + 1];
                            x[j1] = x[i3] - wd_real1;
                            x[j1 + 1] = x[i3 + 1] - wd_imag1;
                            x[i3] += wd_real1;
                            x[i3 + 1] += wd_imag1;
                        }
                        /* a = 1 .. (dual-1) */
                        for (int a1 = 1; a1 < dual1; a1++) {
                            /* trignometric recurrence for w-> exp(i theta) w */
                            {
                                double tmp_real1 = w_real1 - s1 * w_imag1 - s3 * w_real1;
                                double tmp_imag1 = w_imag1 + s1 * w_real1 - s3 * w_imag1;
                                w_real1 = tmp_real1;
                                w_imag1 = tmp_imag1;
                            }
                            for (int b1 = 0; b1 < n3; b1 += 2 * dual1) {
                                int i3 = 2 * (b1 + a1);
                                int j1 = 2 * (b1 + a1 + dual1);
                                double z1_real1 = x[j1];
                                double z1_imag1 = x[j1 + 1];
                                double wd_real1 = w_real1 * z1_real1 - w_imag1 * z1_imag1;
                                double wd_imag1 = w_real1 * z1_imag1 + w_imag1 * z1_real1;
                                x[j1] = x[i3] - wd_real1;
                                x[j1 + 1] = x[i3 + 1] - wd_imag1;
                                x[i3] += wd_real1;
                                x[i3 + 1] += wd_imag1;
                            }
                        }
                    }
                }
            }
            if (x.length != 0) {
                int n2 = x.length / 2;
                if (n2 != 1) {
                    int log = 0;
                    for (int k = 1; k < n2; k *= 2, log++)
                        ;
                    if (n2 != (1 << log))
                        throw new Error("FFT: Data length is not a power of 2!: " + n2);
                    int logn = log;/* bit reverse the input data for decimation in time algorithm */
                    /* apply fft recursion */
                    /* this loop executed log2(N) times */
                    /* This is the Goldrader bit-reversal algorithm */
                    int n1 = x.length / 2;
                    int nm1 = n1 - 1;
                    int i = 0;
                    int j1 = 0;
                    for (; i < nm1; i++) {
                        int ii = i << 1;
                        int jj = j1 << 1;
                        int k = n1 >> 1;
                        if (i < j1) {
                            double tmp_real1 = x[ii];
                            double tmp_imag1 = x[ii + 1];
                            x[ii] = x[jj];
                            x[ii + 1] = x[jj + 1];
                            x[jj] = tmp_real1;
                            x[jj + 1] = tmp_imag1;
                        }
                        while (k <= j1) {
                            j1 -= k;
                            k >>= 1;
                        }
                        j1 += k;
                    }
                    for (int bit = 0, dual = 1; bit < logn; bit++, dual *= 2) {
                        double w_real = 1.0;
                        double w_imag = 0.0;
                        double theta = 2.0 * +1 * Math.PI / (2.0 * (double) dual);
                        double s = Math.sin(theta);
                        double t = Math.sin(theta / 2.0);
                        double s2 = 2.0 * t * t;
                        /* a = 0 */
                        for (int b = 0; b < n2; b += 2 * dual) {
                            int i2 = 2 * b;
                            int j = 2 * (b + dual);
                            double wd_real = x[j];
                            double wd_imag = x[j + 1];
                            x[j] = x[i2] - wd_real;
                            x[j + 1] = x[i2 + 1] - wd_imag;
                            x[i2] += wd_real;
                            x[i2 + 1] += wd_imag;
                        }
                        /* a = 1 .. (dual-1) */
                        for (int a = 1; a < dual; a++) {
                            /* trignometric recurrence for w-> exp(i theta) w */
                            {
                                double tmp_real = w_real - s * w_imag - s2 * w_real;
                                double tmp_imag = w_imag + s * w_real - s2 * w_imag;
                                w_real = tmp_real;
                                w_imag = tmp_imag;
                            }
                            for (int b = 0; b < n2; b += 2 * dual) {
                                int i2 = 2 * (b + a);
                                int j = 2 * (b + a + dual);
                                double z1_real = x[j];
                                double z1_imag = x[j + 1];
                                double wd_real = w_real * z1_real - w_imag * z1_imag;
                                double wd_imag = w_real * z1_imag + w_imag * z1_real;
                                x[j] = x[i2] - wd_real;
                                x[j + 1] = x[i2 + 1] - wd_imag;
                                x[i2] += wd_real;
                                x[i2 + 1] += wd_imag;
                            }
                        }
                    }
                }
            }
            int nd1 = x.length;
            int n1 = nd1 / 2;
            double norm1 = 1 / ((double) n1);
            for (int i1 = 0; i1 < nd1; i1++)
                x[i1] *= norm1;
            double diff = 0.0;
            for (int i = 0; i < nd2; i++) {
                double d = x[i] - copy[i];
                diff += d * d;
            }
            double fftTest = Math.sqrt(diff / nd2);
            if (kernel.CURRENT_FFT_RESULT.equals("" + fftTest)) {
                if (id == 1) {
                    System.out.println(Constants.VERIFICATION_PASSED_MESSAGE);
                }
            } else {
                System.out.println(Constants.VERIFICATION_FAILED_MESSAGE
                        + "The result is " + "" + fftTest + "  instead of " + kernel.CURRENT_FFT_RESULT);
            }
            if (!(fftTest / FFT_size > EPS)) {
                x = null;
                if (Q.running) {
                    Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
                    Q.last_time = (System.currentTimeMillis() * 0.001);
                }
                result = num_flops(FFT_size) * cycles / Q.total * 1.0e-6;
            }
            res = result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /** Compute Fast Fourier Transform of (complex) data, in place. */
    public void transform(double data[]) {
        if (data.length == 0)
            return;
        int n = data.length / 2;
        if (n == 1)
            return;
        int log = 0;
        for (int k = 1; k < n; k *= 2, log++)
            ;
        if (n != (1 << log))
            throw new Error("FFT: Data length is not a power of 2!: " + n);
        int logn = log;
        /* bit reverse the input data for decimation in time algorithm */
        /* This is the Goldrader bit-reversal algorithm */
        int n1 = data.length / 2;
        int nm1 = n1 - 1;
        int i1 = 0;
        int j1 = 0;
        for (; i1 < nm1; i1++) {
            int ii = i1 << 1;
            int jj = j1 << 1;
            int k = n1 >> 1;
            if (i1 < j1) {
                double tmp_real1 = data[ii];
                double tmp_imag1 = data[ii + 1];
                data[ii] = data[jj];
                data[ii + 1] = data[jj + 1];
                data[jj] = tmp_real1;
                data[jj + 1] = tmp_imag1;
            }
            while (k <= j1) {
                j1 -= k;
                k >>= 1;
            }
            j1 += k;
        }
        /* apply fft recursion */
        /* this loop executed log2(N) times */
        for (int bit = 0, dual = 1; bit < logn; bit++, dual *= 2) {
            double w_real = 1.0;
            double w_imag = 0.0;
            double theta = 2.0 * -1 * Math.PI / (2.0 * (double) dual);
            double s = Math.sin(theta);
            double t = Math.sin(theta / 2.0);
            double s2 = 2.0 * t * t;
            /* a = 0 */
            for (int b = 0; b < n; b += 2 * dual) {
                int i = 2 * b;
                int j = 2 * (b + dual);
                double wd_real = data[j];
                double wd_imag = data[j + 1];
                data[j] = data[i] - wd_real;
                data[j + 1] = data[i + 1] - wd_imag;
                data[i] += wd_real;
                data[i + 1] += wd_imag;
            }
            /* a = 1 .. (dual-1) */
            for (int a = 1; a < dual; a++) {
                /* trignometric recurrence for w-> exp(i theta) w */
                {
                    double tmp_real = w_real - s * w_imag - s2 * w_real;
                    double tmp_imag = w_imag + s * w_real - s2 * w_imag;
                    w_real = tmp_real;
                    w_imag = tmp_imag;
                }
                for (int b = 0; b < n; b += 2 * dual) {
                    int i = 2 * (b + a);
                    int j = 2 * (b + a + dual);
                    double z1_real = data[j];
                    double z1_imag = data[j + 1];
                    double wd_real = w_real * z1_real - w_imag * z1_imag;
                    double wd_imag = w_real * z1_imag + w_imag * z1_real;
                    data[j] = data[i] - wd_real;
                    data[j + 1] = data[i + 1] - wd_imag;
                    data[i] += wd_real;
                    data[i + 1] += wd_imag;
                }
            }
        }
    }

    /** Compute Inverse Fast Fourier Transform of (complex) data, in place. */
    public void inverse(double data[]) {
        if (data.length != 0) {
            int n1 = data.length / 2;
            if (n1 != 1) {
                int log = 0;
                for (int k = 1; k < n1; k *= 2, log++)
                    ;
                if (n1 != (1 << log))
                    throw new Error("FFT: Data length is not a power of 2!: " + n1);
                int logn = log;/* bit reverse the input data for decimation in time algorithm */
                /* apply fft recursion */
                /* this loop executed log2(N) times */
                /* This is the Goldrader bit-reversal algorithm */
                int n = data.length / 2;
                int nm1 = n - 1;
                int i = 0;
                int j1 = 0;
                for (; i < nm1; i++) {
                    int ii = i << 1;
                    int jj = j1 << 1;
                    int k = n >> 1;
                    if (i < j1) {
                        double tmp_real1 = data[ii];
                        double tmp_imag1 = data[ii + 1];
                        data[ii] = data[jj];
                        data[ii + 1] = data[jj + 1];
                        data[jj] = tmp_real1;
                        data[jj + 1] = tmp_imag1;
                    }
                    while (k <= j1) {
                        j1 -= k;
                        k >>= 1;
                    }
                    j1 += k;
                }
                for (int bit = 0, dual = 1; bit < logn; bit++, dual *= 2) {
                    double w_real = 1.0;
                    double w_imag = 0.0;
                    double theta = 2.0 * +1 * Math.PI / (2.0 * (double) dual);
                    double s = Math.sin(theta);
                    double t = Math.sin(theta / 2.0);
                    double s2 = 2.0 * t * t;
                    /* a = 0 */
                    for (int b = 0; b < n1; b += 2 * dual) {
                        int i1 = 2 * b;
                        int j = 2 * (b + dual);
                        double wd_real = data[j];
                        double wd_imag = data[j + 1];
                        data[j] = data[i1] - wd_real;
                        data[j + 1] = data[i1 + 1] - wd_imag;
                        data[i1] += wd_real;
                        data[i1 + 1] += wd_imag;
                    }
                    /* a = 1 .. (dual-1) */
                    for (int a = 1; a < dual; a++) {
                        /* trignometric recurrence for w-> exp(i theta) w */
                        {
                            double tmp_real = w_real - s * w_imag - s2 * w_real;
                            double tmp_imag = w_imag + s * w_real - s2 * w_imag;
                            w_real = tmp_real;
                            w_imag = tmp_imag;
                        }
                        for (int b = 0; b < n1; b += 2 * dual) {
                            int i1 = 2 * (b + a);
                            int j = 2 * (b + a + dual);
                            double z1_real = data[j];
                            double z1_imag = data[j + 1];
                            double wd_real = w_real * z1_real - w_imag * z1_imag;
                            double wd_imag = w_real * z1_imag + w_imag * z1_real;
                            data[j] = data[i1] - wd_real;
                            data[j + 1] = data[i1 + 1] - wd_imag;
                            data[i1] += wd_real;
                            data[i1 + 1] += wd_imag;
                        }
                    }
                }
            }
        }
        int nd = data.length;
        int n = nd / 2;
        double norm = 1 / ((double) n);
        for (int i = 0; i < nd; i++)
            data[i] *= norm;
    }

    /**
     * Accuracy check on FFT of data. Make a copy of data, Compute the FFT, then
     * the inverse and compare to the original. Returns the rms difference.
     */
    public double test(double data[]) {
        int nd = data.length;
        double copy[] = new double[nd];
        System.arraycopy(data, 0, copy, 0, nd);
        if (data.length != 0) {
            int n2 = data.length / 2;
            if (n2 != 1) {
                int log1 = 0;
                for (int k1 = 1; k1 < n2; k1 *= 2, log1++)
                    ;
                if (n2 != (1 << log1))
                    throw new Error("FFT: Data length is not a power of 2!: " + n2);
                int logn1 = log1;/* bit reverse the input data for decimation in time algorithm */
                /* apply fft recursion */
                /* this loop executed log2(N) times */
                /* This is the Goldrader bit-reversal algorithm */
                int n = data.length / 2;
                int nm1 = n - 1;
                int i = 0;
                int j = 0;
                for (; i < nm1; i++) {
                    int ii = i << 1;
                    int jj = j << 1;
                    int k = n >> 1;
                    if (i < j) {
                        double tmp_real = data[ii];
                        double tmp_imag = data[ii + 1];
                        data[ii] = data[jj];
                        data[ii + 1] = data[jj + 1];
                        data[jj] = tmp_real;
                        data[jj + 1] = tmp_imag;
                    }
                    while (k <= j) {
                        j -= k;
                        k >>= 1;
                    }
                    j += k;
                }
                for (int bit1 = 0, dual1 = 1; bit1 < logn1; bit1++, dual1 *= 2) {
                    double w_real1 = 1.0;
                    double w_imag1 = 0.0;
                    double theta1 = 2.0 * -1 * Math.PI / (2.0 * (double) dual1);
                    double s1 = Math.sin(theta1);
                    double t1 = Math.sin(theta1 / 2.0);
                    double s3 = 2.0 * t1 * t1;
                    /* a = 0 */
                    for (int b1 = 0; b1 < n2; b1 += 2 * dual1) {
                        int i3 = 2 * b1;
                        int j1 = 2 * (b1 + dual1);
                        double wd_real1 = data[j1];
                        double wd_imag1 = data[j1 + 1];
                        data[j1] = data[i3] - wd_real1;
                        data[j1 + 1] = data[i3 + 1] - wd_imag1;
                        data[i3] += wd_real1;
                        data[i3 + 1] += wd_imag1;
                    }
                    /* a = 1 .. (dual-1) */
                    for (int a1 = 1; a1 < dual1; a1++) {
                        /* trignometric recurrence for w-> exp(i theta) w */
                        {
                            double tmp_real1 = w_real1 - s1 * w_imag1 - s3 * w_real1;
                            double tmp_imag1 = w_imag1 + s1 * w_real1 - s3 * w_imag1;
                            w_real1 = tmp_real1;
                            w_imag1 = tmp_imag1;
                        }
                        for (int b1 = 0; b1 < n2; b1 += 2 * dual1) {
                            int i3 = 2 * (b1 + a1);
                            int j1 = 2 * (b1 + a1 + dual1);
                            double z1_real1 = data[j1];
                            double z1_imag1 = data[j1 + 1];
                            double wd_real1 = w_real1 * z1_real1 - w_imag1 * z1_imag1;
                            double wd_imag1 = w_real1 * z1_imag1 + w_imag1 * z1_real1;
                            data[j1] = data[i3] - wd_real1;
                            data[j1 + 1] = data[i3 + 1] - wd_imag1;
                            data[i3] += wd_real1;
                            data[i3 + 1] += wd_imag1;
                        }
                    }
                }
            }
        }
        if (data.length != 0) {
            int n1 = data.length / 2;
            if (n1 != 1) {
                int log = 0;
                for (int k = 1; k < n1; k *= 2, log++)
                    ;
                if (n1 != (1 << log))
                    throw new Error("FFT: Data length is not a power of 2!: " + n1);
                int logn = log;/* bit reverse the input data for decimation in time algorithm */
                /* apply fft recursion */
                /* this loop executed log2(N) times */
                /* This is the Goldrader bit-reversal algorithm */
                int n = data.length / 2;
                int nm1 = n - 1;
                int i = 0;
                int j1 = 0;
                for (; i < nm1; i++) {
                    int ii = i << 1;
                    int jj = j1 << 1;
                    int k = n >> 1;
                    if (i < j1) {
                        double tmp_real1 = data[ii];
                        double tmp_imag1 = data[ii + 1];
                        data[ii] = data[jj];
                        data[ii + 1] = data[jj + 1];
                        data[jj] = tmp_real1;
                        data[jj + 1] = tmp_imag1;
                    }
                    while (k <= j1) {
                        j1 -= k;
                        k >>= 1;
                    }
                    j1 += k;
                }
                for (int bit = 0, dual = 1; bit < logn; bit++, dual *= 2) {
                    double w_real = 1.0;
                    double w_imag = 0.0;
                    double theta = 2.0 * +1 * Math.PI / (2.0 * (double) dual);
                    double s = Math.sin(theta);
                    double t = Math.sin(theta / 2.0);
                    double s2 = 2.0 * t * t;
                    /* a = 0 */
                    for (int b = 0; b < n1; b += 2 * dual) {
                        int i2 = 2 * b;
                        int j = 2 * (b + dual);
                        double wd_real = data[j];
                        double wd_imag = data[j + 1];
                        data[j] = data[i2] - wd_real;
                        data[j + 1] = data[i2 + 1] - wd_imag;
                        data[i2] += wd_real;
                        data[i2 + 1] += wd_imag;
                    }
                    /* a = 1 .. (dual-1) */
                    for (int a = 1; a < dual; a++) {
                        /* trignometric recurrence for w-> exp(i theta) w */
                        {
                            double tmp_real = w_real - s * w_imag - s2 * w_real;
                            double tmp_imag = w_imag + s * w_real - s2 * w_imag;
                            w_real = tmp_real;
                            w_imag = tmp_imag;
                        }
                        for (int b = 0; b < n1; b += 2 * dual) {
                            int i2 = 2 * (b + a);
                            int j = 2 * (b + a + dual);
                            double z1_real = data[j];
                            double z1_imag = data[j + 1];
                            double wd_real = w_real * z1_real - w_imag * z1_imag;
                            double wd_imag = w_real * z1_imag + w_imag * z1_real;
                            data[j] = data[i2] - wd_real;
                            data[j + 1] = data[i2 + 1] - wd_imag;
                            data[i2] += wd_real;
                            data[i2 + 1] += wd_imag;
                        }
                    }
                }
            }
        }
        int nd1 = data.length;
        int n = nd1 / 2;
        double norm = 1 / ((double) n);
        for (int i1 = 0; i1 < nd1; i1++)
            data[i1] *= norm;
        double diff = 0.0;
        for (int i = 0; i < nd; i++) {
            double d = data[i] - copy[i];
            diff += d * d;
        }
        return Math.sqrt(diff / nd);
    }

    /** Make a random array of n (complex) elements. */
    public double[] makeRandom(int n) {
        int nd = 2 * n;
        double data[] = new double[nd];
        for (int i = 0; i < nd; i++)
            data[i] = Math.random();
        return data;
    }

    /** Simple Test routine. */
    public static void main(int id) {
        FFT fft = new FFT(id);
        try {
            double min_time = Constants.RESOLUTION_DEFAULT;
            int FFT_size = kernel.CURRENT_FFT_SIZE;
            double res = 0.0;
            Random R = new Random(Constants.RANDOM_SEED);
            double result = 0.0;
            double x[];
            x = threadLocalVector.get();
            if (x.length != FFT_size) {
                x = new double[FFT_size];
                threadLocalVector.set(x);
            }
            for (int i6 = 0; i6 < x.length; i6++) {
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
                x[i6] = result1;
            }
            x = x;
            long cycles = 1;
            Stopwatch Q = new Stopwatch();
            if (!Q.running) {
                Q.running = true;
                Q.total = 0.0;
                Q.last_time = (System.currentTimeMillis() * 0.001);
            }
            if (x.length != 0) {
                int n5 = x.length / 2;
                if (n5 != 1) {
                    int log3 = 0;
                    for (int k3 = 1; k3 < n5; k3 *= 2, log3++)
                        ;
                    if (n5 != (1 << log3))
                        throw new Error("FFT: Data length is not a power of 2!: " + n5);
                    int logn3 = log3;/* bit reverse the input data for decimation in time algorithm */
                    /* apply fft recursion */
                    /* this loop executed log2(N) times */
                    /* This is the Goldrader bit-reversal algorithm */
                    int n = x.length / 2;
                    int nm1 = n - 1;
                    int i = 0;
                    int j = 0;
                    for (; i < nm1; i++) {
                        int ii = i << 1;
                        int jj = j << 1;
                        int k = n >> 1;
                        if (i < j) {
                            double tmp_real = x[ii];
                            double tmp_imag = x[ii + 1];
                            x[ii] = x[jj];
                            x[ii + 1] = x[jj + 1];
                            x[jj] = tmp_real;
                            x[jj + 1] = tmp_imag;
                        }
                        while (k <= j) {
                            j -= k;
                            k >>= 1;
                        }
                        j += k;
                    }
                    for (int bit3 = 0, dual3 = 1; bit3 < logn3; bit3++, dual3 *= 2) {
                        double w_real3 = 1.0;
                        double w_imag3 = 0.0;
                        double theta3 = 2.0 * -1 * Math.PI / (2.0 * (double) dual3);
                        double s6 = Math.sin(theta3);
                        double t3 = Math.sin(theta3 / 2.0);
                        double s7 = 2.0 * t3 * t3;
                        /* a = 0 */
                        for (int b3 = 0; b3 < n5; b3 += 2 * dual3) {
                            int i5 = 2 * b3;
                            int j3 = 2 * (b3 + dual3);
                            double wd_real3 = x[j3];
                            double wd_imag3 = x[j3 + 1];
                            x[j3] = x[i5] - wd_real3;
                            x[j3 + 1] = x[i5 + 1] - wd_imag3;
                            x[i5] += wd_real3;
                            x[i5 + 1] += wd_imag3;
                        }
                        /* a = 1 .. (dual-1) */
                        for (int a3 = 1; a3 < dual3; a3++) {
                            /* trignometric recurrence for w-> exp(i theta) w */
                            {
                                double tmp_real3 = w_real3 - s6 * w_imag3 - s7 * w_real3;
                                double tmp_imag3 = w_imag3 + s6 * w_real3 - s7 * w_imag3;
                                w_real3 = tmp_real3;
                                w_imag3 = tmp_imag3;
                            }
                            for (int b3 = 0; b3 < n5; b3 += 2 * dual3) {
                                int i5 = 2 * (b3 + a3);
                                int j3 = 2 * (b3 + a3 + dual3);
                                double z1_real3 = x[j3];
                                double z1_imag3 = x[j3 + 1];
                                double wd_real3 = w_real3 * z1_real3 - w_imag3 * z1_imag3;
                                double wd_imag3 = w_real3 * z1_imag3 + w_imag3 * z1_real3;
                                x[j3] = x[i5] - wd_real3;
                                x[j3 + 1] = x[i5 + 1] - wd_imag3;
                                x[i5] += wd_real3;
                                x[i5 + 1] += wd_imag3;
                            }
                        }
                    }
                }
            }
            if (x.length != 0) {
                int n4 = x.length / 2;
                if (n4 != 1) {
                    int log2 = 0;
                    for (int k2 = 1; k2 < n4; k2 *= 2, log2++)
                        ;
                    if (n4 != (1 << log2))
                        throw new Error("FFT: Data length is not a power of 2!: " + n4);
                    int logn2 = log2;/* bit reverse the input data for decimation in time algorithm */
                    /* apply fft recursion */
                    /* this loop executed log2(N) times */
                    /* This is the Goldrader bit-reversal algorithm */
                    int n = x.length / 2;
                    int nm1 = n - 1;
                    int i = 0;
                    int j = 0;
                    for (; i < nm1; i++) {
                        int ii = i << 1;
                        int jj = j << 1;
                        int k = n >> 1;
                        if (i < j) {
                            double tmp_real = x[ii];
                            double tmp_imag = x[ii + 1];
                            x[ii] = x[jj];
                            x[ii + 1] = x[jj + 1];
                            x[jj] = tmp_real;
                            x[jj + 1] = tmp_imag;
                        }
                        while (k <= j) {
                            j -= k;
                            k >>= 1;
                        }
                        j += k;
                    }
                    for (int bit2 = 0, dual2 = 1; bit2 < logn2; bit2++, dual2 *= 2) {
                        double w_real2 = 1.0;
                        double w_imag2 = 0.0;
                        double theta2 = 2.0 * +1 * Math.PI / (2.0 * (double) dual2);
                        double s4 = Math.sin(theta2);
                        double t2 = Math.sin(theta2 / 2.0);
                        double s5 = 2.0 * t2 * t2;
                        /* a = 0 */
                        for (int b2 = 0; b2 < n4; b2 += 2 * dual2) {
                            int i4 = 2 * b2;
                            int j2 = 2 * (b2 + dual2);
                            double wd_real2 = x[j2];
                            double wd_imag2 = x[j2 + 1];
                            x[j2] = x[i4] - wd_real2;
                            x[j2 + 1] = x[i4 + 1] - wd_imag2;
                            x[i4] += wd_real2;
                            x[i4 + 1] += wd_imag2;
                        }
                        /* a = 1 .. (dual-1) */
                        for (int a2 = 1; a2 < dual2; a2++) {
                            /* trignometric recurrence for w-> exp(i theta) w */
                            {
                                double tmp_real2 = w_real2 - s4 * w_imag2 - s5 * w_real2;
                                double tmp_imag2 = w_imag2 + s4 * w_real2 - s5 * w_imag2;
                                w_real2 = tmp_real2;
                                w_imag2 = tmp_imag2;
                            }
                            for (int b2 = 0; b2 < n4; b2 += 2 * dual2) {
                                int i4 = 2 * (b2 + a2);
                                int j2 = 2 * (b2 + a2 + dual2);
                                double z1_real2 = x[j2];
                                double z1_imag2 = x[j2 + 1];
                                double wd_real2 = w_real2 * z1_real2 - w_imag2 * z1_imag2;
                                double wd_imag2 = w_real2 * z1_imag2 + w_imag2 * z1_real2;
                                x[j2] = x[i4] - wd_real2;
                                x[j2 + 1] = x[i4 + 1] - wd_imag2;
                                x[i4] += wd_real2;
                                x[i4 + 1] += wd_imag2;
                            }
                        }
                    }
                }
            }
            int nd = x.length;
            int n = nd / 2;
            double norm = 1 / ((double) n);
            for (int i = 0; i < nd; i++)
                x[i] *= norm;
            if (Q.running) {
                Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
                Q.running = false;
            }
            final double EPS = 1.0e-10;
            int nd2 = x.length;
            double copy[] = new double[nd2];
            System.arraycopy(x, 0, copy, 0, nd2);
            if (x.length != 0) {
                int n3 = x.length / 2;
                if (n3 != 1) {
                    int log1 = 0;
                    for (int k1 = 1; k1 < n3; k1 *= 2, log1++)
                        ;
                    if (n3 != (1 << log1))
                        throw new Error("FFT: Data length is not a power of 2!: " + n3);
                    int logn1 = log1;/* bit reverse the input data for decimation in time algorithm */
                    /* apply fft recursion */
                    /* this loop executed log2(N) times */
                    /* This is the Goldrader bit-reversal algorithm */
                    int n1 = x.length / 2;
                    int nm1 = n1 - 1;
                    int i = 0;
                    int j = 0;
                    for (; i < nm1; i++) {
                        int ii = i << 1;
                        int jj = j << 1;
                        int k = n1 >> 1;
                        if (i < j) {
                            double tmp_real = x[ii];
                            double tmp_imag = x[ii + 1];
                            x[ii] = x[jj];
                            x[ii + 1] = x[jj + 1];
                            x[jj] = tmp_real;
                            x[jj + 1] = tmp_imag;
                        }
                        while (k <= j) {
                            j -= k;
                            k >>= 1;
                        }
                        j += k;
                    }
                    for (int bit1 = 0, dual1 = 1; bit1 < logn1; bit1++, dual1 *= 2) {
                        double w_real1 = 1.0;
                        double w_imag1 = 0.0;
                        double theta1 = 2.0 * -1 * Math.PI / (2.0 * (double) dual1);
                        double s1 = Math.sin(theta1);
                        double t1 = Math.sin(theta1 / 2.0);
                        double s3 = 2.0 * t1 * t1;
                        /* a = 0 */
                        for (int b1 = 0; b1 < n3; b1 += 2 * dual1) {
                            int i3 = 2 * b1;
                            int j1 = 2 * (b1 + dual1);
                            double wd_real1 = x[j1];
                            double wd_imag1 = x[j1 + 1];
                            x[j1] = x[i3] - wd_real1;
                            x[j1 + 1] = x[i3 + 1] - wd_imag1;
                            x[i3] += wd_real1;
                            x[i3 + 1] += wd_imag1;
                        }
                        /* a = 1 .. (dual-1) */
                        for (int a1 = 1; a1 < dual1; a1++) {
                            /* trignometric recurrence for w-> exp(i theta) w */
                            {
                                double tmp_real1 = w_real1 - s1 * w_imag1 - s3 * w_real1;
                                double tmp_imag1 = w_imag1 + s1 * w_real1 - s3 * w_imag1;
                                w_real1 = tmp_real1;
                                w_imag1 = tmp_imag1;
                            }
                            for (int b1 = 0; b1 < n3; b1 += 2 * dual1) {
                                int i3 = 2 * (b1 + a1);
                                int j1 = 2 * (b1 + a1 + dual1);
                                double z1_real1 = x[j1];
                                double z1_imag1 = x[j1 + 1];
                                double wd_real1 = w_real1 * z1_real1 - w_imag1 * z1_imag1;
                                double wd_imag1 = w_real1 * z1_imag1 + w_imag1 * z1_real1;
                                x[j1] = x[i3] - wd_real1;
                                x[j1 + 1] = x[i3 + 1] - wd_imag1;
                                x[i3] += wd_real1;
                                x[i3 + 1] += wd_imag1;
                            }
                        }
                    }
                }
            }
            if (x.length != 0) {
                int n2 = x.length / 2;
                if (n2 != 1) {
                    int log = 0;
                    for (int k = 1; k < n2; k *= 2, log++)
                        ;
                    if (n2 != (1 << log))
                        throw new Error("FFT: Data length is not a power of 2!: " + n2);
                    int logn = log;/* bit reverse the input data for decimation in time algorithm */
                    /* apply fft recursion */
                    /* this loop executed log2(N) times */
                    /* This is the Goldrader bit-reversal algorithm */
                    int n1 = x.length / 2;
                    int nm1 = n1 - 1;
                    int i = 0;
                    int j1 = 0;
                    for (; i < nm1; i++) {
                        int ii = i << 1;
                        int jj = j1 << 1;
                        int k = n1 >> 1;
                        if (i < j1) {
                            double tmp_real1 = x[ii];
                            double tmp_imag1 = x[ii + 1];
                            x[ii] = x[jj];
                            x[ii + 1] = x[jj + 1];
                            x[jj] = tmp_real1;
                            x[jj + 1] = tmp_imag1;
                        }
                        while (k <= j1) {
                            j1 -= k;
                            k >>= 1;
                        }
                        j1 += k;
                    }
                    for (int bit = 0, dual = 1; bit < logn; bit++, dual *= 2) {
                        double w_real = 1.0;
                        double w_imag = 0.0;
                        double theta = 2.0 * +1 * Math.PI / (2.0 * (double) dual);
                        double s = Math.sin(theta);
                        double t = Math.sin(theta / 2.0);
                        double s2 = 2.0 * t * t;
                        /* a = 0 */
                        for (int b = 0; b < n2; b += 2 * dual) {
                            int i2 = 2 * b;
                            int j = 2 * (b + dual);
                            double wd_real = x[j];
                            double wd_imag = x[j + 1];
                            x[j] = x[i2] - wd_real;
                            x[j + 1] = x[i2 + 1] - wd_imag;
                            x[i2] += wd_real;
                            x[i2 + 1] += wd_imag;
                        }
                        /* a = 1 .. (dual-1) */
                        for (int a = 1; a < dual; a++) {
                            /* trignometric recurrence for w-> exp(i theta) w */
                            {
                                double tmp_real = w_real - s * w_imag - s2 * w_real;
                                double tmp_imag = w_imag + s * w_real - s2 * w_imag;
                                w_real = tmp_real;
                                w_imag = tmp_imag;
                            }
                            for (int b = 0; b < n2; b += 2 * dual) {
                                int i2 = 2 * (b + a);
                                int j = 2 * (b + a + dual);
                                double z1_real = x[j];
                                double z1_imag = x[j + 1];
                                double wd_real = w_real * z1_real - w_imag * z1_imag;
                                double wd_imag = w_real * z1_imag + w_imag * z1_real;
                                x[j] = x[i2] - wd_real;
                                x[j + 1] = x[i2 + 1] - wd_imag;
                                x[i2] += wd_real;
                                x[i2 + 1] += wd_imag;
                            }
                        }
                    }
                }
            }
            int nd1 = x.length;
            int n1 = nd1 / 2;
            double norm1 = 1 / ((double) n1);
            for (int i1 = 0; i1 < nd1; i1++)
                x[i1] *= norm1;
            double diff = 0.0;
            for (int i = 0; i < nd2; i++) {
                double d = x[i] - copy[i];
                diff += d * d;
            }
            double fftTest = Math.sqrt(diff / nd2);
            if (kernel.CURRENT_FFT_RESULT.equals("" + fftTest)) {
                if (fft.id == 1) {
                    System.out.println(Constants.VERIFICATION_PASSED_MESSAGE);
                }
            } else {
                System.out.println(Constants.VERIFICATION_FAILED_MESSAGE
                        + "The result is " + "" + fftTest + "  instead of " + kernel.CURRENT_FFT_RESULT);
            }
            if (!(fftTest / FFT_size > EPS)) {
                x = null;
                if (Q.running) {
                    Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
                    Q.last_time = (System.currentTimeMillis() * 0.001);
                }
                result = fft.num_flops(FFT_size) * cycles / Q.total * 1.0e-6;
            }
            res = result;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ______________________________________________________________________ */
    protected int log2(int n) {
        int log = 0;
        for (int k = 1; k < n; k *= 2, log++)
            ;
        if (n != (1 << log))
            throw new Error("FFT: Data length is not a power of 2!: " + n);
        return log;
    }

    protected void transform_internal(double data[], int direction) {
        if (data.length == 0)
            return;
        int n = data.length / 2;
        if (n == 1)
            return;
        int log = 0;
        for (int k = 1; k < n; k *= 2, log++)
            ;
        if (n != (1 << log))
            throw new Error("FFT: Data length is not a power of 2!: " + n);
        int logn = log;
        /* bit reverse the input data for decimation in time algorithm */
        /* This is the Goldrader bit-reversal algorithm */
        int n1 = data.length / 2;
        int nm1 = n1 - 1;
        int i1 = 0;
        int j1 = 0;
        for (; i1 < nm1; i1++) {
            int ii = i1 << 1;
            int jj = j1 << 1;
            int k = n1 >> 1;
            if (i1 < j1) {
                double tmp_real1 = data[ii];
                double tmp_imag1 = data[ii + 1];
                data[ii] = data[jj];
                data[ii + 1] = data[jj + 1];
                data[jj] = tmp_real1;
                data[jj + 1] = tmp_imag1;
            }
            while (k <= j1) {
                j1 -= k;
                k >>= 1;
            }
            j1 += k;
        }
        /* apply fft recursion */
        /* this loop executed log2(N) times */
        for (int bit = 0, dual = 1; bit < logn; bit++, dual *= 2) {
            double w_real = 1.0;
            double w_imag = 0.0;
            double theta = 2.0 * direction * Math.PI / (2.0 * (double) dual);
            double s = Math.sin(theta);
            double t = Math.sin(theta / 2.0);
            double s2 = 2.0 * t * t;
            /* a = 0 */
            for (int b = 0; b < n; b += 2 * dual) {
                int i = 2 * b;
                int j = 2 * (b + dual);
                double wd_real = data[j];
                double wd_imag = data[j + 1];
                data[j] = data[i] - wd_real;
                data[j + 1] = data[i + 1] - wd_imag;
                data[i] += wd_real;
                data[i + 1] += wd_imag;
            }
            /* a = 1 .. (dual-1) */
            for (int a = 1; a < dual; a++) {
                /* trignometric recurrence for w-> exp(i theta) w */
                {
                    double tmp_real = w_real - s * w_imag - s2 * w_real;
                    double tmp_imag = w_imag + s * w_real - s2 * w_imag;
                    w_real = tmp_real;
                    w_imag = tmp_imag;
                }
                for (int b = 0; b < n; b += 2 * dual) {
                    int i = 2 * (b + a);
                    int j = 2 * (b + a + dual);
                    double z1_real = data[j];
                    double z1_imag = data[j + 1];
                    double wd_real = w_real * z1_real - w_imag * z1_imag;
                    double wd_imag = w_real * z1_imag + w_imag * z1_real;
                    data[j] = data[i] - wd_real;
                    data[j + 1] = data[i + 1] - wd_imag;
                    data[i] += wd_real;
                    data[i + 1] += wd_imag;
                }
            }
        }
    }

    protected void bitreverse(double data[]) {
        /* This is the Goldrader bit-reversal algorithm */
        int n = data.length / 2;
        int nm1 = n - 1;
        int i = 0;
        int j = 0;
        for (; i < nm1; i++) {
            int ii = i << 1;
            int jj = j << 1;
            int k = n >> 1;
            if (i < j) {
                double tmp_real = data[ii];
                double tmp_imag = data[ii + 1];
                data[ii] = data[jj];
                data[ii + 1] = data[jj + 1];
                data[jj] = tmp_real;
                data[jj + 1] = tmp_imag;
            }
            while (k <= j) {
                j -= k;
                k >>= 1;
            }
            j += k;
        }
    }

    public static final ThreadLocal<double[]> threadLocalVector = new ThreadLocal<double[]>() {
        @Override
        protected double[] initialValue() {
            return new double[kernel.CURRENT_FFT_SIZE];
        }
    };

    public double measureFFT(int N, double mintime, Random R) {
        double x[];
        x = threadLocalVector.get();
        if (x.length != N) {
            x = new double[N];
            threadLocalVector.set(x);
        }
        for (int i6 = 0; i6 < x.length; i6++) {
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
            x[i6] = result;
        }
        x = x;
        long cycles = 1;
        Stopwatch Q = new Stopwatch();
        if (!Q.running) {
            Q.running = true;
            Q.total = 0.0;
            Q.last_time = (System.currentTimeMillis() * 0.001);
        }
        if (x.length != 0) {
            int n5 = x.length / 2;
            if (n5 != 1) {
                int log3 = 0;
                for (int k3 = 1; k3 < n5; k3 *= 2, log3++)
                    ;
                if (n5 != (1 << log3))
                    throw new Error("FFT: Data length is not a power of 2!: " + n5);
                int logn3 = log3;/* bit reverse the input data for decimation in time algorithm */
                /* apply fft recursion */
                /* this loop executed log2(N) times */
                /* This is the Goldrader bit-reversal algorithm */
                int n = x.length / 2;
                int nm1 = n - 1;
                int i = 0;
                int j = 0;
                for (; i < nm1; i++) {
                    int ii = i << 1;
                    int jj = j << 1;
                    int k = n >> 1;
                    if (i < j) {
                        double tmp_real = x[ii];
                        double tmp_imag = x[ii + 1];
                        x[ii] = x[jj];
                        x[ii + 1] = x[jj + 1];
                        x[jj] = tmp_real;
                        x[jj + 1] = tmp_imag;
                    }
                    while (k <= j) {
                        j -= k;
                        k >>= 1;
                    }
                    j += k;
                }
                for (int bit3 = 0, dual3 = 1; bit3 < logn3; bit3++, dual3 *= 2) {
                    double w_real3 = 1.0;
                    double w_imag3 = 0.0;
                    double theta3 = 2.0 * -1 * Math.PI / (2.0 * (double) dual3);
                    double s6 = Math.sin(theta3);
                    double t3 = Math.sin(theta3 / 2.0);
                    double s7 = 2.0 * t3 * t3;
                    /* a = 0 */
                    for (int b3 = 0; b3 < n5; b3 += 2 * dual3) {
                        int i5 = 2 * b3;
                        int j3 = 2 * (b3 + dual3);
                        double wd_real3 = x[j3];
                        double wd_imag3 = x[j3 + 1];
                        x[j3] = x[i5] - wd_real3;
                        x[j3 + 1] = x[i5 + 1] - wd_imag3;
                        x[i5] += wd_real3;
                        x[i5 + 1] += wd_imag3;
                    }
                    /* a = 1 .. (dual-1) */
                    for (int a3 = 1; a3 < dual3; a3++) {
                        /* trignometric recurrence for w-> exp(i theta) w */
                        {
                            double tmp_real3 = w_real3 - s6 * w_imag3 - s7 * w_real3;
                            double tmp_imag3 = w_imag3 + s6 * w_real3 - s7 * w_imag3;
                            w_real3 = tmp_real3;
                            w_imag3 = tmp_imag3;
                        }
                        for (int b3 = 0; b3 < n5; b3 += 2 * dual3) {
                            int i5 = 2 * (b3 + a3);
                            int j3 = 2 * (b3 + a3 + dual3);
                            double z1_real3 = x[j3];
                            double z1_imag3 = x[j3 + 1];
                            double wd_real3 = w_real3 * z1_real3 - w_imag3 * z1_imag3;
                            double wd_imag3 = w_real3 * z1_imag3 + w_imag3 * z1_real3;
                            x[j3] = x[i5] - wd_real3;
                            x[j3 + 1] = x[i5 + 1] - wd_imag3;
                            x[i5] += wd_real3;
                            x[i5 + 1] += wd_imag3;
                        }
                    }
                }
            }
        }
        if (x.length != 0) {
            int n4 = x.length / 2;
            if (n4 != 1) {
                int log2 = 0;
                for (int k2 = 1; k2 < n4; k2 *= 2, log2++)
                    ;
                if (n4 != (1 << log2))
                    throw new Error("FFT: Data length is not a power of 2!: " + n4);
                int logn2 = log2;/* bit reverse the input data for decimation in time algorithm */
                /* apply fft recursion */
                /* this loop executed log2(N) times */
                /* This is the Goldrader bit-reversal algorithm */
                int n = x.length / 2;
                int nm1 = n - 1;
                int i = 0;
                int j = 0;
                for (; i < nm1; i++) {
                    int ii = i << 1;
                    int jj = j << 1;
                    int k = n >> 1;
                    if (i < j) {
                        double tmp_real = x[ii];
                        double tmp_imag = x[ii + 1];
                        x[ii] = x[jj];
                        x[ii + 1] = x[jj + 1];
                        x[jj] = tmp_real;
                        x[jj + 1] = tmp_imag;
                    }
                    while (k <= j) {
                        j -= k;
                        k >>= 1;
                    }
                    j += k;
                }
                for (int bit2 = 0, dual2 = 1; bit2 < logn2; bit2++, dual2 *= 2) {
                    double w_real2 = 1.0;
                    double w_imag2 = 0.0;
                    double theta2 = 2.0 * +1 * Math.PI / (2.0 * (double) dual2);
                    double s4 = Math.sin(theta2);
                    double t2 = Math.sin(theta2 / 2.0);
                    double s5 = 2.0 * t2 * t2;
                    /* a = 0 */
                    for (int b2 = 0; b2 < n4; b2 += 2 * dual2) {
                        int i4 = 2 * b2;
                        int j2 = 2 * (b2 + dual2);
                        double wd_real2 = x[j2];
                        double wd_imag2 = x[j2 + 1];
                        x[j2] = x[i4] - wd_real2;
                        x[j2 + 1] = x[i4 + 1] - wd_imag2;
                        x[i4] += wd_real2;
                        x[i4 + 1] += wd_imag2;
                    }
                    /* a = 1 .. (dual-1) */
                    for (int a2 = 1; a2 < dual2; a2++) {
                        /* trignometric recurrence for w-> exp(i theta) w */
                        {
                            double tmp_real2 = w_real2 - s4 * w_imag2 - s5 * w_real2;
                            double tmp_imag2 = w_imag2 + s4 * w_real2 - s5 * w_imag2;
                            w_real2 = tmp_real2;
                            w_imag2 = tmp_imag2;
                        }
                        for (int b2 = 0; b2 < n4; b2 += 2 * dual2) {
                            int i4 = 2 * (b2 + a2);
                            int j2 = 2 * (b2 + a2 + dual2);
                            double z1_real2 = x[j2];
                            double z1_imag2 = x[j2 + 1];
                            double wd_real2 = w_real2 * z1_real2 - w_imag2 * z1_imag2;
                            double wd_imag2 = w_real2 * z1_imag2 + w_imag2 * z1_real2;
                            x[j2] = x[i4] - wd_real2;
                            x[j2 + 1] = x[i4 + 1] - wd_imag2;
                            x[i4] += wd_real2;
                            x[i4 + 1] += wd_imag2;
                        }
                    }
                }
            }
        }
        int nd = x.length;
        int n = nd / 2;
        double norm = 1 / ((double) n);
        for (int i = 0; i < nd; i++)
            x[i] *= norm;
        if (Q.running) {
            Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
            Q.running = false;
        }
        final double EPS = 1.0e-10;
        int nd2 = x.length;
        double copy[] = new double[nd2];
        System.arraycopy(x, 0, copy, 0, nd2);
        if (x.length != 0) {
            int n3 = x.length / 2;
            if (n3 != 1) {
                int log1 = 0;
                for (int k1 = 1; k1 < n3; k1 *= 2, log1++)
                    ;
                if (n3 != (1 << log1))
                    throw new Error("FFT: Data length is not a power of 2!: " + n3);
                int logn1 = log1;/* bit reverse the input data for decimation in time algorithm */
                /* apply fft recursion */
                /* this loop executed log2(N) times */
                /* This is the Goldrader bit-reversal algorithm */
                int n1 = x.length / 2;
                int nm1 = n1 - 1;
                int i = 0;
                int j = 0;
                for (; i < nm1; i++) {
                    int ii = i << 1;
                    int jj = j << 1;
                    int k = n1 >> 1;
                    if (i < j) {
                        double tmp_real = x[ii];
                        double tmp_imag = x[ii + 1];
                        x[ii] = x[jj];
                        x[ii + 1] = x[jj + 1];
                        x[jj] = tmp_real;
                        x[jj + 1] = tmp_imag;
                    }
                    while (k <= j) {
                        j -= k;
                        k >>= 1;
                    }
                    j += k;
                }
                for (int bit1 = 0, dual1 = 1; bit1 < logn1; bit1++, dual1 *= 2) {
                    double w_real1 = 1.0;
                    double w_imag1 = 0.0;
                    double theta1 = 2.0 * -1 * Math.PI / (2.0 * (double) dual1);
                    double s1 = Math.sin(theta1);
                    double t1 = Math.sin(theta1 / 2.0);
                    double s3 = 2.0 * t1 * t1;
                    /* a = 0 */
                    for (int b1 = 0; b1 < n3; b1 += 2 * dual1) {
                        int i3 = 2 * b1;
                        int j1 = 2 * (b1 + dual1);
                        double wd_real1 = x[j1];
                        double wd_imag1 = x[j1 + 1];
                        x[j1] = x[i3] - wd_real1;
                        x[j1 + 1] = x[i3 + 1] - wd_imag1;
                        x[i3] += wd_real1;
                        x[i3 + 1] += wd_imag1;
                    }
                    /* a = 1 .. (dual-1) */
                    for (int a1 = 1; a1 < dual1; a1++) {
                        /* trignometric recurrence for w-> exp(i theta) w */
                        {
                            double tmp_real1 = w_real1 - s1 * w_imag1 - s3 * w_real1;
                            double tmp_imag1 = w_imag1 + s1 * w_real1 - s3 * w_imag1;
                            w_real1 = tmp_real1;
                            w_imag1 = tmp_imag1;
                        }
                        for (int b1 = 0; b1 < n3; b1 += 2 * dual1) {
                            int i3 = 2 * (b1 + a1);
                            int j1 = 2 * (b1 + a1 + dual1);
                            double z1_real1 = x[j1];
                            double z1_imag1 = x[j1 + 1];
                            double wd_real1 = w_real1 * z1_real1 - w_imag1 * z1_imag1;
                            double wd_imag1 = w_real1 * z1_imag1 + w_imag1 * z1_real1;
                            x[j1] = x[i3] - wd_real1;
                            x[j1 + 1] = x[i3 + 1] - wd_imag1;
                            x[i3] += wd_real1;
                            x[i3 + 1] += wd_imag1;
                        }
                    }
                }
            }
        }
        if (x.length != 0) {
            int n2 = x.length / 2;
            if (n2 != 1) {
                int log = 0;
                for (int k = 1; k < n2; k *= 2, log++)
                    ;
                if (n2 != (1 << log))
                    throw new Error("FFT: Data length is not a power of 2!: " + n2);
                int logn = log;/* bit reverse the input data for decimation in time algorithm */
                /* apply fft recursion */
                /* this loop executed log2(N) times */
                /* This is the Goldrader bit-reversal algorithm */
                int n1 = x.length / 2;
                int nm1 = n1 - 1;
                int i = 0;
                int j1 = 0;
                for (; i < nm1; i++) {
                    int ii = i << 1;
                    int jj = j1 << 1;
                    int k = n1 >> 1;
                    if (i < j1) {
                        double tmp_real1 = x[ii];
                        double tmp_imag1 = x[ii + 1];
                        x[ii] = x[jj];
                        x[ii + 1] = x[jj + 1];
                        x[jj] = tmp_real1;
                        x[jj + 1] = tmp_imag1;
                    }
                    while (k <= j1) {
                        j1 -= k;
                        k >>= 1;
                    }
                    j1 += k;
                }
                for (int bit = 0, dual = 1; bit < logn; bit++, dual *= 2) {
                    double w_real = 1.0;
                    double w_imag = 0.0;
                    double theta = 2.0 * +1 * Math.PI / (2.0 * (double) dual);
                    double s = Math.sin(theta);
                    double t = Math.sin(theta / 2.0);
                    double s2 = 2.0 * t * t;
                    /* a = 0 */
                    for (int b = 0; b < n2; b += 2 * dual) {
                        int i2 = 2 * b;
                        int j = 2 * (b + dual);
                        double wd_real = x[j];
                        double wd_imag = x[j + 1];
                        x[j] = x[i2] - wd_real;
                        x[j + 1] = x[i2 + 1] - wd_imag;
                        x[i2] += wd_real;
                        x[i2 + 1] += wd_imag;
                    }
                    /* a = 1 .. (dual-1) */
                    for (int a = 1; a < dual; a++) {
                        /* trignometric recurrence for w-> exp(i theta) w */
                        {
                            double tmp_real = w_real - s * w_imag - s2 * w_real;
                            double tmp_imag = w_imag + s * w_real - s2 * w_imag;
                            w_real = tmp_real;
                            w_imag = tmp_imag;
                        }
                        for (int b = 0; b < n2; b += 2 * dual) {
                            int i2 = 2 * (b + a);
                            int j = 2 * (b + a + dual);
                            double z1_real = x[j];
                            double z1_imag = x[j + 1];
                            double wd_real = w_real * z1_real - w_imag * z1_imag;
                            double wd_imag = w_real * z1_imag + w_imag * z1_real;
                            x[j] = x[i2] - wd_real;
                            x[j + 1] = x[i2 + 1] - wd_imag;
                            x[i2] += wd_real;
                            x[i2 + 1] += wd_imag;
                        }
                    }
                }
            }
        }
        int nd1 = x.length;
        int n1 = nd1 / 2;
        double norm1 = 1 / ((double) n1);
        for (int i1 = 0; i1 < nd1; i1++)
            x[i1] *= norm1;
        double diff = 0.0;
        for (int i = 0; i < nd2; i++) {
            double d = x[i] - copy[i];
            diff += d * d;
        }
        double fftTest = Math.sqrt(diff / nd2);
        if (kernel.CURRENT_FFT_RESULT.equals("" + fftTest)) {
            if (id == 1) {
                System.out.println(Constants.VERIFICATION_PASSED_MESSAGE);
            }
        } else {
            System.out.println(Constants.VERIFICATION_FAILED_MESSAGE
                    + "The result is " + "" + fftTest + "  instead of " + kernel.CURRENT_FFT_RESULT);
        }
        if (fftTest / N > EPS)
            return 0.0;
        x = null;
        if (Q.running) {
            Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
            Q.last_time = (System.currentTimeMillis() * 0.001);
        }
        return num_flops(N) * cycles / Q.total * 1.0e-6;
    }

    public void run() {
        try {
            double min_time = Constants.RESOLUTION_DEFAULT;
            int FFT_size = kernel.CURRENT_FFT_SIZE;
            double res = 0.0;
            Random R = new Random(Constants.RANDOM_SEED);
            double result = 0.0;
            double x[];
            x = threadLocalVector.get();
            if (x.length != FFT_size) {
                x = new double[FFT_size];
                threadLocalVector.set(x);
            }
            for (int i6 = 0; i6 < x.length; i6++) {
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
                x[i6] = result1;
            }
            x = x;
            long cycles = 1;
            Stopwatch Q = new Stopwatch();
            if (!Q.running) {
                Q.running = true;
                Q.total = 0.0;
                Q.last_time = (System.currentTimeMillis() * 0.001);
            }
            if (x.length != 0) {
                int n5 = x.length / 2;
                if (n5 != 1) {
                    int log3 = 0;
                    for (int k3 = 1; k3 < n5; k3 *= 2, log3++)
                        ;
                    if (n5 != (1 << log3))
                        throw new Error("FFT: Data length is not a power of 2!: " + n5);
                    int logn3 = log3;/* bit reverse the input data for decimation in time algorithm */
                    /* apply fft recursion */
                    /* this loop executed log2(N) times */
                    /* This is the Goldrader bit-reversal algorithm */
                    int n = x.length / 2;
                    int nm1 = n - 1;
                    int i = 0;
                    int j = 0;
                    for (; i < nm1; i++) {
                        int ii = i << 1;
                        int jj = j << 1;
                        int k = n >> 1;
                        if (i < j) {
                            double tmp_real = x[ii];
                            double tmp_imag = x[ii + 1];
                            x[ii] = x[jj];
                            x[ii + 1] = x[jj + 1];
                            x[jj] = tmp_real;
                            x[jj + 1] = tmp_imag;
                        }
                        while (k <= j) {
                            j -= k;
                            k >>= 1;
                        }
                        j += k;
                    }
                    for (int bit3 = 0, dual3 = 1; bit3 < logn3; bit3++, dual3 *= 2) {
                        double w_real3 = 1.0;
                        double w_imag3 = 0.0;
                        double theta3 = 2.0 * -1 * Math.PI / (2.0 * (double) dual3);
                        double s6 = Math.sin(theta3);
                        double t3 = Math.sin(theta3 / 2.0);
                        double s7 = 2.0 * t3 * t3;
                        /* a = 0 */
                        for (int b3 = 0; b3 < n5; b3 += 2 * dual3) {
                            int i5 = 2 * b3;
                            int j3 = 2 * (b3 + dual3);
                            double wd_real3 = x[j3];
                            double wd_imag3 = x[j3 + 1];
                            x[j3] = x[i5] - wd_real3;
                            x[j3 + 1] = x[i5 + 1] - wd_imag3;
                            x[i5] += wd_real3;
                            x[i5 + 1] += wd_imag3;
                        }
                        /* a = 1 .. (dual-1) */
                        for (int a3 = 1; a3 < dual3; a3++) {
                            /* trignometric recurrence for w-> exp(i theta) w */
                            {
                                double tmp_real3 = w_real3 - s6 * w_imag3 - s7 * w_real3;
                                double tmp_imag3 = w_imag3 + s6 * w_real3 - s7 * w_imag3;
                                w_real3 = tmp_real3;
                                w_imag3 = tmp_imag3;
                            }
                            for (int b3 = 0; b3 < n5; b3 += 2 * dual3) {
                                int i5 = 2 * (b3 + a3);
                                int j3 = 2 * (b3 + a3 + dual3);
                                double z1_real3 = x[j3];
                                double z1_imag3 = x[j3 + 1];
                                double wd_real3 = w_real3 * z1_real3 - w_imag3 * z1_imag3;
                                double wd_imag3 = w_real3 * z1_imag3 + w_imag3 * z1_real3;
                                x[j3] = x[i5] - wd_real3;
                                x[j3 + 1] = x[i5 + 1] - wd_imag3;
                                x[i5] += wd_real3;
                                x[i5 + 1] += wd_imag3;
                            }
                        }
                    }
                }
            }
            if (x.length != 0) {
                int n4 = x.length / 2;
                if (n4 != 1) {
                    int log2 = 0;
                    for (int k2 = 1; k2 < n4; k2 *= 2, log2++)
                        ;
                    if (n4 != (1 << log2))
                        throw new Error("FFT: Data length is not a power of 2!: " + n4);
                    int logn2 = log2;/* bit reverse the input data for decimation in time algorithm */
                    /* apply fft recursion */
                    /* this loop executed log2(N) times */
                    /* This is the Goldrader bit-reversal algorithm */
                    int n = x.length / 2;
                    int nm1 = n - 1;
                    int i = 0;
                    int j = 0;
                    for (; i < nm1; i++) {
                        int ii = i << 1;
                        int jj = j << 1;
                        int k = n >> 1;
                        if (i < j) {
                            double tmp_real = x[ii];
                            double tmp_imag = x[ii + 1];
                            x[ii] = x[jj];
                            x[ii + 1] = x[jj + 1];
                            x[jj] = tmp_real;
                            x[jj + 1] = tmp_imag;
                        }
                        while (k <= j) {
                            j -= k;
                            k >>= 1;
                        }
                        j += k;
                    }
                    for (int bit2 = 0, dual2 = 1; bit2 < logn2; bit2++, dual2 *= 2) {
                        double w_real2 = 1.0;
                        double w_imag2 = 0.0;
                        double theta2 = 2.0 * +1 * Math.PI / (2.0 * (double) dual2);
                        double s4 = Math.sin(theta2);
                        double t2 = Math.sin(theta2 / 2.0);
                        double s5 = 2.0 * t2 * t2;
                        /* a = 0 */
                        for (int b2 = 0; b2 < n4; b2 += 2 * dual2) {
                            int i4 = 2 * b2;
                            int j2 = 2 * (b2 + dual2);
                            double wd_real2 = x[j2];
                            double wd_imag2 = x[j2 + 1];
                            x[j2] = x[i4] - wd_real2;
                            x[j2 + 1] = x[i4 + 1] - wd_imag2;
                            x[i4] += wd_real2;
                            x[i4 + 1] += wd_imag2;
                        }
                        /* a = 1 .. (dual-1) */
                        for (int a2 = 1; a2 < dual2; a2++) {
                            /* trignometric recurrence for w-> exp(i theta) w */
                            {
                                double tmp_real2 = w_real2 - s4 * w_imag2 - s5 * w_real2;
                                double tmp_imag2 = w_imag2 + s4 * w_real2 - s5 * w_imag2;
                                w_real2 = tmp_real2;
                                w_imag2 = tmp_imag2;
                            }
                            for (int b2 = 0; b2 < n4; b2 += 2 * dual2) {
                                int i4 = 2 * (b2 + a2);
                                int j2 = 2 * (b2 + a2 + dual2);
                                double z1_real2 = x[j2];
                                double z1_imag2 = x[j2 + 1];
                                double wd_real2 = w_real2 * z1_real2 - w_imag2 * z1_imag2;
                                double wd_imag2 = w_real2 * z1_imag2 + w_imag2 * z1_real2;
                                x[j2] = x[i4] - wd_real2;
                                x[j2 + 1] = x[i4 + 1] - wd_imag2;
                                x[i4] += wd_real2;
                                x[i4 + 1] += wd_imag2;
                            }
                        }
                    }
                }
            }
            int nd = x.length;
            int n = nd / 2;
            double norm = 1 / ((double) n);
            for (int i = 0; i < nd; i++)
                x[i] *= norm;
            if (Q.running) {
                Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
                Q.running = false;
            }
            final double EPS = 1.0e-10;
            int nd2 = x.length;
            double copy[] = new double[nd2];
            System.arraycopy(x, 0, copy, 0, nd2);
            if (x.length != 0) {
                int n3 = x.length / 2;
                if (n3 != 1) {
                    int log1 = 0;
                    for (int k1 = 1; k1 < n3; k1 *= 2, log1++)
                        ;
                    if (n3 != (1 << log1))
                        throw new Error("FFT: Data length is not a power of 2!: " + n3);
                    int logn1 = log1;/* bit reverse the input data for decimation in time algorithm */
                    /* apply fft recursion */
                    /* this loop executed log2(N) times */
                    /* This is the Goldrader bit-reversal algorithm */
                    int n1 = x.length / 2;
                    int nm1 = n1 - 1;
                    int i = 0;
                    int j = 0;
                    for (; i < nm1; i++) {
                        int ii = i << 1;
                        int jj = j << 1;
                        int k = n1 >> 1;
                        if (i < j) {
                            double tmp_real = x[ii];
                            double tmp_imag = x[ii + 1];
                            x[ii] = x[jj];
                            x[ii + 1] = x[jj + 1];
                            x[jj] = tmp_real;
                            x[jj + 1] = tmp_imag;
                        }
                        while (k <= j) {
                            j -= k;
                            k >>= 1;
                        }
                        j += k;
                    }
                    for (int bit1 = 0, dual1 = 1; bit1 < logn1; bit1++, dual1 *= 2) {
                        double w_real1 = 1.0;
                        double w_imag1 = 0.0;
                        double theta1 = 2.0 * -1 * Math.PI / (2.0 * (double) dual1);
                        double s1 = Math.sin(theta1);
                        double t1 = Math.sin(theta1 / 2.0);
                        double s3 = 2.0 * t1 * t1;
                        /* a = 0 */
                        for (int b1 = 0; b1 < n3; b1 += 2 * dual1) {
                            int i3 = 2 * b1;
                            int j1 = 2 * (b1 + dual1);
                            double wd_real1 = x[j1];
                            double wd_imag1 = x[j1 + 1];
                            x[j1] = x[i3] - wd_real1;
                            x[j1 + 1] = x[i3 + 1] - wd_imag1;
                            x[i3] += wd_real1;
                            x[i3 + 1] += wd_imag1;
                        }
                        /* a = 1 .. (dual-1) */
                        for (int a1 = 1; a1 < dual1; a1++) {
                            /* trignometric recurrence for w-> exp(i theta) w */
                            {
                                double tmp_real1 = w_real1 - s1 * w_imag1 - s3 * w_real1;
                                double tmp_imag1 = w_imag1 + s1 * w_real1 - s3 * w_imag1;
                                w_real1 = tmp_real1;
                                w_imag1 = tmp_imag1;
                            }
                            for (int b1 = 0; b1 < n3; b1 += 2 * dual1) {
                                int i3 = 2 * (b1 + a1);
                                int j1 = 2 * (b1 + a1 + dual1);
                                double z1_real1 = x[j1];
                                double z1_imag1 = x[j1 + 1];
                                double wd_real1 = w_real1 * z1_real1 - w_imag1 * z1_imag1;
                                double wd_imag1 = w_real1 * z1_imag1 + w_imag1 * z1_real1;
                                x[j1] = x[i3] - wd_real1;
                                x[j1 + 1] = x[i3 + 1] - wd_imag1;
                                x[i3] += wd_real1;
                                x[i3 + 1] += wd_imag1;
                            }
                        }
                    }
                }
            }
            if (x.length != 0) {
                int n2 = x.length / 2;
                if (n2 != 1) {
                    int log = 0;
                    for (int k = 1; k < n2; k *= 2, log++)
                        ;
                    if (n2 != (1 << log))
                        throw new Error("FFT: Data length is not a power of 2!: " + n2);
                    int logn = log;/* bit reverse the input data for decimation in time algorithm */
                    /* apply fft recursion */
                    /* this loop executed log2(N) times */
                    /* This is the Goldrader bit-reversal algorithm */
                    int n1 = x.length / 2;
                    int nm1 = n1 - 1;
                    int i = 0;
                    int j1 = 0;
                    for (; i < nm1; i++) {
                        int ii = i << 1;
                        int jj = j1 << 1;
                        int k = n1 >> 1;
                        if (i < j1) {
                            double tmp_real1 = x[ii];
                            double tmp_imag1 = x[ii + 1];
                            x[ii] = x[jj];
                            x[ii + 1] = x[jj + 1];
                            x[jj] = tmp_real1;
                            x[jj + 1] = tmp_imag1;
                        }
                        while (k <= j1) {
                            j1 -= k;
                            k >>= 1;
                        }
                        j1 += k;
                    }
                    for (int bit = 0, dual = 1; bit < logn; bit++, dual *= 2) {
                        double w_real = 1.0;
                        double w_imag = 0.0;
                        double theta = 2.0 * +1 * Math.PI / (2.0 * (double) dual);
                        double s = Math.sin(theta);
                        double t = Math.sin(theta / 2.0);
                        double s2 = 2.0 * t * t;
                        /* a = 0 */
                        for (int b = 0; b < n2; b += 2 * dual) {
                            int i2 = 2 * b;
                            int j = 2 * (b + dual);
                            double wd_real = x[j];
                            double wd_imag = x[j + 1];
                            x[j] = x[i2] - wd_real;
                            x[j + 1] = x[i2 + 1] - wd_imag;
                            x[i2] += wd_real;
                            x[i2 + 1] += wd_imag;
                        }
                        /* a = 1 .. (dual-1) */
                        for (int a = 1; a < dual; a++) {
                            /* trignometric recurrence for w-> exp(i theta) w */
                            {
                                double tmp_real = w_real - s * w_imag - s2 * w_real;
                                double tmp_imag = w_imag + s * w_real - s2 * w_imag;
                                w_real = tmp_real;
                                w_imag = tmp_imag;
                            }
                            for (int b = 0; b < n2; b += 2 * dual) {
                                int i2 = 2 * (b + a);
                                int j = 2 * (b + a + dual);
                                double z1_real = x[j];
                                double z1_imag = x[j + 1];
                                double wd_real = w_real * z1_real - w_imag * z1_imag;
                                double wd_imag = w_real * z1_imag + w_imag * z1_real;
                                x[j] = x[i2] - wd_real;
                                x[j + 1] = x[i2 + 1] - wd_imag;
                                x[i2] += wd_real;
                                x[i2 + 1] += wd_imag;
                            }
                        }
                    }
                }
            }
            int nd1 = x.length;
            int n1 = nd1 / 2;
            double norm1 = 1 / ((double) n1);
            for (int i1 = 0; i1 < nd1; i1++)
                x[i1] *= norm1;
            double diff = 0.0;
            for (int i = 0; i < nd2; i++) {
                double d = x[i] - copy[i];
                diff += d * d;
            }
            double fftTest = Math.sqrt(diff / nd2);
            if (kernel.CURRENT_FFT_RESULT.equals("" + fftTest)) {
                if (id == 1) {
                    System.out.println(Constants.VERIFICATION_PASSED_MESSAGE);
                }
            } else {
                System.out.println(Constants.VERIFICATION_FAILED_MESSAGE
                        + "The result is " + "" + fftTest + "  instead of " + kernel.CURRENT_FFT_RESULT);
            }
            if (!(fftTest / FFT_size > EPS)) {
                x = null;
                if (Q.running) {
                    Q.total += (System.currentTimeMillis() * 0.001) - Q.last_time;
                    Q.last_time = (System.currentTimeMillis() * 0.001);
                }
                result = num_flops(FFT_size) * cycles / Q.total * 1.0e-6;
            }
            res = result;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
