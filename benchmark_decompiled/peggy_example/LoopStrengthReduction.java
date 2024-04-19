public class LoopStrengthReduction {
   public static void original() {
      int var1 = 0;

      for(int var0 = 0; 300 > var1; var0 += 5) {
         ++var1;
         System.out.println(var0);
      }

   }

   public static void expected() {
      int var1 = 0;

      for(int var0 = 0; var1 < 300; var0 += 5) {
         System.out.println(var0);
         ++var1;
      }

   }
}
