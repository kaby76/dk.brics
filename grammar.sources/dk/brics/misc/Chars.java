package dk.brics.misc;

public class Chars {
   private Chars() {
   }

   public static String escape(String var0) {
      return escape(var0, false);
   }

   public static String escape(String var0, boolean var1) {
      StringBuilder var2 = new StringBuilder();

      for (int var3 = 0; var3 < var0.length(); var3++) {
         char var4 = var0.charAt(var3);
         String var5;
         int var6;
         switch (var4) {
            case '\b':
               var2.append("\\b");
               continue;
            case '\t':
               var2.append("\\t");
               continue;
            case '\n':
               var2.append("\\n");
               continue;
            case '\f':
               var2.append("\\f");
               continue;
            case '\r':
               var2.append("\\r");
               continue;
            case '"':
               var2.append("\\\"");
               continue;
            case '\\':
               var2.append("\\\\");
               continue;
            default:
               if (var4 >= ' ' && var4 <= '~' && var4 != '\\' && var4 != '"') {
                  var2.append(var4);
                  continue;
               }

               var2.append("\\u");
               var5 = Integer.toHexString(var4 & '\uffff');
               var6 = 0;
         }

         while (var6 + var5.length() < 4) {
            var2.append('0');
            var6++;
         }

         var2.append(var5);
      }

      return var2.toString();
   }
}
