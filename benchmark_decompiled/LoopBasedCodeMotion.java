public class LoopBasedCodeMotion {
   public int original() {
      int var1;
      for(var1 = 0; var1 < 3; ++var1) {
      }

      return 5 * var1;
   }

   public int expected() {
      int var1;
      for(var1 = 0; 3 > var1; var1 += 5) {
      }

      return var1;
   }
}
