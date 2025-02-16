import java.io.PrintStream;

public class Adi {
    static final int N = 200;  // Define N as needed
    static final int TSTEPS = 100;  // Define TSTEPS as needed

    static void initArray(int n, double[][] u) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                u[i][j] = (double) (i + n - j) / n;
            }
        }
    }

    static void printArray(int n, double[][] u, PrintStream out) {
        out.println("==BEGIN DUMP_ARRAYS==");
        out.printf("begin dump: u");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if ((i * n + j) % 20 == 0) out.println();
                out.printf("%.3f ", u[i][j]); // Adjust format as needed
            }
        }
        out.println();
        out.println("end   dump: u");
        out.println("==END   DUMP_ARRAYS==");
    }

    static void kernelADI(int tsteps, int n, double[][] u, double[][] v, double[][] p, double[][] q) {
        int t, i, j;
        double DX = 1.0 / n;
        double DY = 1.0 / n;
        double DT = 1.0 / tsteps;
        double B1 = 2.0;
        double B2 = 1.0;
        double mul1 = B1 * DT / (DX * DX);
        double mul2 = B2 * DT / (DY * DY);

        double a = -mul1 / 2.0;
        double b = 1.0 + mul1;
        double c = a;
        double d = -mul2 / 2.0;
        double e = 1.0 + mul2;
        double f = d;

        for (t = 1; t <= tsteps; t++) {
            // Column Sweep
            for (i = 1; i < n - 1; i++) {
                v[0][i] = 1.0;
                p[i][0] = 0.0;
                q[i][0] = v[0][i];

                for (j = 1; j < n - 1; j++) {
                    p[i][j] = -c / (a * p[i][j - 1] + b);
                    q[i][j] = (-d * u[j][i - 1] + (1.0 + 2.0 * d) * u[j][i] - f * u[j][i + 1] - a * q[i][j - 1])
                            / (a * p[i][j - 1] + b);
                }

                v[n - 1][i] = 1.0;
                for (j = n - 2; j >= 1; j--) {
                    v[j][i] = p[i][j] * v[j + 1][i] + q[i][j];
                }
            }

            // Row Sweep
            for (i = 1; i < n - 1; i++) {
                u[i][0] = 1.0;
                p[i][0] = 0.0;
                q[i][0] = u[i][0];

                for (j = 1; j < n - 1; j++) {
                    p[i][j] = -f / (d * p[i][j - 1] + e);
                    q[i][j] = (-a * v[i - 1][j] + (1.0 + 2.0 * a) * v[i][j] - c * v[i + 1][j] - d * q[i][j - 1])
                            / (d * p[i][j - 1] + e);
                }

                u[i][n - 1] = 1.0;
                for (j = n - 2; j >= 1; j--) {
                    u[i][j] = p[i][j] * u[i][j + 1] + q[i][j];
                }
            }
        }
    }

    public static void main(String[] args) {
        int n = N;
        int tsteps = TSTEPS;

        double[][] u = new double[n][n];
        double[][] v = new double[n][n];
        double[][] p = new double[n][n];
        double[][] q = new double[n][n];

        // Initialize arrays
        initArray(n, u);

        // Run kernel
        kernelADI(tsteps, n, u, v, p, q);

        // Print results
        printArray(n, u, System.out);
    }
}
