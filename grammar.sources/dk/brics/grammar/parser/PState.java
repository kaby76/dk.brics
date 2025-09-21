package dk.brics.grammar.parser;

import dk.brics.grammar.Entity;
import dk.brics.grammar.Production;
import dk.brics.grammar.ast.BranchNode;
import java.util.List;

class PState implements Comparable<PState> {
   Production prod;
   List<Entity> remaining;
   int next_entity;
   int from;
   int current;
   BranchNode ast;
   int serial;
   static int counter;

   private PState(Production var1, List<Entity> var2, int var3, int var4, int var5, BranchNode var6) {
      this.prod = var1;
      this.remaining = var2;
      this.next_entity = var3;
      this.from = var4;
      this.current = var5;
      this.ast = var6;
      this.serial = counter++;
   }

   static PState makeNew(Production var0, int var1, BranchNode var2) {
      return new PState(var0, var0.isUnordered() ? var0.getEntities() : null, 0, var1, var1, var2);
   }

   static PState makeNextUnordered(Production var0, List<Entity> var1, int var2, int var3, BranchNode var4) {
      return new PState(var0, var1, 0, var2, var3, var4);
   }

   static PState makeNextOrdered(Production var0, int var1, int var2, int var3, BranchNode var4) {
      return new PState(var0, null, var1, var2, var3, var4);
   }

   boolean isDone() {
      return this.prod.isUnordered() ? this.remaining.isEmpty() : this.next_entity == this.prod.getEntities().size();
   }

   Entity getNextRemaining() {
      return this.prod.getEntities().get(this.next_entity);
   }

   List<Entity> getRemaining() {
      return this.prod.isUnordered() ? this.remaining : this.prod.getEntities().subList(this.next_entity, this.prod.getEntities().size());
   }

   public int getFrom() {
      return this.from;
   }

   public int getCurrent() {
      return this.current;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof PState)) {
         return false;
      } else {
         PState var2 = (PState)var1;
         return this.prod.isUnordered()
            ? var2.prod == this.prod && var2.from == this.from && var2.current == this.current && var2.remaining.equals(this.remaining)
            : var2.prod == this.prod && var2.next_entity == this.next_entity && var2.from == this.from && var2.current == this.current;
      }
   }

   @Override
   public int hashCode() {
      int var1;
      if (this.prod.isUnordered()) {
         var1 = this.remaining.size();
      } else {
         var1 = this.next_entity;
      }

      return this.prod.hashCode() + var1 * 5 + this.from * 3 + this.current * 2;
   }

   public int compareTo(PState var1) {
      if (this.prod.getPriority() > var1.prod.getPriority()) {
         return -1;
      } else if (this.prod.getPriority() < var1.prod.getPriority()) {
         return 1;
      } else if (this.current > var1.current) {
         return -1;
      } else {
         return this.current < var1.current ? 1 : this.serial - var1.serial;
      }
   }
}
