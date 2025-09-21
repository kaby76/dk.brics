package dk.brics.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class Loader {
   private Loader() {
   }

   public static String getString(String var0, String var1) throws IOException {
      BufferedReader var2 = null;
      if (var1 == null) {
         var1 = Charset.defaultCharset().name();
      }

      String var12;
      try {
         Object var3;
         try {
            var3 = new URL(var0).openStream();
         } catch (MalformedURLException var10) {
            var3 = new FileInputStream(var0);
         }

         var2 = new BufferedReader(new InputStreamReader((InputStream)var3, var1));
         StringBuilder var4 = new StringBuilder();
         boolean var5 = false;

         while (!var5) {
            int var6 = var2.read();
            if (var6 == -1) {
               var5 = true;
            } else {
               var4.append((char)var6);
            }
         }

         var12 = var4.toString();
      } finally {
         if (var2 != null) {
            var2.close();
         }
      }

      return var12;
   }

   public static String resolveRelative(String var0, String var1) throws MalformedURLException {
      String var2;
      try {
         var2 = new URL(new URL(var0), var1).toString();
      } catch (MalformedURLException var4) {
         var2 = new URL(new URL(new File(var0).toURI().toString()), var1).toString();
      }

      if (var2.startsWith("file:/")) {
         var2 = var2.substring(6);
      }

      return var2;
   }
}
