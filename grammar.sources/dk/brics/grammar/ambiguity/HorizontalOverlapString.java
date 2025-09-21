package dk.brics.grammar.ambiguity;

public class HorizontalOverlapString {
   private String s;
   private int i1;
   private int i2;

   public HorizontalOverlapString(String var1, int var2, int var3) {
      this.s = var1;
      this.i1 = var2;
      this.i2 = var3;
   }

   public String getString() {
      return this.s;
   }

   public String getX() {
      return this.s.substring(0, this.i1);
   }

   public String getXA() {
      return this.s.substring(0, this.i2);
   }

   public String getY() {
      return this.s.substring(this.i2);
   }

   public String getAY() {
      return this.s.substring(this.i1);
   }
}
