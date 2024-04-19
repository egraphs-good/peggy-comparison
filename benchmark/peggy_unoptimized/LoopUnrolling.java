public class LoopUnrolling {
    public static void main(String[] args) {
        // If you unroll this loop once, you will realize it does not need to exist
        int i = 0;
        while (i < 1) {
            i++;
        }
        System.out.println(i);
    }
}