/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */

public class Constants {
    public static final double RESOLUTION_DEFAULT = 2.0; /* secs */
    public static final int RANDOM_SEED = 101010;
    public static final int FFT_SIZE = 1024;
    public static final int SOR_SIZE = 100;
    public static final int SPARSE_SIZE_M = 1000;
    public static final int SPARSE_SIZE_nz = 5000;
    public static final int LU_SIZE = 100;
    public static final String SMALL_DATASET_NAME = "SMALL";
    public static final String DEFAULT_DATASET_NAME = "DEFAULT";
    public static final String LARGE_DATASET_NAME = "LARGE";
    static final int SMALL_FFT_SIZE = 65536;
    static final int SMALL_SOR_SIZE = 250;
    static final int SMALL_SPARSE_SIZE_M = 25000;
    static final int SMALL_SPARSE_SIZE_nz = 62500;
    static final int SMALL_LU_SIZE = 250;
    static final int DEFAULT_FFT_SIZE = 1048576;
    static final int DEFAULT_SOR_SIZE = 1000;
    static final int DEFAULT_SPARSE_SIZE_M = 100000;
    static final int DEFAULT_SPARSE_SIZE_nz = 1000000;
    static final int DEFAULT_LU_SIZE = 1000;
    static final int LARGE_FFT_SIZE = 4194304;
    static final int LARGE_SOR_SIZE = 2048;
    static final int LARGE_SPARSE_SIZE_M = 200000;
    static final int LARGE_SPARSE_SIZE_nz = 4000000;
    static final int LARGE_LU_SIZE = 2048;
    public static final int TINY_FFT_SIZE = 16;
    public static final int TINY_SOR_SIZE = 10;
    public static final int TINY_SPARSE_SIZE_M = 10;
    public static final int TINY_SPARSE_SIZE_N = 10;
    public static final int TINY_SPARSE_SIZE_nz = 50;
    public static final int TINY_LU_SIZE = 10;
    static final String SMALL_FFT_RESULT = "1.9479755659589253E-15";
    static final String SMALL_LU_RESULT = "1.3607136251092555E-11";
    static final String SMALL_SOR_RESULT = "125.97382183302308";
    static final String SMALL_SPARSE_RESULT = "10498.968164601722";
    static final String DEFAULT_FFT_RESULT = "7.68822223098636E-15";
    static final String DEFAULT_LU_RESULT = "6.859625172571382E-11";
    static final String DEFAULT_SOR_RESULT = "519.2137313757584";
    static final String DEFAULT_SPARSE_RESULT = "242020.31251847022";
    static final String LARGE_FFT_RESULT = "1.5820104483934885E-14";
    static final String LARGE_LU_RESULT = "3.3668594294922888E-9";
    static final String LARGE_SOR_RESULT = "1009.9963165360384";
    static final String LARGE_SPARSE_RESULT = "985196.4951987901";
    static final String VERIFICATION_PASSED_MESSAGE = "The result is correct.";
    static final String VERIFICATION_FAILED_MESSAGE = "The result is wrong. ";
}
