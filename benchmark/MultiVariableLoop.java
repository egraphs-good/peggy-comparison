public class MultiVariableLoop {
    public static void main(String[] args) {
        for (int i = 0, j = 4; i < 10 & j < 2; i++, j++) {
            System.out.println(i);
            System.out.println(j);
            System.out.println(i + j);
        }
    }
}
