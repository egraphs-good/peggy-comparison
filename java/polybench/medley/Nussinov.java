import java.util.Arrays;

public class Nussinov {
    public static final int N = 500;
    
    public static void initArray(int n, byte[] seq, int[][] table) {
        for (int i = 0; i < n; i++) {
            seq[i] = (byte) ((i + 1) % 4);
        }
        
        for (int i = 0; i < n; i++) {
            Arrays.fill(table[i], 0);
        }
    }
    
    public static void printArray(int n, int[][] table) {
        System.out.println("table:");
        int t = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                if (t % 20 == 0) System.out.println();
                System.out.printf("%.2f ", (double) table[i][j]);
                t++;
            }
        }
    }
    
    public static int match(byte b1, byte b2) {
        return (b1 + b2) == 3 ? 1 : 0;
    }
    
    public static int maxScore(int s1, int s2) {
        return Math.max(s1, s2);
    }
    
    public static void kernelNussinov(int n, byte[] seq, int[][] table) {
        for (int i = n - 1; i >= 0; i--) {
            for (int j = i + 1; j < n; j++) {
                if (j - 1 >= 0) {
                    table[i][j] = maxScore(table[i][j], table[i][j - 1]);
                }
                if (i + 1 < n) {
                    table[i][j] = maxScore(table[i][j], table[i + 1][j]);
                }
                if (j - 1 >= 0 && i + 1 < n) {
                    if (i < j - 1) {
                        table[i][j] = maxScore(table[i][j], table[i + 1][j - 1] + match(seq[i], seq[j]));
                    } else {
                        table[i][j] = maxScore(table[i][j], table[i + 1][j - 1]);
                    }
                }
                for (int k = i + 1; k < j; k++) {
                    table[i][j] = maxScore(table[i][j], table[i][k] + table[k + 1][j]);
                }
            }
        }
    }
    
    public static void main(String[] args) {
        int n = N;
        
        byte[] seq = new byte[N];
        int[][] table = new int[N][N];
        
        initArray(n, seq, table);
        
        long startTime = System.currentTimeMillis();
        kernelNussinov(n, seq, table);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
        
        printArray(n, table);
    }
}