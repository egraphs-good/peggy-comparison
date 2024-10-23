public class InlinedThreeMM {
    public static void main(String[] args) {
        // Constants
        final int NI = 180;
        final int NJ = 190;
        final int NK = 200;
        final int NL = 210;
        final int NM = 220;

        // Initialize arrays
        float[][] A = new float[NI][NK];
        float[][] B = new float[NK][NJ];
        float[][] C = new float[NJ][NM];
        float[][] D = new float[NM][NL];
        for (int i = 0; i < NI; i++) {
            for (int j = 0; j < NK; j++) {
                A[i][j] = ((float) i * j + 1) % 5 / 5.0f;
            }
        }

        for (int i = 0; i < NK; i++) {
            for (int j = 0; j < NJ; j++) {
                B[i][j] = ((float) i * (j + 1) + 2) % 5 / 5.0f;
            }
        }

        for (int i = 0; i < NJ; i++) {
            for (int j = 0; j < NM; j++) {
                C[i][j] = ((float) i * (j + 3)) % 5 / 5.0f;
            }
        }

        for (int i = 0; i < NM; i++) {
            for (int j = 0; j < NL; j++) {
                D[i][j] = ((float) i * (j + 2) + 2) % 5 / 5.0f;
            }
        }

        for (int i = 0; i < NI; i++) {
            for (int j = 0; j < NK; j++) {
                System.out.print(A[i][j] + " ");
            }
            System.out.println();
        }
        for (int i = 0; i < NK; i++) {
            for (int j = 0; j < NJ; j++) {
                System.out.print(B[i][j] + " ");
            }
            System.out.println();
        }
        for (int i = 0; i < NJ; i++) {
            for (int j = 0; j < NM; j++) {
                System.out.print(C[i][j] + " ");
            }
            System.out.println();
        }

        for (int i = 0; i < NM; i++) {
            for (int j = 0; j < NL; j++) {
                System.out.print(D[i][j] + " ");
            }
            System.out.println();
        }

        // Main computation
        // Compute E := A * B
        float[][] E = new float[NI][NJ];
        for (int i = 0; i < NI; i++) {
            for (int j = 0; j < NJ; j++) {
                E[i][j] = 0;
                for (int k = 0; k < NK; k++) {
                    E[i][j] += A[i][k] * B[k][j];
                }
            }
        }

        // Compute F := C * D
        float[][] F = new float[NJ][NL];
        for (int i = 0; i < NJ; i++) {
            for (int j = 0; j < NL; j++) {
                F[i][j] = 0;
                for (int k = 0; k < NM; k++) {
                    F[i][j] += C[i][k] * D[k][j];
                }
            }
        }

        // Compute G := E * F
        float[][] G = new float[NI][NL];
        for (int i = 0; i < NI; i++) {
            for (int j = 0; j < NL; j++) {
                G[i][j] = 0;
                for (int k = 0; k < NJ; k++) {
                    G[i][j] += E[i][k] * F[k][j];
                }
            }
        }

        for (int i = 0; i < NI; i++) {
            for (int j = 0; j < NL; j++) {
                System.out.print(G[i][j] + " ");
            }
            System.out.println();
        }

    }

}
