public class UnswitchedLoop {
    // This causes the encoding of output CFG to never terminate
    public int expected(int n) {
        int j = 0;
        if (n < 0) {
            for (int i = 0; i < n; i++) {
                System.out.println(i);
                j = 2;
                j++;
            }
        } else {
            for (int i = 0; i < n; i++) {
                System.out.println(i);
                j++;
            }
        }
        return j;
    }
}
