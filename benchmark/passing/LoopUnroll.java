public class LoopUnroll {
    public int original(int n) {
        int i = 0;
        while (i < 1) {
            i++;
        }
        return i;
    }

    public int expected(int n) {
        return 1;
    }
}
