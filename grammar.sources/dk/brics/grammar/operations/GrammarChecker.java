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
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class GrammarChecker {
   private Grammar g;

   public int check(final Grammar var1, PrintWriter var2) {
      this.g = var1;
      int var3 = 0;
      final HashSet var4 = new HashSet();

      for (Production var6 : var1.getProductions()) {
         for (Entity var8 : var6.getEntities()) {
            var8.visitBy(new VoidEntityVisitor() {
               @Override
               public void visitNonterminal(NonterminalEntity var1x) {
                  if (var1.getProductions(var1x.getNonterminal()) == null) {
                     var4.add(var1x.getNonterminal());
                  }
               }
            });
         }
      }

      for (var var19 : var4) {
         var2.println("*** nonterminal '" + var19 + "' has no productions");
         var3++;
      }

      HashSet var18 = new HashSet();
      this.findReachable(var1.getStart(), var18);
      HashSet var20 = new HashSet<>(var1.getNonterminals());
      var20.removeAll(var18);

      for (var var23 : var20) {
         var2.println("*** nonterminal '" + var23 + "' is not reachable from the start nonterminal");
         var3++;
      }

      HashSet var22 = new HashSet();

      for (var var9 : var18) {
         if (!var4.contains(var9) && !this.checkProductive((String)var9, var22, new HashSet<>())) {
            var2.println("*** nonterminal '" + var9 + "' cannot derive any strings");
            var3++;
         }
      }

      for (String var27 : var1.getNonterminals()) {
         HashSet var10 = new HashSet();

         for (Production var12 : var1.getProductions(var27)) {
            String var13 = var12.getID().getLabel();
            if (var13 != null) {
               if (var10.contains(var13)) {
                  var2.println("*** multiple productions for '" + var12.getNonterminal() + "' with label '" + var13 + "'");
                  var3++;
               } else {
                  var10.add(var13);
               }
            }
         }
      }

      Map var26 = this.buildReverseEdgess();
      Set var28 = this.getMustEOFNonterminals(var26);
      Set var29 = new NullableFinder(var1).getNullableEntities();
      HashSet var30 = new HashSet();

      for (Production var33 : var1.getProductions()) {
         if (!var33.isUnordered()) {
            boolean var14 = false;

            for (Entity var16 : var33.getEntities()) {
               if (var14
                  || !(var16 instanceof EOFTerminalEntity)
                     && (!(var16 instanceof NonterminalEntity) || !var28.contains(((NonterminalEntity)var16).getNonterminal()))) {
                  if (var14 && !var29.contains(var16)) {
                     var30.add(var33.getNonterminal());
                  }
               } else {
                  var14 = true;
               }
            }
         }
      }

      for (var var34 : var30) {
         var2.println("*** nonterminal '" + var34 + "' cannot derive any strings (due to EOF terminals)");
         var3++;
      }

      return var3;
   }

   private void findReachable(String var1, final Set<String> var2) {
      if (!var2.contains(var1)) {
         var2.add(var1);
         if (this.g.getProductions(var1) != null) {
            for (Production var4 : this.g.getProductions(var1)) {
               for (Entity var6 : var4.getEntities()) {
                  var6.visitBy(new VoidEntityVisitor() {
                     @Override
                     public void visitNonterminal(NonterminalEntity var1) {
                        GrammarChecker.this.findReachable(var1.getNonterminal(), var2);
                     }
                  });
               }
            }
         }
      }
   }

   private boolean checkProductive(String var1, final Set<String> var2, final Set<String> var3) {
      boolean var4;
      if (var3.contains(var1)) {
         var4 = var2.contains(var1);
      } else {
         var3.add(var1);
         var4 = false;
         if (this.g.getProductions(var1) != null) {
            for (Production var6 : this.g.getProductions(var1)) {
               boolean var7 = true;

               for (Entity var9 : var6.getEntities()) {
                  var7 &= var9.visitBy(new EntityVisitor<Boolean>() {
                     public Boolean visitNonterminalEntity(NonterminalEntity var1) {
                        return GrammarChecker.this.checkProductive(var1.getNonterminal(), var2, var3);
                     }

                     public Boolean visitRegexpTerminalEntity(RegexpTerminalEntity var1) {
                        return !var1.getAutomaton().isEmpty();
                     }

                     public Boolean visitStringTerminalEntity(StringTerminalEntity var1) {
                        return true;
                     }

                     public Boolean visitEOFTerminalEntity(EOFTerminalEntity var1) {
                        return true;
                     }
                  });
               }

               if (var7) {
                  var2.add(var1);
                  var4 = true;
                  break;
               }
            }
         }
      }

      return var4;
   }

   private Map<String, Set<Production>> buildReverseEdgess() {
      final HashMap var1 = new HashMap();

      for (final Production var3 : this.g.getProductions()) {
         for (Entity var5 : var3.getEntities()) {
            var5.visitBy(new VoidEntityVisitor() {
               @Override
               public void visitNonterminal(NonterminalEntity var1x) {
                  var var2 = (Set)var1.get(var1x.getNonterminal());
                  if (var2 == null) {
                     var2 = new HashSet();
                     var1.put(var1x.getNonterminal(), var2);
                  }

                  var2.add(var3);
               }
            });
         }
      }

      return var1;
   }

   private Set<String> getMustEOFNonterminals(Map<String, Set<Production>> var1) {
      HashSet var2 = new HashSet();
      LinkedHashSet var3 = new LinkedHashSet<>(this.g.getNonterminals());

      while (!var3.isEmpty()) {
         String var4 = (String)var3.iterator().next();
         var3.remove(var4);
         boolean var5 = true;
         boolean var6 = false;

         for (Production var8 : this.g.getProductions(var4)) {
            boolean var9 = false;

            for (Entity var11 : var8.getEntities()) {
               if (var11 instanceof EOFTerminalEntity || var11 instanceof NonterminalEntity && var2.contains(((NonterminalEntity)var11).getNonterminal())) {
                  var9 = true;
               }
            }

            if (var9) {
               var6 = true;
            } else {
               var5 = false;
            }
         }

         if (var5 && var6 && var2.add(var4)) {
            Set var12 = (Set)var1.get(var4);
            if (var12 != null) {
               for (var var14 : var12) {
                  var3.add(((Production)var14).getNonterminal());
               }
            }
         }
      }

      return var2;
   }
}
