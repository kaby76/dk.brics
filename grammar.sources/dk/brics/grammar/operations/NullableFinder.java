package dk.brics.grammar.operations;

import dk.brics.grammar.EOFTerminalEntity;
import dk.brics.grammar.Entity;
import dk.brics.grammar.EntityVisitor;
import dk.brics.grammar.Grammar;
import dk.brics.grammar.NonterminalEntity;
import dk.brics.grammar.Production;
import dk.brics.grammar.RegexpTerminalEntity;
import dk.brics.grammar.StringTerminalEntity;
import dk.brics.grammar.VoidEntityVisitor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class NullableFinder {
   private Set<Entity> nullable_entities = new HashSet<>();
   private Set<String> nullable_nonterminals = new HashSet<>();
   private Set<Production> nullable_productions = new HashSet<>();

   public NullableFinder(Grammar var1) {
      Stack var2 = new Stack();
      final HashMap var3 = new HashMap();

      for (final Production var5 : var1.getProductions()) {
         boolean var6 = true;

         for (Entity var8 : var5.getEntities()) {
            if (var8.visitBy(new EntityVisitor<Boolean>() {
               public Boolean visitNonterminalEntity(NonterminalEntity var1) {
                  var var2 = (Set)var3.get(var1.getNonterminal());
                  if (var2 == null) {
                     var2 = new HashSet();
                     var3.put(var1.getNonterminal(), var2);
                  }

                  var2.add(var5);
                  return false;
               }

               public Boolean visitRegexpTerminalEntity(RegexpTerminalEntity var1) {
                  return var1.getAutomaton().run("");
               }

               public Boolean visitStringTerminalEntity(StringTerminalEntity var1) {
                  return var1.getString().length() == 0;
               }

               public Boolean visitEOFTerminalEntity(EOFTerminalEntity var1) {
                  return true;
               }
            })) {
               this.nullable_entities.add(var8);
            } else {
               var6 = false;
            }
         }

         if (var6) {
            var2.push(var5.getNonterminal());
         }
      }

      while (!var2.isEmpty()) {
         String var10 = (String)var2.pop();
         this.nullable_nonterminals.add(var10);
         if (var3.get(var10) != null) {
            for (var var16 : (Set)var3.get(var10)) {
               boolean var18 = true;

               for (Entity var9 : ((Production)var16).getEntities()) {
                  var18 &= var9.visitBy(new EntityVisitor<Boolean>() {
                     public Boolean visitNonterminalEntity(NonterminalEntity var1) {
                        return NullableFinder.this.nullable_nonterminals.contains(var1.getNonterminal());
                     }

                     public Boolean visitRegexpTerminalEntity(RegexpTerminalEntity var1) {
                        return NullableFinder.this.nullable_entities.contains(var1);
                     }

                     public Boolean visitStringTerminalEntity(StringTerminalEntity var1) {
                        return NullableFinder.this.nullable_entities.contains(var1);
                     }

                     public Boolean visitEOFTerminalEntity(EOFTerminalEntity var1) {
                        return NullableFinder.this.nullable_entities.contains(var1);
                     }
                  });
               }

               if (var18 && !this.nullable_nonterminals.contains(((Production)var16).getNonterminal())) {
                  var2.push(((Production)var16).getNonterminal());
               }
            }
         }
      }

      for (Production var14 : var1.getProductions()) {
         for (Entity var19 : var14.getEntities()) {
            var19.visitBy(new VoidEntityVisitor() {
               @Override
               public void visitNonterminal(NonterminalEntity var1) {
                  if (NullableFinder.this.nullable_nonterminals.contains(var1.getNonterminal())) {
                     NullableFinder.this.nullable_entities.add(var1);
                  }
               }
            });
         }
      }

      for (Production var15 : var1.getProductions()) {
         if (this.nullable_entities.containsAll(var15.getEntities())) {
            this.nullable_productions.add(var15);
         }
      }
   }

   public Set<Entity> getNullableEntities() {
      return this.nullable_entities;
   }

   public Set<String> getNullableNonterminals() {
      return this.nullable_nonterminals;
   }

   public Set<Production> getNullableProductions() {
      return this.nullable_productions;
   }
}
