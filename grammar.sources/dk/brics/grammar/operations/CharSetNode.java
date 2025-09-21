package dk.brics.grammar.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

class CharSetNode {
   private CharSet cs = new CharSet();
   private Collection<CharSetNode> in = new ArrayList<>();
   private Collection<CharSetNode> out = new ArrayList<>();

   CharSet getCS() {
      return this.cs;
   }

   void addIn(CharSetNode var1) {
      if (var1 != null && !this.in.contains(var1) && var1 != this) {
         this.in.add(var1);
         var1.getOut().add(this);
      }
   }

   Collection<CharSetNode> getIn() {
      return this.in;
   }

   Collection<CharSetNode> getOut() {
      return this.out;
   }

   static void fixpoint(Set<CharSetNode> var0) {
      while (!var0.isEmpty()) {
         CharSetNode var1 = (CharSetNode)var0.iterator().next();
         var0.remove(var1);
         boolean var2 = false;

         for (CharSetNode var4 : var1.getIn()) {
            var2 |= var1.getCS().add(var4.getCS());
         }

         if (var2) {
            var0.addAll(var1.getOut());
         }
      }
   }
}
