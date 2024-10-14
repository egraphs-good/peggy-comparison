public class FunctionInlining {
    public int foo(int x) {
        return x + 1;
    }

    public int original(int x) {
        return foo(x) + 1;
    }

    public int expected(int x) {
        return x + 2;
    }
}
