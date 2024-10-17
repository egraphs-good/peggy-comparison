public class FunctionInlining {
    public int foo() {
        return 1;
    }

    public int original(int x) {
        return foo() + 1;
    }

    public int expected(int x) {
        return x + 2;
    }
}
