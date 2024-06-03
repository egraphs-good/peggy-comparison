public class InfiniteEffectfulLoop {
    public static void main(String[] args) {
        original();
    }

     public static int original() {
        int j = 0;
        for (int i = 5; i == 5; ) {
            System.out.println(j);
        }
        j = 2;
        return j;
    }
}

