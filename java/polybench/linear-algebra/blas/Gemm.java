import java.util.Arrays;

public class Gemm {
    public static final int NI = 200;
    public static final int NJ = 220;
    public static final int NK = 240;
    
    public static void initArray(int ni, int nj, int nk, double[] alpha, double[] beta, double[][] C, double[][] A, double[][] B) {
        alpha[0] = 1.5;
        beta[0] = 1.2;
        
        for (int i = 0; i < ni; i++) {
            for (int j = 0; j < nj; j++) {
                C[i][j] = ((i * j + 1) % ni) / (double) ni;
            }
        }
        
        for (int i = 0; i < ni; i++) {
            for (int j = 0; j < nk; j++) {
                A[i][j] = (i * (j + 1) % nk) / (double) nk;
            }
        }
        
        for (int i = 0; i < nk; i++) {
            for (int j = 0; j < nj; j++) {
                B[i][j] = (i * (j + 2) % nj) / (double) nj;
            }
        }
    }
    
    public static void printArray(int ni, int nj, double[][] C) {
        System.out.println("C:");
        for (int i = 0; i < ni; i++) {
            for (int j = 0; j < nj; j++) {
                if ((i * ni + j) % 20 == 0) System.out.println();
                System.out.printf("%.6f ", C[i][j]);
            }
        }
    }
    
    public static void kernelGemm(int ni, int nj, int nk, double alpha, double beta, double[][] C, double[][] A, double[][] B) {
        for (int i = 0; i < ni; i++) {
            for (int j = 0; j < nj; j++) {
                C[i][j] *= beta;
            }
            for (int k = 0; k < nk; k++) {
                for (int j = 0; j < nj; j++) {
                    C[i][j] += alpha * A[i][k] * B[k][j];
                }
            }
        }
    }
    
    public static void main(String[] args) {
        int ni = NI;
        int nj = NJ;
        int nk = NK;
        
        double[] alpha = new double[1];
        double[] beta = new double[1];
        double[][] C = new double[NI][NJ];
        double[][] A = new double[NI][NK];
        double[][] B = new double[NK][NJ];
        
        initArray(ni, nj, nk, alpha, beta, C, A, B);
        
        long startTime = System.currentTimeMillis();
        kernelGemm(ni, nj, nk, alpha[0], beta[0], C, A, B);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
        
        printArray(ni, nj, C);
    }
}
