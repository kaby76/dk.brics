package dk.brics.grammar.main;

import dk.brics.grammar.Grammar;
import dk.brics.grammar.GrammarException;
import dk.brics.grammar.ambiguity.AmbiguityAnalyzer;
import dk.brics.grammar.ast.AST;
import dk.brics.grammar.ast.DotPrinter;
import dk.brics.grammar.operations.GrammarTokenizer;
import dk.brics.grammar.operations.Unfolder;
import dk.brics.grammar.parser.MetaGrammar;
import dk.brics.grammar.parser.ParseException;
import dk.brics.grammar.parser.Parser;
import dk.brics.grammar.parser.String2Grammar;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

public class Main {
   private Main() {
   }

   public static void main(String[] var0) {
      if (var0.length == 0) {
         MainGUI.main(var0);
      } else {
         MainCommandLine.main(var0);
      }
   }

   public static int run(
      String grammar_text,
      String grammar_file_name,
      String input_text,
      String input_file_name,
      boolean var4,
      int var5,
      String var6,
      String var7,
      boolean verbose,
      boolean var9,
      boolean var10,
      boolean var11,
      PrintWriter var12
   ) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException {
      AmbiguityAnalyzer var13 = null;
      Object var14 = null;
      Parser var15 = null;
      long var16 = System.currentTimeMillis();
      long var18 = -1L;
      long var20 = -1L;
               System.setProperty("dk.brics.grammar.parser.debug","true");

      try {
         try {
            var12.println("Grammar " + MetaGrammar.getMetaGrammar().toString());
            if (verbose) {
               var12.println("loading grammar " + grammar_file_name);
            }

            ByteArrayOutputStream var22 = new ByteArrayOutputStream();
            //PrintWriter var23 = new PrintWriter(var22, true);
            var14 = new String2Grammar().convert(grammar_text, grammar_file_name, var12); // var23);
            //var23.close();
            var12.print(var22);
            if (!var4 && input_file_name == null) {
               if (var22.size() == 0) {
                  var12.println("no errors found in the grammar!");
               } else {
                  var12.println("errors found in the grammar!");
               }
            }
            var12.println("Grammar " + var14.toString());
         } catch (ParseException var28) {
            var12.println("*** " + grammar_file_name + ": " + var28.getMessage());
            printStatus(var16, var18, var20, var15, var13, false, var12);
            return -2;
         }

         if (var4) {
            Grammar var32 = (Grammar)var14;
            if (var10) {
               if (verbose) {
                  var12.println("tokenizing grammar");
               }

               new GrammarTokenizer().tokenize((Grammar)var14);
            }

            if (var5 > 0) {
               if (verbose) {
                  var12.println("unfolding grammar");
               }

               var32 = new Unfolder(var12).unfold((Grammar)var14, var5, var6, var7);
            }

            var13 = new AmbiguityAnalyzer(var12, verbose);
            long var35 = System.currentTimeMillis();
            var13.analyze(var32);
            var18 = System.currentTimeMillis() - var35;
         }

         if (input_file_name != null) {
            try {
               var15 = new Parser((Grammar)var14, var12);
               long var33 = System.currentTimeMillis();
               if (verbose) {
                  var12.println("parsing text...");
               }
               
//               System.setProperty("dk.brics.grammar.parser.debug","true");

               AST ast = var15.parse(input_text, input_file_name);
               var20 = System.currentTimeMillis() - var33;
               if (var9) {
                  new DotPrinter(var12).print(ast);
               }
               var12.println("no syntax errors!");
               if (ast != null) {
                  var root = ast.getRoot();
                  ast.getRoot().setMystring(ast.getOriginalString());
                  ast.getRoot().myprint(0, null);
               }
            } catch (ParseException var29) {
               var12.println("*** " + input_file_name + ": " + var29.getMessage());
               var12.println("syntax error found!");
               if (verbose) {
                  var12.println();
                  printStatus(var16, var18, var20, var15, var13, false, var12);
                  return -3;
               }
            }
         }
      } catch (GrammarException var30) {
         var12.println("*** error in grammar: " + var30.getMessage());
         return -4;
      }

      if (!var9 || var4) {
         boolean var34 = false;
         if (var4) {
            int var36 = var13.getNumberOfPotentialVerticalAmbiguities();
            int var37 = var13.getNumberOfPotentialHorizontalAmbiguities();
            int var25 = var13.getNumberOfCertainVerticalAmbiguities();
            int var26 = var13.getNumberOfCertainHorizontalAmbiguities();
            int var27 = var13.getNumberOfOutOfMemoryErrors();
            if (var36 + var37 + var25 + var26 + var27 == 0) {
               var12.println("the grammar is unambiguous!");
            } else {
               if (var25 + var26 > 0) {
                  var12.println("the grammar is ambiguous!");
               } else {
                  var12.println("the grammar might be ambiguous, but I'm not sure...");
               }

               var34 = true;
            }
         }

         if (verbose) {
            var12.println();
            if (var4) {
               var13.printStatistics(var12);
            }

            printStatus(var16, var18, var20, var15, var13, var11, var12);
         }

         if (var34) {
            return -5;
         }
      }

      var12.flush();
      return 0;
   }

   private static void printStatus(long var0, long var2, long var4, Parser var6, AmbiguityAnalyzer var7, boolean var8, PrintWriter var9) {
      if (var7 != null && var8) {
         var9.println("maximal jvm memory usage during ambiguity analysis (without intensive gc): " + var7.getMaxMemory() + " bytes");
      }

      if (var2 != -1L) {
         var9.println("time for analyzing ambiguity: " + var2 + "ms");
      }

      if (var4 != -1L) {
         var9.println("time for parsing input text: " + var4 + "ms");
      }

      var9.println("total time: " + (System.currentTimeMillis() - var0) + "ms");
      if (var8 && var6 != null) {
         var9.println("maximal jvm memory usage during parsing (without intensive gc): " + var6.getMaxMemory() + " bytes");
         var9.println("total number of parse states: " + var6.getTotalStates());
         var9.println("maximal number of pending parse states: " + var6.getMaxStates());
      }
   }
}
