import java.util.Arrays;

public class ThreeMM {
    public static final int NI = 180;
    public static final int NJ = 190;
    public static final int NK = 200;
    public static final int NL = 210;
    public static final int NM = 220;
    
    public static void initArray(int ni, int nj, int nk, int nl, int nm,
                                  double[][] A, double[][] B, double[][] C, double[][] D) {
        for (int i = 0; i < ni; i++) {
            for (int j = 0; j < nk; j++) {
                A[i][j] = ((i * j + 1) % ni) / (5.0 * ni);
            }
        }
        for (int i = 0; i < nk; i++) {
            for (int j = 0; j < nj; j++) {
                B[i][j] = ((i * (j + 1) + 2) % nj) / (5.0 * nj);
            }
        }
        for (int i = 0; i < nj; i++) {
            for (int j = 0; j < nm; j++) {
                C[i][j] = (i * (j + 3) % nl) / (5.0 * nl);
            }
        }
        for (int i = 0; i < nm; i++) {
            for (int j = 0; j < nl; j++) {
                D[i][j] = ((i * (j + 2) + 2) % nk) / (5.0 * nk);
            }
        }
    }
    
    public static void printArray(int ni, int nl, double[][] G) {
        System.out.println("G:");
        for (int i = 0; i < ni; i++) {
            for (int j = 0; j < nl; j++) {
                if ((i * ni + j) % 20 == 0) System.out.println();
                System.out.printf("%.6f ", G[i][j]);
            }
        }
    }
    
    public static void kernelThreeMM(int ni, int nj, int nk, int nl, int nm,
                                      double[][] E, double[][] A, double[][] B,
                                      double[][] F, double[][] C, double[][] D,
                                      double[][] G) {
        for (int i = 0; i < ni; i++) {
            for (int j = 0; j < nj; j++) {
                E[i][j] = 0.0;
                for (int k = 0; k < nk; k++) {
                    E[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        
        for (int i = 0; i < nj; i++) {
            for (int j = 0; j < nl; j++) {
                F[i][j] = 0.0;
                for (int k = 0; k < nm; k++) {
                    F[i][j] += C[i][k] * D[k][j];
                }
            }
        }
        
        for (int i = 0; i < ni; i++) {
            for (int j = 0; j < nl; j++) {
                G[i][j] = 0.0;
                for (int k = 0; k < nj; k++) {
                    G[i][j] += E[i][k] * F[k][j];
                }
            }
        }
    }
    
    public static void main(String[] args) {
        int ni = NI, nj = NJ, nk = NK, nl = NL, nm = NM;
        
        double[][] A = new double[NI][NK];
        double[][] B = new double[NK][NJ];
        double[][] C = new double[NJ][NM];
        double[][] D = new double[NM][NL];
        double[][] E = new double[NI][NJ];
        double[][] F = new double[NJ][NL];
        double[][] G = new double[NI][NL];
        
        initArray(ni, nj, nk, nl, nm, A, B, C, D);
        
        long startTime = System.currentTimeMillis();
        kernelThreeMM(ni, nj, nk, nl, nm, E, A, B, F, C, D, G);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
        
        printArray(ni, nl, G);
    }
}
