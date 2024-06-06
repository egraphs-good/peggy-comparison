public class ConditionalConstantFolding {
    public int original(int x) {
        if (x == 5) {
            return 4 * x;
        } else if (x == 4) {
            return 5 * x;
        } else {
            return 20;
        }
    }

    public int expected() {
        return 20;
    }
}