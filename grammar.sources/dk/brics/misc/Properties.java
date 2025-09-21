package dk.brics.misc;

public class Properties {
   private Properties() {
   }

   public static boolean get(String var0) {
      String var1 = System.getProperty(var0);
      return var1 != null ? !var1.equals("false") && !var1.equals("0") : false;
   }

   public static String[] getStrings(String var0) {
      String var1 = System.getProperty(var0);
      return var1 == null ? null : var1.split(",");
   }
}
