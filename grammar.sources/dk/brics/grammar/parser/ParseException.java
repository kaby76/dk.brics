package dk.brics.grammar.parser;

public class ParseException extends Exception {
   private static final long serialVersionUID = 1L;
   Location loc;

   ParseException(String var1, String var2, int var3) {
      super("parse error at character " + var3);
      this.loc = new Location(var1, var2, var3);
   }

   @Override
   public String getMessage() {
      String var1;
      if (this.loc.getIndex() >= 0) {
         var1 = "parse error at character " + this.loc.getIndex() + " (line " + this.loc.getLine() + ", column " + this.loc.getColumn() + ")";
      } else {
         var1 = "parse error (line " + this.loc.getLine() + ", column " + this.loc.getColumn() + ")";
      }

      if (this.loc.getFile() != null) {
         var1 = var1 + " in " + this.loc.getFile();
      }

      return var1;
   }

   public void setLocation(Location var1) {
      this.loc = var1;
   }

   public Location getLocation() {
      return this.loc;
   }
}
