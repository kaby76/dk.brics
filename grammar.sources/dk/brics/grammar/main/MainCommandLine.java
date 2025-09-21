package dk.brics.grammar.main;

import dk.brics.misc.Loader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

public class MainCommandLine {
   private MainCommandLine() {
   }

   public static void main(String[] var0) {
      String var2;
      String encoding = var2 = Charset.defaultCharset().name();
      boolean var3 = false;
      boolean var4 = false;
      boolean verbose = false;
      boolean var6 = false;
      String grammar_file_name = null;
      String input_file_name = null;
      int var9 = 0;
      String var10 = "";
      String var11 = "";
      boolean var12 = false;
      boolean var13 = false;

      for (int var14 = 0; var14 < var0.length; var14++) {
         String var15 = var0[var14];
         if (var15.startsWith("-")) {
            if (var15.equals("-d")) {
               var3 = true;
            } else if (var15.equals("-a")) {
               var4 = true;
            } else if (var15.equals("-v")) {
               verbose = true;
            } else if (var15.equals("-z")) {
               var6 = true;
            } else if (var15.equals("-h")) {
               var12 = true;
               var13 = true;
            } else if (var15.equals("-g") && var14 + 1 < var0.length) {
               encoding = var0[++var14];
            } else if (var15.equals("-t") && var14 + 1 < var0.length) {
               var2 = var0[++var14];
            } else if (var15.equals("-u") && var14 + 1 < var0.length) {
               try {
                  var9 = Integer.parseInt(var0[++var14]);
               } catch (NumberFormatException var22) {
                  var12 = true;
               }

               if (var9 < 0) {
                  var12 = true;
               }
            } else if (var15.equals("-l") && var14 + 1 < var0.length) {
               var10 = var0[++var14];
            } else if (var15.equals("-r") && var14 + 1 < var0.length) {
               var11 = var0[++var14];
            } else {
               var12 = true;
            }
         } else if (grammar_file_name == null) {
            grammar_file_name = var15;
         } else if (input_file_name == null) {
            input_file_name = var15;
         } else {
            var12 = true;
         }
      }

      if (grammar_file_name == null && !var13) {
         var12 = true;
         System.out.println("*** error: no grammar specified");
      }

      if (var12) {
         System.out
            .print(
               """
Usage: java dk.brics.grammar.main.Main [Options...] <path or URL of grammar> [ <path or URL of text to parse> ]

This tool checks whether the given text is syntactically correct according to the
given grammar and that the grammar itself is syntactically correct.
It can also check whether the grammar is ambiguous using the technique described in
\"Analyzing Ambiguity of Context-Free Grammars\", Claus Brabrand, Robert Giegerich,
and Anders Møller, CIAA 2007.
If only a grammar is given, only the grammar checks are performed.\n\nOptions:
-a                     analyze the grammar for potential ambiguity
-v                     verbose, print progress information and statistics
-d                     dump AST in Graphviz dot format after parsing
-g <encoding>          character encoding used by the grammar
-t <encoding>          character encoding used by the text
-u <unfold level>      grammar unfolding level (for ambiguity analysis)
-l <left parentheses>  left parentheses symbols for grammar unfolding
-r <right parentheses> right parentheses symbols for grammar unfolding
-z                     tokenize grammar (for ambiguity analysis)

System properties:
-Ddk.brics.grammar.parser.debug            extra output from parser
-Ddk.brics.grammar.ambiguity.debug         extra output from ambiguity analyzer
-Ddk.brics.grammar.ambiguity.noparsecheck  omit parse check in ambiguity analyzer
-Ddk.brics.grammar.ambiguity.ignorables    force enabling ignorables mode in ambiguity analyzer
-Ddk.brics.grammar.ambiguity.strategies=[comma separated list of ApproximationStrategy classes]
"""
            );
         System.exit(-1);
      }

      try {
         String grammar_text = Loader.getString(grammar_file_name, encoding);
         String input_text = input_file_name != null ? Loader.getString(input_file_name, var2) : null;
         int var16 = Main.run(grammar_text, grammar_file_name, input_text, input_file_name, var4, var9, var10, var11, verbose, var3, var6, true, new PrintWriter(System.out, true));
         if (var16 != 0) {
            System.exit(var16);
         }
      } catch (IllegalArgumentException var17) {
         fail(var17);
      } catch (IOException var18) {
         fail(var18);
      } catch (InstantiationException var19) {
         fail(var19);
      } catch (IllegalAccessException var20) {
         fail(var20);
      } catch (ClassNotFoundException var21) {
         fail(var21);
      }
   }

   private static void fail(Exception var0) {
      System.out.println("*** error: " + var0.getMessage());
      System.exit(-1);
   }
}
