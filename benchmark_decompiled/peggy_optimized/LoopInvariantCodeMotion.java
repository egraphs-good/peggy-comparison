public class LoopInvariantCodeMotion {
   public void original(int var1, int var2) {
      var1 *= 20;
      if (var2 > var1) {
         var2 = 1 + var1;
      } else {
         var2 = var1;
      }

      var1 = 0;
      int var3 = 0;

      while(var3 < 20) {
         int var10001 = var1;
         var1 += var2;
         ++var3;
         System.out.println(var10001);
      }

   }

   public void expected(int var1, int var2) {
      var1 *= 20;
      if (var2 > var1) {
         var2 = 1 + var1;
      } else {
         var2 = var1;
      }

      int var3 = 0;

      for(var1 = 0; var3 < 20; var1 += var2) {
         ++var3;
         System.out.println(var1);
      }

   }
}
