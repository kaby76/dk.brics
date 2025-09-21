package dk.brics.grammar.ambiguity;

import dk.brics.automaton.Automaton;
import dk.brics.grammar.EOFTerminalEntity;
import dk.brics.grammar.Entity;
import dk.brics.grammar.EntityVisitor;
import dk.brics.grammar.Grammar;
import dk.brics.grammar.NonterminalEntity;
import dk.brics.grammar.Production;
import dk.brics.grammar.RegexpTerminalEntity;
import dk.brics.grammar.StringTerminalEntity;
import java.util.ArrayList;

public class RegularApproximation2 extends RegularApproximation {
   boolean enabled;

   @Override
   public String getName() {
      return "regular2";
   }

   @Override
   public void init() {
      int var1 = 0;

      for (Production var3 : this.g.getProductions()) {
         int var4 = this.getLevel(var3.getNonterminal());
         if (var4 > var1) {
            var1 = var4;
         }
      }

      if (var1 > 0) {
         this.enabled = true;
      }

      if (this.enabled) {
         this.g = new Grammar(this.g);
         Automaton var10 = Automaton.makeChar((char)var1).concatenate(Automaton.makeAnyChar()).repeat();
         final RegexpTerminalEntity var11 = new RegexpTerminalEntity(var10, false, "[ANY]", null, null);
         final int var12 = var1;

         for (Production var6 : this.g.getProductions()) {
            if (this.getLevel(var6.getNonterminal()) == var1 - 1) {
               ArrayList var7 = new ArrayList();

               for (Entity var9 : var6.getEntities()) {
                  var7.add(var9.visitBy(new EntityVisitor<Entity>() {
                     public Entity visitNonterminalEntity(NonterminalEntity var1) {
                        return (Entity)(RegularApproximation2.this.getLevel(var1.getNonterminal()) == var12 ? var11 : var1);
                     }

                     public Entity visitRegexpTerminalEntity(RegexpTerminalEntity var1) {
                        return var1;
                     }

                     public Entity visitStringTerminalEntity(StringTerminalEntity var1) {
                        return var1;
                     }

                     public Entity visitEOFTerminalEntity(EOFTerminalEntity var1) {
                        return var1;
                     }
                  }));
               }

               var6.setEntities(var7);
            }
         }

         if (this.debug) {
            this.out.print("regular2 grammar:\n" + this.g);
         }

         super.init();
      }
   }

   private int getLevel(String var1) {
      int var2 = var1.indexOf(35);
      return var2 > 0 ? Integer.parseInt(var1.substring(var2 + 1)) : 0;
   }

   @Override
   public HorizontalOverlapString checkHorizontalOverlap(Production var1, int var2) {
      return this.enabled ? super.checkHorizontalOverlap(var1, var2) : HORIZONTAL_NOT_APPLICABLE;
   }

   @Override
   public VerticalOverlapString checkVerticalOverlap(Production var1, Production var2) {
      return this.enabled ? super.checkVerticalOverlap(var1, var2) : VERTICAL_NOT_APPLICABLE;
   }
}
