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

public class FirstLastFinder {
   private Map<Entity, CharSet> entity_first;
   private Map<Entity, CharSet> entity_last;

   public FirstLastFinder(Grammar var1, boolean var2) {
      final HashMap var3 = new HashMap();
      final HashMap var4 = new HashMap();

      for (Production var6 : var1.getProductions()) {
         for (Entity var8 : var6.getEntities()) {
            CharSet var9 = CharSet.getCharSet(var8, var2, true);
            if (var9 != null) {
               var3.put(var8, var9);
            }

            CharSet var10 = CharSet.getCharSet(var8, var2, false);
            if (var10 != null) {
               var4.put(var8, var10);
            }
         }
      }

      final HashMap var17 = new HashMap();
      final HashMap var18 = new HashMap();
      final HashMap var19 = new HashMap();
      final HashMap var20 = new HashMap();
      HashSet var21 = new HashSet();

      for (String var11 : var1.getNonterminals()) {
         CharSetNode var12 = new CharSetNode();
         var21.add(var12);
         var17.put(var11, var12);
         CharSetNode var13 = new CharSetNode();
         var21.add(var13);
         var18.put(var11, var13);
      }

      for (Production var25 : var1.getProductions()) {
         ArrayList var28 = new ArrayList();
         ArrayList var31 = new ArrayList();
         if (var25.isUnordered()) {
            CharSetNode var35 = new CharSetNode();
            var21.add(var35);
            var28.add(var35);
            CharSetNode var40 = new CharSetNode();
            var21.add(var40);
            var31.add(var40);
         } else {
            for (int var14 = 0; var14 < var25.getEntities().size(); var14++) {
               CharSetNode var15 = new CharSetNode();
               var21.add(var15);
               var28.add(var15);
               CharSetNode var16 = new CharSetNode();
               var21.add(var16);
               var31.add(var16);
            }
         }

         var19.put(var25, var28);
         var20.put(var25, var31);
      }

      Set var24 = new NullableFinder(var1).getNullableEntities();

      for (final Production var29 : var1.getProductions()) {
         if (var29.getEntities().size() > 0) {
            String var32 = var29.getNonterminal();
            ((CharSetNode)var17.get(var32)).addIn((CharSetNode)((List)var19.get(var29)).get(0));
            ((CharSetNode)var18.get(var32)).addIn((CharSetNode)((List)var20.get(var29)).get(((List)var20.get(var29)).size() - 1));
         }

         List var33 = var29.getEntities();
         if (var29.isUnordered()) {
            for (int var38 = 0; var38 < var33.size(); var38++) {
               ((Entity)var33.get(var38)).visitBy(new VoidEntityVisitor() {
                  @Override
                  public void visitNonterminal(NonterminalEntity var1) {
                     ((CharSetNode)((List)var19.get(var29)).get(0)).addIn((CharSetNode)var17.get(var1.getNonterminal()));
                     ((CharSetNode)((List)var20.get(var29)).get(0)).addIn((CharSetNode)var18.get(var1.getNonterminal()));
                  }

                  @Override
                  public void visitTerminal(TerminalEntity var1) {
                     ((CharSetNode)((List)var19.get(var29)).get(0)).getCS().add((CharSet)var3.get(var1));
                     ((CharSetNode)((List)var20.get(var29)).get(0)).getCS().add((CharSet)var4.get(var1));
                  }
               });
            }
         } else {
            for (int var36 = 0; var36 < var33.size(); var36++) {
               final int var41 = var36;

               for (int var44 = var36; var44 < var33.size(); var44++) {
                  ((Entity)var33.get(var44)).visitBy(new VoidEntityVisitor() {
                     @Override
                     public void visitNonterminal(NonterminalEntity var1) {
                        ((CharSetNode)((List)var19.get(var29)).get(var41)).addIn((CharSetNode)var17.get(var1.getNonterminal()));
                     }

                     @Override
                     public void visitTerminal(TerminalEntity var1) {
                        ((CharSetNode)((List)var19.get(var29)).get(var41)).getCS().add((CharSet)var3.get(var1));
                     }
                  });
                  if (!var24.contains(var33.get(var44))) {
                     break;
                  }
               }
            }

            for (int var37 = var33.size() - 1; var37 >= 0; var37--) {
               final int var42 = var37;

               for (int var45 = var37; var45 >= 0; var45--) {
                  ((Entity)var33.get(var45)).visitBy(new VoidEntityVisitor() {
                     @Override
                     public void visitNonterminal(NonterminalEntity var1) {
                        ((CharSetNode)((List)var20.get(var29)).get(var42)).addIn((CharSetNode)var18.get(var1.getNonterminal()));
                     }

                     @Override
                     public void visitTerminal(TerminalEntity var1) {
                        ((CharSetNode)((List)var20.get(var29)).get(var42)).getCS().add((CharSet)var4.get(var1));
                     }
                  });
                  if (!var24.contains(var33.get(var45))) {
                     break;
                  }
               }
            }
         }
      }

      CharSetNode.fixpoint(var21);
      this.entity_first = new HashMap<>();
      this.entity_last = new HashMap<>();

      for (Production var30 : var1.getProductions()) {
         int var34 = 0;

         for (Entity var43 : var30.getEntities()) {
            int var46 = var30.isUnordered() ? 0 : var34++;
            this.entity_first.put(var43, ((CharSetNode)((List)var19.get(var30)).get(var46)).getCS());
            this.entity_last.put(var43, ((CharSetNode)((List)var20.get(var30)).get(var46)).getCS());
         }
      }
   }

   public Map<Entity, CharSet> getEntityFirst() {
      return this.entity_first;
   }

   public Map<Entity, CharSet> getEntityLast() {
      return this.entity_last;
   }
}
