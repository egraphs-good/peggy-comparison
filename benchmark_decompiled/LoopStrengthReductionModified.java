public class LoopStrengthReductionModified {
   public static void original() {
      int var2 = 0;

      for(int var1 = 0; var1 < 300; ++var1) {
         int var0 = var2 + 1;
         if (var1 == 150) {
            var0 += 3;
         } else {
            var0 = var0;
         }

         int var10001 = var2;
         var2 = var0;
         System.out.println(var10001 * 5);
      }

   }

   public static void expected() {
      int var2 = 0;
      int var0 = 0;

      while(var2 < 300) {
         int var1 = var0 + 5;
         if (var2 % 2 == 0) {
            var1 += 15;
         } else {
            var1 = var1;
         }

         int var10001 = var0;
         ++var2;
         var0 = var1;
         System.out.println(var10001);
      }

   }
}
