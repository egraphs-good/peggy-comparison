public class ConstantFold {
    public int original() {
        int j = 1 + 1;
        int k = j * 3;
        return k - 10;
    }
}