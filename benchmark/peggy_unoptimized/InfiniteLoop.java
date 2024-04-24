public class InfiniteLoop {
    public static void main(String[] args) {
        original();
    }

     public static int original() {
        int j = 0;
        for (int i = 5; i == 5; ) {
            j++;
        }
        j = 2;
        return j;
    }
}
