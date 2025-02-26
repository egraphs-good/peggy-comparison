public class Benchmark {

    public static int conditionalInvariantCodeMotion(int n, int m) {
        int k = 0;
        int k2 = 0;
        if (n < m) {
            k = n * m;
            k2 = k * 3;
        } else {
            k = n * m;
            k2 = k * 2;
        }
        return k2;

    }

    public static int conditionalSpliting(int n, int m, int k) {
        if (n == m || n == k) {
            return (n - m) + (n - k);
        } else {
            return k;
        }
    }

    public static int conditionalOrdering(int n, int m, int k) {
        if (n == m) {
            if (n == k) {
                return n + m + k;
            } else {
                return n * m + k;
            }
        } else {
            return n * m * k;
        }
    }

    public static int branchHoistingOriginal(int n) {
        int y = 0;
        int x = 0;
        while (y < 500) {
            y++;
            if (n == 0) {
                x = y * 2;
            } else {
                x = y * 3;
            }
        }
        return x;
    }

    public static int branchHoistingExpected(int n) {
        int y = 0;
        int x = 0;
        while (y < 500)
            y++;

        if (n == 0) {
            x = y * 2;
        } else {
            x = y * 3;
        }

        return x;
    }

    public static int conditionalConstantFoldingOriginal(int x) {
        if (x == 5) {
            return 4 * x;
        } else if (x == 4) {
            return 5 * x;
        } else {
            return 20;
        }
    }

    public static int conditionalConstantFoldingExpected() {
        return 20;
    }

    public static int constantFoldOriginal() {
        int j = 1 + 1;
        int k = j * 3;
        return k - 10;
    }

    public static int deadLoopDeletionOriginal() {
        int j = 3;
        for (int i = 0; i < 4; i++) {
            j++;
        }
        j = 2;
        return j;
    }

    public static int functionInliningFoo() {
        return 1;
    }

    public static int functionInliningOriginal(int x) {
        return functionInliningFoo() + 1;
    }

    public static int functionInliningExpected(int x) {
        return x + 2;
    }

    public static int ifTrueOriginal(int x) {
        if (true) {
            return x;
        } else {
            return x - 1;
        }
    }

    public static int ifTrueExpected(int x) {
        return x;
    }

    public static int infiniteEffectfulLoopOriginal() {
        int j = 0;
        for (int i = 5; i == 5;) {
            System.out.println(j);
        }
        j = 2;
        return j;
    }

    public static int infiniteLoopOriginal() {
        int j = 0;
        for (int i = 5; i == 5;) {
            j++;
        }
        return j;
    }

    // Modified from the paper example (see loop strength reduction)
    public static int loopBasedCodeMotionOriginal() {
        int x = 0;
        while (x < 3) {
            x += 1;
        }
        return x * 50;
    }

    public static int loopBasedCodeMotionExpected() {
        int x = 0;
        while (x < 150) {
            x += 50;
        }
        return x;
    }

    public static void loopInvariantCodeMotionOriginal(int n, int m) {
        for (int i = 0; i < 20; i++) {
            int j = n * 20;
            if (j < m) {
                j++;
            }
            System.out.println(i * j);
        }
    }

    public static void loopInvariantCodeMotionExpected(int n, int m) {
        int j = n * 20;
        if (j < m) {
            j++;
        }
        for (int i = 0; i < 20; i++) {
            System.out.println(i * j);
        }
    }

    public static int loopPeelingOriginal(int n) {
        int x = 0;
        int i = 0;
        while (i < n) {
            x += 5;
            i++;
        }
        return x;
    }

    public static int loopPeelingExpected(int n) {
        int x = 0;
        if (0 >= n) {
            x = 0;
        } else {
            x = 5;
            int i = 1;
            while (i < n) {
                x += 5;
                i++;
            }
        }
        return x;
    }

    public static void loopStrengthReductionOriginal() {
        int i = 0;
        int d = 0;
        while (d < 300) {
            System.out.println(i * 5);
            i = i + 1;
            d++;
        }
    }

    public static void loopStrengthReductionExpected() {
        int i = 0;
        int d = 0;
        while (d < 300) {
            System.out.println(i);
            i = i + 5;
            d++;
        }
    }

    // Modified from the paper example, as multiplying by 5 makes
    // the optimizer just turn it into five additions.
    public static void loopStrengthReductionModifiedOriginal() {
        int i = 0;
        int d = 0;
        while (d < 300) {
            System.out.println(i * 50);
            i = i + 1;
            if (d == 150) {
                i = i + 3;
            }
            d++;
        }
    }

    public static void loopStrengthReductionModifiedExpected() {
        int i = 0;
        int d = 0;
        while (d < 300) {
            System.out.println(i);
            i = i + 50;
            if (d == 150) {
                i = i + 150;
            }
            d++;
        }
    }

    public static int loopUnrollOriginal(int n) {
        int i = 0;
        while (i < 1) {
            i++;
        }
        return i;
    }

    public static int loopUnrollExpected(int n) {
        return 1;
    }

    public static int simpleLoopUnswitchOriginal(int n) {
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

    // The expected code for simple loop unswitch causes encoding
    // of the output CFG to never terminate, so it is in Failing.java.
}
