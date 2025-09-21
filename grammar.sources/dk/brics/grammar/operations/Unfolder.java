package dk.brics.grammar.operations;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import dk.brics.grammar.EOFTerminalEntity;
import dk.brics.grammar.Entity;
import dk.brics.grammar.EntityVisitor;
import dk.brics.grammar.Grammar;
import dk.brics.grammar.NonterminalEntity;
import dk.brics.grammar.Production;
import dk.brics.grammar.RegexpTerminalEntity;
import dk.brics.grammar.StringTerminalEntity;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Unfolder {
   private PrintWriter out;

   public Unfolder(PrintWriter var1) {
      this.out = var1;
   }

   public Grammar unfold(Grammar var1, int var2, final String var3, final String var4) throws IllegalArgumentException {
      ArrayList var5 = new ArrayList();
      boolean var6 = false;
      final Stack var7 = new Stack();

      for (int var8 = 0; var8 <= var2; var8++) {
         ArrayList var9 = new ArrayList();

         for (final Production var11 : var1.getProductions()) {
            List var12 = var11.getEntities();
            ArrayList var13 = new ArrayList();

            for (var var15 : var12) {
               int var16 = ((Entity)var15).visitBy(
                  new EntityVisitor<Integer>() {
                     public Integer visitNonterminalEntity(NonterminalEntity var1) {
                        return 0;
                     }

                     public Integer visitRegexpTerminalEntity(RegexpTerminalEntity var1) {
                        if (Unfolder.this.contains(var1.getAutomaton(), var4)) {
                           throw new IllegalArgumentException(
                              Unfolder.this.productionName(var11) + ": can't unfold grammar with parenthesis in regexp terminal"
                           );
                        } else {
                           return 0;
                        }
                     }

                     public Integer visitStringTerminalEntity(StringTerminalEntity var1) {
                        String var2 = var1.getString();
                        if (var2.length() == 1) {
                           char var7x = var2.charAt(0);
                           if (Unfolder.this.contains(var2, var3)) {
                              var7.push(var7x);
                              return 1;
                           }

                           if (Unfolder.this.contains(var2, var4)) {
                              if (!var7.isEmpty() && (Character)var7.peek() == var3.charAt(var4.indexOf(var7x))) {
                                 var7.pop();
                                 return 0;
                              }

                              throw new IllegalArgumentException(Unfolder.this.productionName(var11) + ": can't unfold grammar with non-balanced parentheses");
                           }
                        } else if (Unfolder.this.contains(var2, var4) || Unfolder.this.contains(var2, var3)) {
                           if (var2.length() == 2) {
                              char var3x = var2.charAt(0);
                              char var4x = var2.charAt(1);
                              int var5 = var3.indexOf(var3x);
                              int var6 = var4.indexOf(var4x);
                              if (var5 >= 0 && var5 == var6) {
                                 return 0;
                              }
                           }

                           throw new IllegalArgumentException(
                              Unfolder.this.productionName(var11) + ": can't unfold grammar with parenthesis in non-singleton string terminal"
                           );
                        }

                        return 0;
                     }

                     public Integer visitEOFTerminalEntity(EOFTerminalEntity var1) {
                        return 0;
                     }
                  }
               );
               if (!var7.isEmpty()) {
                  if (var11.isUnordered()) {
                     throw new IllegalArgumentException(this.productionName(var11) + ": can't unfold grammar with unordered parenthesis productions");
                  }

                  var6 = true;
               }

               final int var17 = var7.size() - var16 + var8 < var2 ? var7.size() - var16 + var8 : var2;
               var13.add(
                  ((Entity)var15).visitBy(
                     new EntityVisitor<Entity>() {
                        public Entity visitNonterminalEntity(NonterminalEntity var1) {
                           return new NonterminalEntity(Unfolder.this.expandName(var1.getNonterminal(), var17), var1.getLabel(), var1.getExample());
                        }

                        public Entity visitRegexpTerminalEntity(RegexpTerminalEntity var1) {
                           return new RegexpTerminalEntity(
                              AutomataOperations.expandAlphabet(var1.getAutomaton(), (char)var17), var1.isMax(), var1.getAutomatonName(), var1.getLabel(), null
                           );
                        }

                        public Entity visitStringTerminalEntity(StringTerminalEntity var1) {
                           return new StringTerminalEntity(Unfolder.this.expandAlphabet(var1.getString(), (char)var17));
                        }

                        public Entity visitEOFTerminalEntity(EOFTerminalEntity var1) {
                           return new EOFTerminalEntity();
                        }
                     }
                  )
               );
            }

            if (!var7.isEmpty()) {
               throw new IllegalArgumentException(this.productionName(var11) + ": can't unfold grammar with non-balanced parentheses");
            }

            Production var19 = new Production(this.expandName(var11.getNonterminal(), var8), var13, var11.isUnordered(), var11.getID(), var11.getPriority());
            if (var8 > 0) {
               var19.setUnfolded();
            }

            var5.add(var19);
            var9.add(var19);
         }
      }

      if (!var6) {
         this.out.println("*** warning: no productions matching the given unfolding parentheses");
      }

      Grammar var18 = new Grammar(this.expandName(var1.getStart(), 0), var5);
      var18.setUnfolded(true);
      return var18;
   }

   private String productionName(Production var1) {
      return "production " + var1.getNonterminal() + "[" + var1.getID().getLabel() + "]";
   }

   private String expandAlphabet(String var1, char var2) {
      StringBuilder var3 = new StringBuilder();

      for (int var4 = 0; var4 < var1.length(); var4++) {
         var3.append(var2).append(var1.charAt(var4));
      }

      return var3.toString();
   }

   private String expandName(String var1, int var2) {
      return var2 > 0 ? var1 + "#" + var2 : var1;
   }

   private boolean contains(Automaton var1, String var2) {
      String var3 = var1.getSingleton();
      if (var3 != null) {
         return this.contains(var3, var2);
      } else {
         for (State var5 : var1.getStates()) {
            for (Transition var7 : var5.getTransitions()) {
               for (int var8 = 0; var8 < var2.length(); var8++) {
                  char var9 = var2.charAt(var8);
                  if (var7.getMin() <= var9 && var9 <= var7.getMax()) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   private boolean contains(String var1, String var2) {
      for (int var3 = 0; var3 < var1.length(); var3++) {
         if (var2.indexOf(var1.charAt(var3)) >= 0) {
            return true;
         }
      }

      return false;
   }
}
