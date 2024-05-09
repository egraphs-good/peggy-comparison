public class LoopUnroll {
   public int original(int var1) {
      for(var1 = 0; 1 > var1; ++var1) {
      }

      return var1;
   }

   public int expected(int var1) {
      return 1;
   }
}
