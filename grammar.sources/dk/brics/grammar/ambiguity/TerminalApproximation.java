package dk.brics.grammar.ambiguity;

import dk.brics.automaton.Automaton;
import dk.brics.grammar.EOFTerminalEntity;
import dk.brics.grammar.Entity;
import dk.brics.grammar.EntityVisitor;
import dk.brics.grammar.NonterminalEntity;
import dk.brics.grammar.Production;
import dk.brics.grammar.RegexpTerminalEntity;
import dk.brics.grammar.StringTerminalEntity;
import dk.brics.grammar.operations.CharSet;
import dk.brics.grammar.operations.FirstLastFinder;
import dk.brics.grammar.operations.NullableFinder;
import dk.brics.grammar.operations.TerminalFinder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TerminalApproximation extends ApproximationStrategy {
   private TerminalFinder tf;
   private Map<Production, Automaton> prodmay;
   private Map<String, Automaton> nonterminalmay;
   private Map<Entity, CharSet> entityfirst;
   private Map<Entity, CharSet> entitylast;
   private Map<Integer, CharSet> leftmay;
   private Map<Integer, CharSet> rightmay;
   private Set<Production> nullable_productions;

   @Override
   public String getName() {
      return "terminal";
   }

   @Override
   public void init() {
      if (this.debug) {
         this.out.println("computing nullables");
      }

      this.nullable_productions = new NullableFinder(this.g).getNullableProductions();
      if (this.debug) {
         this.out.println("computing terminal sets");
      }

      this.tf = new TerminalFinder(this.g);
      FirstLastFinder var1 = new FirstLastFinder(this.g, this.g.isUnfolded());
      this.entityfirst = var1.getEntityFirst();
      this.entitylast = var1.getEntityLast();
      this.clearProductionCache();
      this.clearLeftCache();
      this.nonterminalmay = new HashMap<>();
   }

   @Override
   public void verticalDone() {
      this.clearProductionCache();
   }

   @Override
   public void horizontalDone() {
      this.clearLeftCache();
   }

   private void clearProductionCache() {
      this.prodmay = new HashMap<>();
   }

   private void clearLeftCache() {
      this.leftmay = new HashMap<>();
      this.rightmay = new HashMap<>();
   }

   private Automaton getMayAutomaton(Production var1) {
      Automaton var2 = this.prodmay.get(var1);
      if (var2 == null) {
         var2 = Automaton.union(this.tf.getMayTerminals(var1).getCollection()).repeat();
         this.prodmay.put(var1, var2);
      }

      return var2;
   }

   private Automaton getMayAutomaton(Entity var1) {
      return var1.visitBy(new EntityVisitor<Automaton>() {
         public Automaton visitNonterminalEntity(NonterminalEntity var1) {
            Automaton var2 = TerminalApproximation.this.nonterminalmay.get(var1.getNonterminal());
            if (var2 == null) {
               var2 = Automaton.union(TerminalApproximation.this.tf.getMayTerminals(var1.getNonterminal()).getCollection());
               TerminalApproximation.this.nonterminalmay.put(var1.getNonterminal(), var2);
            }

            return var2;
         }

         public Automaton visitRegexpTerminalEntity(RegexpTerminalEntity var1) {
            return var1.getAutomaton();
         }

         public Automaton visitStringTerminalEntity(StringTerminalEntity var1) {
            return Automaton.makeString(var1.getString());
         }

         public Automaton visitEOFTerminalEntity(EOFTerminalEntity var1) {
            return Automaton.makeEmptyString();
         }
      });
   }

   private CharSet getMayCharsLeft(Production var1, int var2) {
      CharSet var3 = this.leftmay.get(var2);
      if (var3 == null) {
         var3 = new CharSet(this.getMayAutomaton(var1.getEntities().get(var2 - 1)), false, false, this.g.isUnfolded());
         if (var2 > 1) {
            var3.add(this.getMayCharsLeft(var1, var2 - 1));
         }

         this.leftmay.put(var2, var3);
      }

      return var3;
   }

   private CharSet getMayCharsRight(Production var1, int var2) {
      CharSet var3 = this.rightmay.get(var2);
      if (var3 == null) {
         var3 = new CharSet(this.getMayAutomaton(var1.getEntities().get(var2)), false, false, this.g.isUnfolded());
         if (var2 + 1 < var1.getEntities().size()) {
            var3.add(this.getMayCharsRight(var1, var2 + 1));
         }

         this.rightmay.put(var2, var3);
      }

      return var3;
   }

   @Override
   public HorizontalOverlapString checkHorizontalOverlap(Production var1, int var2) {
      if (this.getMayCharsLeft(var1, var2).disjoint(this.entityfirst.get(var1.getEntities().get(var2)))) {
         return null;
      } else {
         return this.entitylast.get(var1.getEntities().get(var2 - 1)).disjoint(this.getMayCharsRight(var1, var2)) ? null : HORIZONTAL_NOT_APPLICABLE;
      }
   }

   @Override
   public VerticalOverlapString checkVerticalOverlap(Production var1, Production var2) {
      boolean var3 = var1.getEntities().size() == 0;
      boolean var4 = var2.getEntities().size() == 0;
      if (var3 && var4) {
         return new VerticalOverlapString("");
      } else {
         boolean var5 = this.nullable_productions.contains(var1);
         boolean var6 = this.nullable_productions.contains(var2);
         if (var5 && var6) {
            return new VerticalOverlapString("");
         } else if ((!var3 || var6) && (!var4 || var5)) {
            if (!var5 && !var6) {
               if (this.entityfirst.get(var1.getEntities().get(0)).disjoint(this.entityfirst.get(var2.getEntities().get(0)))) {
                  return null;
               }

               if (this.entitylast
                  .get(var1.getEntities().get(var1.getEntities().size() - 1))
                  .disjoint(this.entitylast.get(var2.getEntities().get(var2.getEntities().size() - 1)))) {
                  return null;
               }
            }

            for (Automaton var8 : this.tf.getMustTerminals(var1)) {
               if (var8.intersection(this.getMayAutomaton(var2)).isEmpty()) {
                  return null;
               }
            }

            for (Automaton var10 : this.tf.getMustTerminals(var2)) {
               if (var10.intersection(this.getMayAutomaton(var1)).isEmpty()) {
                  return null;
               }
            }

            return VERTICAL_NOT_APPLICABLE;
         } else {
            return null;
         }
      }
   }
}
