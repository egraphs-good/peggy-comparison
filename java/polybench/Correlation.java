import java.util.Arrays;

public class Correlation {
    private static final int N = 260;
    private static final int M = 240;
    
    private static void initArray(int m, int n, double[] float_n, double[][] data) {
        float_n[0] = (double) N;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                data[i][j] = (double) (i * j) / M + i;
            }
        }
    }
    
    private static void printArray(int m, double[][] corr) {
        System.out.println("corr:");
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                if ((i * m + j) % 20 == 0) System.out.println();
                System.out.printf("%.2f ", corr[i][j]);
            }
        }
    }
    
    private static void kernelCorrelation(int m, int n, double float_n, double[][] data, double[][] corr, double[] mean, double[] stddev) {
        double eps = 0.1;
        
        for (int j = 0; j < m; j++) {
            mean[j] = 0.0;
            for (int i = 0; i < n; i++) {
                mean[j] += data[i][j];
            }
            mean[j] /= float_n;
        }
        
        for (int j = 0; j < m; j++) {
            stddev[j] = 0.0;
            for (int i = 0; i < n; i++) {
                stddev[j] += (data[i][j] - mean[j]) * (data[i][j] - mean[j]);
            }
            stddev[j] /= float_n;
            stddev[j] = Math.sqrt(stddev[j]);
            stddev[j] = stddev[j] <= eps ? 1.0 : stddev[j];
        }
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                data[i][j] -= mean[j];
                data[i][j] /= Math.sqrt(float_n) * stddev[j];
            }
        }
        
        for (int i = 0; i < m - 1; i++) {
            corr[i][i] = 1.0;
            for (int j = i + 1; j < m; j++) {
                corr[i][j] = 0.0;
                for (int k = 0; k < n; k++) {
                    corr[i][j] += data[k][i] * data[k][j];
                }
                corr[j][i] = corr[i][j];
            }
        }
        corr[m - 1][m - 1] = 1.0;
    }
    
    public static void main(String[] args) {
        int n = N;
        int m = M;
        
        double[] float_n = new double[1];
        double[][] data = new double[N][M];
        double[][] corr = new double[M][M];
        double[] mean = new double[M];
        double[] stddev = new double[M];
        
        initArray(m, n, float_n, data);
        
        long startTime = System.currentTimeMillis();
        kernelCorrelation(m, n, float_n[0], data, corr, mean, stddev);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
        
        printArray(m, corr);
    }
}
