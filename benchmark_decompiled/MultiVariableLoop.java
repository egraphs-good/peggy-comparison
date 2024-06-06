public class MultiVariableLoop {
   public static void main(String[] var0) {
      int var2 = 4;
      int var3 = 0;

      while(true) {
         boolean var4;
         if (var3 < 10) {
            var4 = true;
         } else {
            var4 = false;
         }

         boolean var1;
         if (2 > var2) {
            var1 = true;
         } else {
            var1 = false;
         }

         if (!(var1 & var4)) {
            return;
         }

         System.out.println(var3);
         System.out.println(var2);
         System.out.println(var3 + var2);
         ++var2;
         ++var3;
      }
   }
}
