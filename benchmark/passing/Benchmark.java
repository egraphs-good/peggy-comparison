public class Benchmark {

    public int branchHoistingOriginal(int n) {
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

    public int branchHoistingExpected(int n) {
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

    public int conditionalConstantFoldingOriginal(int x) {
        if (x == 5) {
            return 4 * x;
        } else if (x == 4) {
            return 5 * x;
        } else {
            return 20;
        }
    }

    public int conditionalConstantFoldingExpected() {
        return 20;
    }

    public int constantFoldOriginal() {
        int j = 1 + 1;
        int k = j * 3;
        return k - 10;
    }

    public int deadLoopDeletionOriginal() {
        int j = 3;
        for (int i = 0; i < 4; i++) {
            j++;
        }
        j = 2;
        return j;
    }

    public int functionInliningFoo() {
        return 1;
    }

    public int functionInliningOriginal(int x) {
        return functionInliningFoo() + 1;
    }

    public int functionInliningExpected(int x) {
        return x + 2;
    }

    public int ifTrueOriginal(int x) {
        if (true) {
            return x;
        } else {
            return x - 1;
        }
    }

    public int ifTrueExpected(int x) {
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

    public int loopBasedCodeMotionOriginal() {
        int x = 0;
        while (x < 3) {
            x += 1;
        }
        return x * 5;
    }

    public int loopBasedCodeMotionExpected() {
        int x = 0;
        while (x < 15) {
            x += 5;
        }
        return x;
    }

    public void loopInvariantCodeMotionOriginal(int n, int m) {
        for (int i = 0; i < 20; i++) {
            int j = n * 20;
            if (j < m) {
                j++;
            }
            System.out.println(i * j);
        }
    }

    public void loopInvariantCodeMotionExpected(int n, int m) {
        int j = n * 20;
        if (j < m) {
            j++;
        }
        for (int i = 0; i < 20; i++) {
            System.out.println(i * j);
        }
    }

    public int loopPeelingOriginal(int n) {
        int x = 0;
        int i = 0;
        while (i < n) {
            x += 5;
            i++;
        }
        return x;
    }

    public int loopPeelingExpected(int n) {
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

    public static void loopStrengthReductionModifiedOriginal() {
        int i = 0;
        int d = 0;
        while (d < 300) {
            System.out.println(i * 5);
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
            i = i + 5;
            if (d == 150) {
                i = i + 15;
            }
            d++;
        }
    }

    public int loopUnrollOriginal(int n) {
        int i = 0;
        while (i < 1) {
            i++;
        }
        return i;
    }

    public int loopUnrollExpected(int n) {
        return 1;
    }

    public int multiVariableLoopOriginal(int n) {
        for (int i = 0, j = 4; i < 10 & j < 2; i++, j++) {
            System.out.println(i);
            System.out.println(j);
            System.out.println(i + j);
            n += 2;
        }
        return n;
    }

    public int separateLoopsInterrelated(int n) {
        int accum = 0;
        for (int i = 0; i < n; i++) {
            accum += i;
        }

        for (int j = 0; j < n + 1; j++) {
            accum += j;
        }

        return accum;
    }

    public void separateLoopsUnrelated(int n) {
        int accumi = 0;
        for (int i = 0; i < n; i++) {
            accumi += i;
        }
        System.out.println(accumi);

        int accumj = 0;
        for (int j = 0; j < n; j++) {
            accumj += j;
        }
        System.out.println(accumj);
    }

    public int simpleLoopUnswitchOriginal(int n) {
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

    // This causes the encoding of output CFG to never terminate
    // public int simpleLoopUnswitchExpected(int n) {
    // int j = 0;
    // if (n < 0) {
    // for (int i = 0; i < n; i++) {
    // System.out.println(i);
    // j = 2;
    // j++;
    // }
    // } else {
    // for (int i = 0; i < n; i++) {
    // System.out.println(i);
    // j++;
    // }
    // }
    // return j;
    // }

}
