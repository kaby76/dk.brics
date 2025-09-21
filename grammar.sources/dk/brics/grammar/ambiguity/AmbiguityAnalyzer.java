package dk.brics.grammar.ambiguity;

import dk.brics.grammar.EOFTerminalEntity;
import dk.brics.grammar.Entity;
import dk.brics.grammar.EntityVisitor;
import dk.brics.grammar.Grammar;
import dk.brics.grammar.NonterminalEntity;
import dk.brics.grammar.Production;
import dk.brics.grammar.RegexpTerminalEntity;
import dk.brics.grammar.StringTerminalEntity;
import dk.brics.grammar.parser.Parser;
import dk.brics.misc.Chars;
import dk.brics.misc.Properties;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class AmbiguityAnalyzer {
   private Grammar g;
   private PrintWriter out;
   private boolean verbose;
   private boolean debug;
   private boolean noparsecheck;
   private boolean ignorable_entities;
   private boolean ignorable_productions;
   private List<ApproximationStrategy> approx;
   private Parser parser;
   private long max_memory;
   private int potential_horizontal_ambiguities;
   private int potential_vertical_ambiguities;
   private int certain_horizontal_ambiguities;
   private int certain_vertical_ambiguities;
   private int outofmemory;

   public AmbiguityAnalyzer(PrintWriter var1, boolean var2) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
      this.verbose = var2;
      ArrayList var3 = new ArrayList();
      String[] var4 = Properties.getStrings("dk.brics.grammar.ambiguity.strategies");
      if (var4 != null) {
         ClassLoader var5 = this.getClass().getClassLoader();

         for (String var9 : var4) {
            var3.add((ApproximationStrategy)var5.loadClass(var9).newInstance());
         }
      } else {
         ApproximationStrategy[] var10 = new ApproximationStrategy[]{new TerminalApproximation(), new RegularApproximation2(), new RegularApproximation()};

         for (ApproximationStrategy var14 : var10) {
            var3.add(var14);
         }
      }

      this.init(var3, var1);
   }

   public AmbiguityAnalyzer(List<ApproximationStrategy> var1, PrintWriter var2) {
      this.init(var1, var2);
   }

   private void init(List<ApproximationStrategy> var1, PrintWriter var2) {
      this.approx = var1;
      this.out = var2;
      this.debug = Properties.get("dk.brics.grammar.ambiguity.debug");
      this.noparsecheck = Properties.get("dk.brics.grammar.ambiguity.noparsecheck");
      if (this.debug) {
         if (this.noparsecheck) {
            var2.println("parse check disabled");
         }

         var2.print("approximation strategies:");

         for (ApproximationStrategy var4 : var1) {
            var2.print(" " + var4.getName());
         }

         var2.println();
      }
   }

   public boolean analyze(Grammar var1) {
      this.g = var1;
      this.potential_horizontal_ambiguities = this.potential_vertical_ambiguities = this.certain_horizontal_ambiguities = this.certain_vertical_ambiguities = 0;
      this.outofmemory = 0;
      this.parser = new Parser(var1, this.out);
      if (this.debug) {
         this.out.print("grammar:\n" + var1.toString());
      }

      if (Properties.get("dk.brics.grammar.ambiguity.ignorables")) {
         this.ignorable_entities = this.ignorable_productions = true;
      } else {
         this.ignorable_entities = this.ignorable_productions = false;

         for (Production var3 : var1.getProductions()) {
            if (var3.getID().hasExplicitLabel()) {
               this.ignorable_productions = true;
            }

            if (!this.ignorable_entities) {
               for (Entity var5 : var3.getEntities()) {
                  if (var5.isExplicitlyLabeled()) {
                     this.ignorable_entities = true;
                     break;
                  }
               }
            }
         }
      }

      if (this.debug) {
         this.out
            .println(
               "ignorable entities "
                  + (this.ignorable_entities ? "enabled" : "disabled")
                  + ", ignorable productions "
                  + (this.ignorable_productions ? "enabled" : "disabled")
            );
      }

      for (ApproximationStrategy var7 : this.approx) {
         if (this.debug) {
            this.out.println("initializing: " + var7.getName());
         }

         var7.init(var1, this.out, this.debug);
      }

      if (this.debug && var1.isUnfolded()) {
         this.out.println("grammar has been unfolded");
      }

      if (this.debug || this.verbose) {
         this.out.println("checking vertical ambiguities...");
      }

      this.analyzeVertical();
      if (this.debug || this.verbose) {
         this.out.println("checking horizontal ambiguities...");
      }

      this.analyzeHorizontal();
      return this.certain_vertical_ambiguities
            + this.potential_vertical_ambiguities
            + this.certain_horizontal_ambiguities
            + this.potential_horizontal_ambiguities
            + this.outofmemory
         == 0;
   }

   public void printStatistics(PrintWriter var1) {
      var1.println(
         "ambiguities detected: "
            + this.certain_vertical_ambiguities
            + " certain + "
            + this.potential_vertical_ambiguities
            + " potential vertical; "
            + this.certain_horizontal_ambiguities
            + " certain + "
            + this.potential_horizontal_ambiguities
            + " potential horizontal"
      );
      if (this.outofmemory > 0) {
         var1.println("ambiguity checks with resources exhausted: " + this.outofmemory);
      }

      for (ApproximationStrategy var3 : this.approx) {
         var3.printStatistics(var1);
      }
   }

   private void analyzeHorizontal() {
      for (Production var2 : this.g.getProductions()) {
         if (!var2.isUnfolded()) {
            int var3 = var2.getEntities().size();

            for (int var4 = 1; var4 < var3; var4++) {
               try {
                  if (!this.ignorable_entities || var2.getEntities().get(var4 - 1).isLabeled() || var2.getEntities().get(var4).isLabeled()) {
                     if (this.debug || this.verbose) {
                        this.out.println(" horizontal check: " + var2.getNonterminal() + "[" + var2.getID().getLabel() + "] at index " + var4);
                     }

                     HorizontalOverlapString var5 = ApproximationStrategy.HORIZONTAL_NOT_APPLICABLE;
                     boolean var6 = false;
                     boolean var7 = true;
                     boolean var8 = false;

                     for (ApproximationStrategy var10 : this.approx) {
                        HorizontalOverlapString var11 = var10.horizontalCheck(var2, var4);
                        if (var11 != ApproximationStrategy.HORIZONTAL_NOT_APPLICABLE) {
                           if (this.debug) {
                              this.out
                                 .println(
                                    "  result from "
                                       + var10.getName()
                                       + ": "
                                       + (var11 == null ? "no overlap" : "\"" + Chars.escape(this.decode(var11.getString())) + "\"")
                                 );
                           }

                           if (var11 == null) {
                              var5 = null;
                              break;
                           }

                           boolean var12 = false;
                           boolean var13 = false;
                           if (!this.noparsecheck) {
                              var12 = this.parseCheckHorizontal(var2, var4, var11);
                              var13 = true;
                           }

                           if (var7 || var12) {
                              var5 = var11;
                              var6 = var12;
                              var8 = var13;
                              var7 = false;
                           }
                        }
                     }

                     if (var5 != null) {
                        if (!this.noparsecheck && !var8) {
                           var6 = this.parseCheckHorizontal(var2, var4, var5);
                        }

                        this.out.println("*** " + (var6 ? "" : "potential ") + "horizontal ambiguity: " + this.describeHorizontal(var2, var4));
                        if (var6) {
                           this.out.println("    ambiguous string: \"" + Chars.escape(this.decode(var5.getString())) + "\"");
                           this.out
                              .println(
                                 "    matched as \""
                                    + Chars.escape(this.decode(var5.getX()))
                                    + "\" <--> \""
                                    + Chars.escape(this.decode(var5.getAY()))
                                    + "\" or \""
                                    + Chars.escape(this.decode(var5.getXA()))
                                    + "\" <--> \""
                                    + Chars.escape(this.decode(var5.getY()))
                                    + "\""
                              );
                           this.certain_horizontal_ambiguities++;
                        } else {
                           this.potential_horizontal_ambiguities++;
                        }
                     }

                     this.updateMaxMemory();
                  }
               } catch (OutOfMemoryError var14) {
                  this.updateMaxMemory();
                  if (this.debug) {
                     this.out.println("  out of memory");
                  }

                  this.out.println("*** resources exhausted, aborting horizontal ambiguity check: " + this.describeHorizontal(var2, var4));
                  this.outofmemory++;
               }
            }

            for (ApproximationStrategy var16 : this.approx) {
               var16.horizontalDone();
            }
         }
      }
   }

   private String describeHorizontal(Production var1, int var2) {
      return var1.getNonterminal()
         + "["
         + var1.getID().getLabel()
         + "]:"
         + this.printEntities(var1, 0, var2)
         + " <-->"
         + this.printEntities(var1, var2, var1.getEntities().size());
   }

   private void analyzeVertical() {
      for (String var2 : this.g.getNonterminals()) {
         ArrayList var3 = new ArrayList<>(this.g.getProductions(var2));

         for (int var4 = 0; var4 + 1 < var3.size(); var4++) {
            Production var5 = (Production)var3.get(var4);
            if (!var5.isUnfolded()) {
               for (int var6 = var4 + 1; var6 < var3.size(); var6++) {
                  Production var7 = (Production)var3.get(var6);
                  if (!var7.isUnfolded()
                     && var5.getPriority() == var7.getPriority()
                     && (!this.ignorable_productions || var5.getID().hasExplicitLabel() || var7.getID().hasExplicitLabel())) {
                     try {
                        if (this.debug || this.verbose) {
                           this.out
                              .println(
                                 " vertical check: "
                                    + var5.getNonterminal()
                                    + "["
                                    + var5.getID().getLabel()
                                    + "] vs. "
                                    + var7.getNonterminal()
                                    + "["
                                    + var7.getID().getLabel()
                                    + "]"
                              );
                        }

                        VerticalOverlapString var8 = ApproximationStrategy.VERTICAL_NOT_APPLICABLE;
                        boolean var9 = false;
                        boolean var10 = true;
                        boolean var11 = false;

                        for (ApproximationStrategy var13 : this.approx) {
                           VerticalOverlapString var14 = var13.verticalCheck(var5, var7);
                           if (var14 != ApproximationStrategy.VERTICAL_NOT_APPLICABLE) {
                              if (this.debug) {
                                 this.out
                                    .println(
                                       "  result from "
                                          + var13.getName()
                                          + ": "
                                          + (var14 == null ? "no overlap" : "\"" + Chars.escape(this.decode(var14.getString())) + "\"")
                                    );
                              }

                              if (var14 == null) {
                                 var8 = null;
                                 break;
                              }

                              boolean var15 = false;
                              boolean var16 = false;
                              if (!this.noparsecheck) {
                                 var15 = this.parseCheckVertical(var5, var7, var14);
                                 var16 = true;
                              }

                              if (var10 || var15) {
                                 var8 = var14;
                                 var9 = var15;
                                 var11 = var16;
                                 var10 = false;
                              }
                           }
                        }

                        if (var8 != null) {
                           if (!this.noparsecheck && !var11) {
                              var9 = this.parseCheckVertical(var5, var7, var8);
                           }

                           this.out.println("*** " + (var9 ? "" : "potential ") + "vertical ambiguity: " + this.describeVertical(var2, var5, var7));
                           if (var9) {
                              this.out.println("    ambiguous string: \"" + Chars.escape(this.decode(var8.getString())) + "\"");
                              this.certain_vertical_ambiguities++;
                           } else {
                              this.potential_vertical_ambiguities++;
                           }
                        }

                        this.updateMaxMemory();
                     } catch (OutOfMemoryError var17) {
                        this.updateMaxMemory();
                        if (this.debug) {
                           this.out.println("  out of memory");
                        }

                        this.out.println("*** resources exhausted, aborting vertical ambiguity check: " + this.describeVertical(var2, var5, var7));
                        this.outofmemory++;
                     }
                  }
               }
            }
         }

         for (ApproximationStrategy var19 : this.approx) {
            var19.verticalDone();
         }
      }
   }

   private String describeVertical(String var1, Production var2, Production var3) {
      return var1 + "[" + var2.getID().getLabel() + "] <--> " + var1 + "[" + var3.getID().getLabel() + "]";
   }

   private boolean parseCheckHorizontal(Production var1, int var2, HorizontalOverlapString var3) {
      Production var4 = new Production("$", var1.getEntities().subList(0, var2), false, var1.getID(), 0);
      Production var5 = new Production("$", var1.getEntities().subList(var2, var1.getEntities().size()), false, var1.getID(), 0);
      boolean var6 = this.parser.check(var3.getX(), var4)
         && this.parser.check(var3.getXA(), var4)
         && this.parser.check(var3.getY(), var5)
         && this.parser.check(var3.getAY(), var5);
      if (this.debug) {
         this.out.println("  parse check: " + (var6 ? "passed" : "failed"));
      }

      return var6;
   }

   private boolean parseCheckVertical(Production var1, Production var2, VerticalOverlapString var3) {
      boolean var4 = this.parser.check(var3.getString(), var1) && this.parser.check(var3.getString(), var2);
      if (this.debug) {
         this.out.println("  parse check: " + (var4 ? "passed" : "failed"));
      }

      return var4;
   }

   private String printEntities(Production var1, int var2, int var3) {
      StringBuffer var4 = new StringBuffer();
      List var5 = var1.getEntities();

      for (int var6 = var2; var6 < var3; var6++) {
         Entity var7 = (Entity)var5.get(var6);
         var4.append(' ').append(var7.visitBy(new EntityVisitor<String>() {
            public String visitNonterminalEntity(NonterminalEntity var1) {
               String var2 = var1.getNonterminal();
               int var3 = var2.lastIndexOf(35);
               return AmbiguityAnalyzer.this.g.isUnfolded() && var3 >= 0 ? var2.substring(0, var3) : var2;
            }

            public String visitRegexpTerminalEntity(RegexpTerminalEntity var1) {
               return var1.toString();
            }

            public String visitStringTerminalEntity(StringTerminalEntity var1) {
               return "\"" + Chars.escape(AmbiguityAnalyzer.this.decode(var1.getString())) + "\"";
            }

            public String visitEOFTerminalEntity(EOFTerminalEntity var1) {
               return var1.toString();
            }
         }));
         if (var7.isExplicitlyLabeled()) {
            var4.append('[').append(var7.getLabel()).append(']');
         }
      }

      return var4.toString();
   }

   private String decode(String var1) {
      if (!this.g.isUnfolded()) {
         return var1;
      } else {
         StringBuilder var2 = new StringBuilder();

         for (byte var3 = 0; var3 < var1.length(); var3 += 2) {
            var2.append(var1.charAt(var3 + 1));
         }

         return var2.toString();
      }
   }

   private void updateMaxMemory() {
      Runtime var1 = Runtime.getRuntime();
      long var2 = var1.totalMemory() - var1.freeMemory();
      if (var2 > this.max_memory) {
         this.max_memory = var2;
      }
   }

   public long getMaxMemory() {
      return this.max_memory;
   }

   public int getNumberOfPotentialHorizontalAmbiguities() {
      return this.potential_horizontal_ambiguities;
   }

   public int getNumberOfPotentialVerticalAmbiguities() {
      return this.potential_vertical_ambiguities;
   }

   public int getNumberOfCertainHorizontalAmbiguities() {
      return this.certain_horizontal_ambiguities;
   }

   public int getNumberOfCertainVerticalAmbiguities() {
      return this.certain_vertical_ambiguities;
   }

   public int getNumberOfOutOfMemoryErrors() {
      return this.outofmemory;
   }
}
