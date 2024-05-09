public class ConditionalConstantFolding {
   public int original(int var1) {
      if (var1 == 5) {
         var1 *= 4;
      } else {
         if (var1 == 4) {
            var1 = 5 * var1;
         } else {
            var1 = 20;
         }

         var1 = var1;
      }

      return var1;
   }

   public int expected() {
      return 20;
   }
}
