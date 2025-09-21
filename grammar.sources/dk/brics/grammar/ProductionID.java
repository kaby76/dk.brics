package dk.brics.grammar;

public class ProductionID {
   private String label;

   public ProductionID() {
   }

   public ProductionID(String var1) {
      this.label = var1;
   }

   public String getLabel() {
      return this.label;
   }

   public void setLabel(String var1) {
      this.label = var1;
   }

   public boolean hasExplicitLabel() {
      return this.label != null && !this.label.startsWith("#");
   }
}
