public class SeparateLoops {
   public int interrelated(int var1) {
      int var2 = 0;

      int var3;
      for(var3 = 0; var1 > var2; var3 += var2++) {
      }

      var3 = var3;
      var2 = 0;
      ++var1;

      while(var2 < var1) {
         var3 += var2;
         ++var2;
      }

      return var3;
   }

   public void unrelated(int var1) {
      int var2 = 0;

      int var3;
      for(var3 = 0; var1 > var2; var3 += var2++) {
      }

      System.out.println(var3);
      System.out.println(var3);
   }
}
