public class MultiVariableLoop {
    public int original(int n) {
        for (int i = 0, j = 4; i < 10 & j < 2; i++, j++) {
            System.out.println(i);
            System.out.println(j);
            System.out.println(i + j);
            n += 2;
        }
        return n;
    }
}
