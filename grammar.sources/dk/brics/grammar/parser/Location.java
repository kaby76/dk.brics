package dk.brics.grammar.parser;

import java.io.Serializable;

public class Location implements Serializable {
   private static final long serialVersionUID = 1L;
   private String file;
   private int index;
   private int line;
   private int col;

   public Location(String var1, String var2, int var3) {
      this.file = var1;
      this.index = var3;
      this.setLineCol(var2);
   }

   public Location(String var1, int var2, int var3, int var4) {
      this.file = var1;
      this.index = var2;
      this.line = var3;
      this.col = var4;
   }

   private void setLineCol(String var1) {
      this.line = this.col = 1;

      for (int var2 = 0; var2 < this.index; var2++) {
         char var3 = var1.charAt(var2);
         if (var3 != '\n' && var3 != '\r') {
            if (var3 == '\t') {
               while (true) {
                  this.col++;
                  if (this.col % 8 == 1) {
                     break;
                  }
               }
            } else {
               this.col++;
            }
         } else {
            this.line++;
            this.col = 1;
            if (var2 + 1 < this.index && var3 == '\r' && var1.charAt(var2 + 1) == '\n') {
               var2++;
            }
         }
      }
   }

   public int getIndex() {
      return this.index;
   }

   public String getFile() {
      return this.file;
   }

   public int getLine() {
      return this.line;
   }

   public int getColumn() {
      return this.col;
   }
}
