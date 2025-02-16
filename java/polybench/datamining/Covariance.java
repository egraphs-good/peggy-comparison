import java.util.Arrays;

public class Covariance {
    public static final int N = 100;
    public static final int M = 80;
    
    public static void initArray(int m, int n, double[] float_n, double[][] data) {
        float_n[0] = (double) n;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                data[i][j] = ((double) i * j) / M;
            }
        }
    }
    
    public static void printArray(int m, double[][] cov) {
        System.out.println("cov:");
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                if ((i * m + j) % 20 == 0) System.out.println();
                System.out.printf("%.5f ", cov[i][j]);
            }
        }
    }
    
    public static void kernelCovariance(int m, int n, double float_n, double[][] data, double[][] cov, double[] mean) {
        for (int j = 0; j < m; j++) {
            mean[j] = 0.0;
            for (int i = 0; i < n; i++) {
                mean[j] += data[i][j];
            }
            mean[j] /= float_n;
        }
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                data[i][j] -= mean[j];
            }
        }
        
        for (int i = 0; i < m; i++) {
            for (int j = i; j < m; j++) {
                cov[i][j] = 0.0;
                for (int k = 0; k < n; k++) {
                    cov[i][j] += data[k][i] * data[k][j];
                }
                cov[i][j] /= (float_n - 1.0);
                cov[j][i] = cov[i][j];
            }
        }
    }
    
    public static void main(String[] args) {
        int n = N;
        int m = M;
        
        double[] float_n = new double[1];
        double[][] data = new double[N][M];
        double[][] cov = new double[M][M];
        double[] mean = new double[M];
        
        initArray(m, n, float_n, data);
        
        long startTime = System.currentTimeMillis();
        kernelCovariance(m, n, float_n[0], data, cov, mean);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
        
        printArray(m, cov);
    }
}
