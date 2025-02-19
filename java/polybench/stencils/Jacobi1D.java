import java.util.Arrays;

public class Jacobi1D {
    public static final int N = 400;
    public static final int TSTEPS = 100;
    
    public static void initArray(int n, double[] A, double[] B) {
        for (int i = 0; i < n; i++) {
            A[i] = ((double) i + 2) / n;
            B[i] = ((double) i + 3) / n;
        }
    }
    
    public static void printArray(int n, double[] A) {
        System.out.printf("A:");
        for (int i = 0; i < n; i++) {
            
            if (i % 20 == 0) System.out.println();
            System.out.printf("%.2f ", A[i]);
        }
    }
    
    public static void kernelJacobi1D(int tsteps, int n, double[] A, double[] B) {
        for (int t = 0; t < tsteps; t++) {
            for (int i = 1; i < n - 1; i++) {
                B[i] = 0.33333 * (A[i - 1] + A[i] + A[i + 1]);
            }
            for (int i = 1; i < n - 1; i++) {
                A[i] = 0.33333 * (B[i - 1] + B[i] + B[i + 1]);
            }
        }
    }
    
    public static void main(String[] args) {
        int n = N;
        int tsteps = TSTEPS;
        
        double[] A = new double[N];
        double[] B = new double[N];
        
        initArray(n, A, B);
        
        long startTime = System.currentTimeMillis();
        kernelJacobi1D(tsteps, n, A, B);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
        
        printArray(n, A);
    }
}
