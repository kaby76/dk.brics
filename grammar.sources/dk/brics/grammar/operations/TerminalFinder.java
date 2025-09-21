package dk.brics.grammar.operations;

import dk.brics.automaton.Automaton;
import dk.brics.grammar.EOFTerminalEntity;
import dk.brics.grammar.Entity;
import dk.brics.grammar.EntityVisitor;
import dk.brics.grammar.Grammar;
import dk.brics.grammar.NonterminalEntity;
import dk.brics.grammar.Production;
import dk.brics.grammar.RegexpTerminalEntity;
import dk.brics.grammar.StringTerminalEntity;
import dk.brics.grammar.TerminalEntity;
import dk.brics.grammar.VoidEntityVisitor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class TerminalFinder {
   private Map<String, AutomataCollection> nonterminal_may = new HashMap<>();
   private Map<String, AutomataCollection> nonterminal_must = new HashMap<>();
   private Map<Production, AutomataCollection> production_may = new HashMap<>();
   private Map<Production, AutomataCollection> production_must = new HashMap<>();

   public TerminalFinder(Grammar var1) {
      final HashMap var2 = new HashMap();
      final HashMap var3 = new HashMap();

      for (String var5 : var1.getNonterminals()) {
         this.nonterminal_may.put(var5, new AutomataCollection());
      }

      HashSet var16 = new HashSet();
      Stack var17 = new Stack();

      for (final Production var7 : var1.getProductions()) {
         String var8 = var7.getNonterminal();
         AutomataCollection var9 = new AutomataCollection();
         this.production_may.put(var7, var9);
         AutomataCollection var10 = this.nonterminal_may.get(var8);

         for (Entity var12 : var7.getEntities()) {
            Automaton var13 = var12.visitBy(new EntityVisitor<Automaton>() {
               public Automaton visitNonterminalEntity(NonterminalEntity var1) {
                  var var2 = (Set)var3.get(var1.getNonterminal());
                  if (var2 == null) {
                     var2 = new HashSet();
                     var3.put(var1.getNonterminal(), var2);
                  }

                  var2.add(var7);
                  return null;
               }

               public Automaton visitRegexpTerminalEntity(RegexpTerminalEntity var1) {
                  return var1.getAutomaton();
               }

               public Automaton visitStringTerminalEntity(StringTerminalEntity var1) {
                  return Automaton.makeString(var1.getString());
               }

               public Automaton visitEOFTerminalEntity(EOFTerminalEntity var1) {
                  return null;
               }
            });
            if (var13 != null) {
               var2.put((TerminalEntity)var12, var13);
               var10.add(var13);
               var9.add(var13);
            }
         }
      }

      var16.addAll(var1.getNonterminals());
      var17.addAll(var1.getNonterminals());

      while (!var17.isEmpty()) {
         String var18 = (String)var17.pop();
         var16.remove(var18);
         AutomataCollection var22 = this.nonterminal_may.get(var18);
         if (var3.get(var18) != null) {
            for (var var28 : (Set)var3.get(var18)) {
               String var31 = ((Production)var28).getNonterminal();
               this.production_may.get(var28).addAll(var22);
               boolean var34 = this.nonterminal_may.get(var31).addAll(var22);
               if (var34 && !var16.contains(var31)) {
                  var16.add(var31);
                  var17.add(var31);
               }
            }
         }
      }

      for (String var23 : var1.getNonterminals()) {
         this.nonterminal_must.put(var23, new AutomataCollection(this.nonterminal_may.get(var23)));
      }

      for (Production var24 : var1.getProductions()) {
         this.production_must.put(var24, new AutomataCollection(this.production_may.get(var24)));
      }

      var16.addAll(var1.getNonterminals());
      var17.addAll(var1.getNonterminals());

      while (!var17.isEmpty()) {
         String var21 = (String)var17.pop();
         var16.remove(var21);
         AutomataCollection var25 = this.nonterminal_must.get(var21);
         boolean var27 = false;

         for (Production var32 : var1.getProductions(var21)) {
            var27 |= var25.retainAll(this.production_must.get(var32));
         }

         if (var27 && var3.get(var21) != null) {
            for (var var33 : (Set)var3.get(var21)) {
               String var35 = ((Production)var33).getNonterminal();
               AutomataCollection var36 = this.production_must.get(var33);
               final AutomataCollection var37 = new AutomataCollection();

               for (Entity var15 : ((Production)var33).getEntities()) {
                  var15.visitBy(new VoidEntityVisitor() {
                     @Override
                     public void visitNonterminal(NonterminalEntity var1) {
                        var37.addAll(TerminalFinder.this.nonterminal_must.get(var1.getNonterminal()));
                     }

                     @Override
                     public void visitTerminal(TerminalEntity var1) {
                        var37.add((Automaton)var2.get(var1));
                     }
                  });
               }

               boolean var38 = var36.retainAll(var37);
               if (var38 && !var16.contains(var35)) {
                  var16.add(var35);
                  var17.add(var35);
               }
            }
         }
      }
   }

   public AutomataCollection getMayTerminals(String var1) {
      AutomataCollection var2 = this.nonterminal_may.get(var1);
      return var2 == null ? AutomataCollection.emptyAutomataCollection : var2;
   }

   public AutomataCollection getMustTerminals(String var1) {
      AutomataCollection var2 = this.nonterminal_must.get(var1);
      return var2 == null ? AutomataCollection.emptyAutomataCollection : var2;
   }

   public AutomataCollection getMayTerminals(Production var1) {
      AutomataCollection var2 = this.production_may.get(var1);
      return var2 == null ? AutomataCollection.emptyAutomataCollection : var2;
   }

   public AutomataCollection getMustTerminals(Production var1) {
      AutomataCollection var2 = this.production_must.get(var1);
      return var2 == null ? AutomataCollection.emptyAutomataCollection : var2;
   }
}
