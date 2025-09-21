package dk.brics.grammar;

import java.util.Collection;
import java.util.List;

public class Production {
   private String nonterminal;
   private List<Entity> entities;
   boolean unordered;
   private ProductionID id;
   private int priority;
   private boolean unfolded;

   public Production(String var1, List<Entity> var2, boolean var3, ProductionID var4, int var5) {
      this.nonterminal = var1;
      this.entities = var2;
      this.unordered = var3;
      this.id = var4;
      this.priority = var5;
   }

   public String print(Collection<Entity> var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(this.nonterminal).append("->");
      boolean var3 = true;

      for (Entity var5 : this.entities) {
         if (var3) {
            var3 = false;
         } else {
            var2.append(",");
         }

         if (var1.contains(var5)) {
            var2.append(var5);
         } else {
            var2.append("(").append(var5).append(")");
         }
      }

      return var2.toString();
   }

   public String getNonterminal() {
      return this.nonterminal;
   }

   public boolean isUnordered() {
      return this.unordered;
   }

   public List<Entity> getEntities() {
      return this.entities;
   }

   public void setEntities(List<Entity> var1) {
      this.entities = var1;
   }

   public ProductionID getID() {
      return this.id;
   }

   public int getPriority() {
      return this.priority;
   }

   public void setUnfolded() {
      this.unfolded = true;
   }

   public boolean isUnfolded() {
      return this.unfolded;
   }
}
