public class LoopBasedCodeMotion {
    public int original() {
        int x = 0;
        while (x < 3) {
            x += 1;
        }
        return x * 5;
    }

    public int expected() {
        int x = 0;
        while (x < 3) {
            x += 5;
        }
        return x;
    }
}