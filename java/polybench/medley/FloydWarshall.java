import java.util.Arrays;

public class FloydWarshall {
    public static final int N = 500;
    
    public static void initArray(int n, int[][] path) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                path[i][j] = (i * j) % 7 + 1;
                if ((i + j) % 13 == 0 || (i + j) % 7 == 0 || (i + j) % 11 == 0) {
                    path[i][j] = 999;
                }
            }
        }
    }
    
    public static void printArray(int n, int[][] path) {
        System.out.println("path:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if ((i * n + j) % 20 == 0) System.out.println();
                System.out.printf("%.2f ", (double)path[i][j]);
            }
        }
    }
    
    public static void kernelFloydWarshall(int n, int[][] path) {
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    path[i][j] = Math.min(path[i][j], path[i][k] + path[k][j]);
                }
            }
        }
    }
    
    public static void main(String[] args) {
        int n = N;
        
        int[][] path = new int[N][N];
        
        initArray(n, path);
        
        long startTime = System.currentTimeMillis();
        kernelFloydWarshall(n, path);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
        
        printArray(n, path);
    }
}
