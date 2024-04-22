public class SimpleLoopUnswitch {
    public int original(int n) {
        int j = 0;
        for (int i = 0; i < n; i++) {
            System.out.println(i);
            if (n < 0) {
                j = 2;
            }
            j++;
        }
        return j;
    }

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
