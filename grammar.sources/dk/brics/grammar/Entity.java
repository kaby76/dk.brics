package dk.brics.grammar;

public abstract class Entity {
   private String label;
   private String example;

   protected Entity(String var1, String var2) throws GrammarException {
      this.label = var1;
      this.example = var2;
   }

   public boolean isLabeled() {
      return this.label != null;
   }

   public String getExample() {
      return this.example;
   }

   public String getLabel() {
      return this.label;
   }

   public void setLabel(String var1) {
      this.label = var1;
   }

   public boolean isExplicitlyLabeled() {
      return this.label != null && !this.label.startsWith("#");
   }

   public abstract <T> T visitBy(EntityVisitor<T> var1);
}
