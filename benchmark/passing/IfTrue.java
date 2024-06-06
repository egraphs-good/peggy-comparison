public class IfTrue {
    public int original(int x) {
        if (true) {
            return x;
        } else {
            return x - 1;
        }
    }

    public int expected(int x) {
        return x;
    }
}