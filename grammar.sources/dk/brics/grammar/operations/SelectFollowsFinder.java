package dk.brics.grammar.operations;

import dk.brics.grammar.Entity;
import dk.brics.grammar.Grammar;
import dk.brics.grammar.NonterminalEntity;
import dk.brics.grammar.Production;
import dk.brics.grammar.TerminalEntity;
import dk.brics.grammar.VoidEntityVisitor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SelectFollowsFinder {
   private Map<String, CharSet> select;
   private Map<String, CharSet> follows;

   public SelectFollowsFinder(Grammar var1) {
      final HashMap var2 = new HashMap();

      for (Production var4 : var1.getProductions()) {
         for (Entity var6 : var4.getEntities()) {
            CharSet var7 = CharSet.getCharSet(var6, false, true);
            if (var7 != null) {
               var2.put(var6, var7);
            }
         }
      }

      final HashMap var16 = new HashMap();
      final HashMap var17 = new HashMap();
      final HashMap var18 = new HashMap();
      HashSet var19 = new HashSet();

      for (String var8 : var1.getNonterminals()) {
         CharSetNode var9 = new CharSetNode();
         var19.add(var9);
         var16.put(var8, var9);
         CharSetNode var10 = new CharSetNode();
         var19.add(var10);
         var17.put(var8, var10);
      }

      for (Production var23 : var1.getProductions()) {
         ArrayList var26 = new ArrayList();
         if (var23.isUnordered()) {
            CharSetNode var30 = new CharSetNode();
            var19.add(var30);
            var26.add(var30);
         } else {
            for (int var29 = 0; var29 <= var23.getEntities().size(); var29++) {
               CharSetNode var11 = new CharSetNode();
               var19.add(var11);
               var26.add(var11);
            }
         }

         var18.put(var23, var26);
      }

      Set var22 = new NullableFinder(var1).getNullableEntities();

      for (final Production var27 : var1.getProductions()) {
         String var31 = var27.getNonterminal();
         ((CharSetNode)var17.get(var31)).addIn((CharSetNode)((List)var18.get(var27)).get(0));
         List var32 = var27.getEntities();
         if (var27.isUnordered()) {
            for (int var33 = 0; var33 < var32.size(); var33++) {
               ((Entity)var32.get(var33)).visitBy(new VoidEntityVisitor() {
                  @Override
                  public void visitNonterminal(NonterminalEntity var1) {
                     if (var16.get(var1.getNonterminal()) != null) {
                        ((CharSetNode)var16.get(var1.getNonterminal())).addIn((CharSetNode)((List)var18.get(var27)).get(0));
                     }
                  }
               });
            }

            ((CharSetNode)((List)var18.get(var27)).get(0)).addIn((CharSetNode)var16.get(var31));

            for (int var34 = 0; var34 < var32.size(); var34++) {
               ((Entity)var32.get(var34)).visitBy(new VoidEntityVisitor() {
                  @Override
                  public void visitNonterminal(NonterminalEntity var1) {
                     ((CharSetNode)((List)var18.get(var27)).get(0)).addIn((CharSetNode)var17.get(var1.getNonterminal()));
                  }

                  @Override
                  public void visitTerminal(TerminalEntity var1) {
                     ((CharSetNode)((List)var18.get(var27)).get(0)).getCS().add((CharSet)var2.get(var1));
                  }
               });
            }
         } else {
            ((CharSetNode)((List)var18.get(var27)).get(var32.size())).addIn((CharSetNode)var16.get(var31));

            for (int var12 = 0; var12 < var32.size(); var12 = var12 + 1) {
               final int cap = var12;
               int var13 = var12;
               ((Entity)var32.get(var12)).visitBy(new VoidEntityVisitor() {
                  @Override
                  public void visitNonterminal(NonterminalEntity var1) {
                     if (var16.get(var1.getNonterminal()) != null) {
                        ((CharSetNode)var16.get(var1.getNonterminal())).addIn((CharSetNode)((List)var18.get(var27)).get(cap + 1));
                     }
                  }
               });
               boolean var14 = true;

               for (int var15 = var12; var15 < var32.size(); var15++) {
                  if (!var22.contains(var32.get(var15))) {
                     var14 = false;
                     break;
                  }
               }

               if (var14) {
                  ((CharSetNode)((List)var18.get(var27)).get(var12)).addIn((CharSetNode)var16.get(var31));
               }

               for (int var35 = var12; var35 < var32.size(); var35++) {
                  ((Entity)var32.get(var35)).visitBy(new VoidEntityVisitor() {
                     @Override
                     public void visitNonterminal(NonterminalEntity var1) {
                        ((CharSetNode)((List)var18.get(var27)).get(var13)).addIn((CharSetNode)var17.get(var1.getNonterminal()));
                     }

                     @Override
                     public void visitTerminal(TerminalEntity var1) {
                        ((CharSetNode)((List)var18.get(var27)).get(var13)).getCS().add((CharSet)var2.get(var1));
                     }
                  });
                  if (!var22.contains(var32.get(var35))) {
                     break;
                  }
               }
            }
         }
      }

      ((CharSetNode)var16.get(var1.getStart())).getCS().addEOF();
      CharSetNode.fixpoint(var19);
      this.select = new HashMap<>();
      this.follows = new HashMap<>();

      for (String var28 : var1.getNonterminals()) {
         this.select.put(var28, ((CharSetNode)var17.get(var28)).getCS());
         this.follows.put(var28, ((CharSetNode)var16.get(var28)).getCS());
      }
   }

   public Map<String, CharSet> getNonterminalSelect() {
      return this.select;
   }

   public Map<String, CharSet> getNonterminalFollows() {
      return this.follows;
   }
}
