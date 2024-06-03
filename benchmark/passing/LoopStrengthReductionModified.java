public class LoopStrengthReductionModified {
    public static void original() {
        int i = 0;
        int d = 0;
        while (d < 300) {
            System.out.println(i * 500);
            i = i + 1;
            if (d == 150) {
                i = i + 3;
            }
            d++;
        }
    }
    public static void expected() {
        int i = 0;
        int d = 0;
        while (d < 300) {
            System.out.println(i);
            i = i + 5;
            if (d % 2 == 0) {
                i = i + 15;
            }
            d++;
        }    
    }
}
