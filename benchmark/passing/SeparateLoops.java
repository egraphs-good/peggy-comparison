public class SeparateLoops {
    public int interrelated(int n) {
        int accum = 0;
        for (int i = 0; i < n; i++) {
            accum += i;
        }

        for (int j = 0; j < n + 1; j++) {
            accum += j;
        }

        return accum;
    }

    public void unrelated(int n) {
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
}