package dk.brics.grammar.operations;

import dk.brics.grammar.EOFTerminalEntity;
import dk.brics.grammar.Entity;
import dk.brics.grammar.EntityVisitor;
import dk.brics.grammar.Grammar;
import dk.brics.grammar.GrammarException;
import dk.brics.grammar.NonterminalEntity;
import dk.brics.grammar.Production;
import dk.brics.grammar.RegexpTerminalEntity;
import dk.brics.grammar.StringTerminalEntity;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExampleStringDeriver {
   private Grammar g;
   private Map<Entity, String> examples;
   private Set<Entity> example_contains_eof;

   public ExampleStringDeriver(Grammar var1) {
      this.g = var1;
      this.examples = new HashMap<>();
      this.example_contains_eof = new HashSet<>();
   }

   public String getExample(Entity var1) {
      return this.getExample(var1, new HashSet<>());
   }

   private String getExample(final Entity var1, final Set<String> var2) {
      String var3 = this.examples.get(var1);
      if (var3 == null) {
         var3 = var1.visitBy(new EntityVisitor<String>() {
            public String visitNonterminalEntity(NonterminalEntity var1x) {
               Production[] var2x = null;
               String var3 = var1x.getExample();
               if (var3 == null) {
                  Collection var4 = ExampleStringDeriver.this.g.getProductions(var1x.getNonterminal());
                  if (var4 == null) {
                     throw new GrammarException("nonterminal " + var1x.getNonterminal() + " has no productions!");
                  }

                  var2x = (Production[])var4.toArray(new Production[0]);
                  Arrays.sort(var2x, new Comparator<Production>() {
                     public int compare(Production var1x, Production var2x) {
                        return var2x.getPriority() - var1x.getPriority();
                     }
                  });

                  label121:
                  for (Production var8 : var2x) {
                     StringBuilder var9 = new StringBuilder();

                     for (Entity var11 : var8.getEntities()) {
                        if (var11 instanceof NonterminalEntity) {
                           continue label121;
                        }

                        var9.append(ExampleStringDeriver.this.getExample(var11, var2));
                        if (var11 instanceof EOFTerminalEntity || ExampleStringDeriver.this.example_contains_eof.contains(var11)) {
                           continue label121;
                        }
                     }

                     var3 = var9.toString();
                     break;
                  }
               }

               if (var3 == null) {
                  label103:
                  for (Production var19 : var2x) {
                     StringBuilder var21 = new StringBuilder();

                     for (Entity var25 : var19.getEntities()) {
                        String var27 = null;
                        if (var25 instanceof NonterminalEntity) {
                           var27 = ((NonterminalEntity)var25).getNonterminal();
                           if (var2.contains(var27)) {
                              continue label103;
                           }

                           var2.add(var27);
                        }

                        var21.append(ExampleStringDeriver.this.getExample(var25, var2));
                        if (var27 != null) {
                           var2.remove(var27);
                        }

                        if (var25 instanceof EOFTerminalEntity || ExampleStringDeriver.this.example_contains_eof.contains(var25)) {
                           continue label103;
                        }
                     }

                     var3 = var21.toString();
                     break;
                  }
               }

               if (var3 == null) {
                  label82:
                  for (Production var20 : var2x) {
                     boolean var22 = false;
                     StringBuilder var24 = new StringBuilder();

                     for (Entity var28 : var20.getEntities()) {
                        String var12 = null;
                        if (var28 instanceof NonterminalEntity) {
                           var12 = ((NonterminalEntity)var28).getNonterminal();
                           if (var2.contains(var12)) {
                              continue label82;
                           }

                           var2.add(var12);
                        }

                        var24.append(ExampleStringDeriver.this.getExample(var28, var2));
                        if (var12 != null) {
                           var2.remove(var12);
                        }

                        if (var28 instanceof EOFTerminalEntity || ExampleStringDeriver.this.example_contains_eof.contains(var28)) {
                           var22 = true;
                        }
                     }

                     var3 = var24.toString();
                     if (var22) {
                        ExampleStringDeriver.this.example_contains_eof.add(var1);
                     }
                     break;
                  }
               }

               if (var3 == null) {
                  throw new GrammarException("nonterminal " + var1x.getNonterminal() + " is not productive!");
               } else {
                  return var3;
               }
            }

            public String visitRegexpTerminalEntity(RegexpTerminalEntity var1x) {
               String var2x = var1x.getExample();
               if (var2x == null) {
                  var2x = var1x.getAutomaton().getShortestExample(true);
                  if (var2x == null) {
                     throw new GrammarException("regular expression has empty language!");
                  }

                  String var3 = var2x.replace('\t', ' ');
                  if (var1x.getAutomaton().run(var3)) {
                     var2x = var3;
                  }
               }

               return var2x;
            }

            public String visitStringTerminalEntity(StringTerminalEntity var1x) {
               return var1x.getString();
            }

            public String visitEOFTerminalEntity(EOFTerminalEntity var1x) {
               return "";
            }
         });
         this.examples.put(var1, var3);
      }

      return var3;
   }
}
