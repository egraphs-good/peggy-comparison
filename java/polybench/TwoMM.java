import java.util.Arrays;

public class TwoMM {
    private static final int NI = 180;
    private static final int NJ = 190;
    private static final int NK = 210;
    private static final int NL = 220;
    
    private static void initArray(int ni, int nj, int nk, int nl, double[] alpha, double[] beta, double[][] A, double[][] B, double[][] C, double[][] D) {
        alpha[0] = 1.5;
        beta[0] = 1.2;
        
        for (int i = 0; i < ni; i++) {
            for (int j = 0; j < nk; j++) {
                A[i][j] = ((i * j + 1) % ni) / (double) ni;
            }
        }
        
        for (int i = 0; i < nk; i++) {
            for (int j = 0; j < nj; j++) {
                B[i][j] = (i * (j + 1) % nj) / (double) nj;
            }
        }
        
        for (int i = 0; i < nj; i++) {
            for (int j = 0; j < nl; j++) {
                C[i][j] = ((i * (j + 3) + 1) % nl) / (double) nl;
            }
        }
        
        for (int i = 0; i < ni; i++) {
            for (int j = 0; j < nl; j++) {
                D[i][j] = (i * (j + 2) % nk) / (double) nk;
            }
        }
    }
    
    private static void printArray(int ni, int nl, double[][] D) {
        System.out.println("D:");
        for (int i = 0; i < ni; i++) {
            for (int j = 0; j < nl; j++) {
                if ((i * ni + j) % 20 == 0) System.out.println();
                System.out.printf("%.6f ", D[i][j]);
            }
        }
    }
    
    private static void kernelTwoMM(int ni, int nj, int nk, int nl, double alpha, double beta, double[][] tmp, double[][] A, double[][] B, double[][] C, double[][] D) {
        for (int i = 0; i < ni; i++) {
            for (int j = 0; j < nj; j++) {
                tmp[i][j] = 0.0;
                for (int k = 0; k < nk; ++k) {
                    tmp[i][j] += alpha * A[i][k] * B[k][j];
                }
            }
        }
        
        for (int i = 0; i < ni; i++) {
            for (int j = 0; j < nl; j++) {
                D[i][j] *= beta;
                for (int k = 0; k < nj; ++k) {
                    D[i][j] += tmp[i][k] * C[k][j];
                }
            }
        }
    }
    
    public static void main(String[] args) {
        int ni = NI;
        int nj = NJ;
        int nk = NK;
        int nl = NL;
        
        double[] alpha = new double[1];
        double[] beta = new double[1];
        double[][] A = new double[NI][NK];
        double[][] B = new double[NK][NJ];
        double[][] C = new double[NJ][NL];
        double[][] D = new double[NI][NL];
        double[][] tmp = new double[NI][NJ];
        
        initArray(ni, nj, nk, nl, alpha, beta, A, B, C, D);
        
        long startTime = System.currentTimeMillis();
        kernelTwoMM(ni, nj, nk, nl, alpha[0], beta[0], tmp, A, B, C, D);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
        
        printArray(ni, nl, D);
    }
}
