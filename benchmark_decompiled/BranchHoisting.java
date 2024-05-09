public class BranchHoisting {
   public int original(int var1) {
      int var2 = 0;

      while(true) {
         int var3 = var2 + 1;
         if (500 <= var3) {
            var3 = var2 + var2;
            if (var1 == 0) {
               var1 = var3;
            } else {
               var1 = var3 + var2;
            }

            return var1;
         }

         var2 = var3;
      }
   }

   public int expected(int var1) {
      int var3;
      for(var3 = 0; var3 < 500; ++var3) {
      }

      int var2 = var3 + var3;
      if (var1 == 0) {
         var2 = var2;
      } else {
         var2 += var3;
      }

      return var2;
   }
}
