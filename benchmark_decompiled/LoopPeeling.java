class LoopPeeling {
   public int original(int var1) {
      int var3 = 0;

      int var2;
      for(var2 = 0; var3 < var1; var2 += 5) {
         ++var3;
      }

      return var2;
   }

   public int expected(int var1) {
      if (var1 <= 0) {
         var1 = 0;
      } else {
         int var2 = 1;

         int var3;
         for(var3 = 5; var2 < var1; var3 += 5) {
            ++var2;
         }

         var1 = var3;
      }

      return var1;
   }
}
