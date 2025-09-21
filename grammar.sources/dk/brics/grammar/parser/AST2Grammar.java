package dk.brics.grammar.parser;

import dk.brics.automaton.Automaton;
import dk.brics.grammar.EOFTerminalEntity;
import dk.brics.grammar.Entity;
import dk.brics.grammar.Grammar;
import dk.brics.grammar.GrammarException;
import dk.brics.grammar.NonterminalEntity;
import dk.brics.grammar.Production;
import dk.brics.grammar.ProductionID;
import dk.brics.grammar.RegexpTerminalEntity;
import dk.brics.grammar.StringTerminalEntity;
import dk.brics.grammar.ast.AST;
import dk.brics.grammar.ast.BranchNode;
import dk.brics.grammar.operations.GrammarChecker;
import dk.brics.misc.Chars;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AST2Grammar {
   private Set<String> unused_regexps;
   private List<AST2Grammar.DeriveCheck> derive_checks;
   private AST ast;

   public Grammar convert(AST ast, PrintWriter var2) throws GrammarException {
      this.ast = ast;
      this.unused_regexps = new HashSet<>();
      this.derive_checks = new ArrayList<>();
      BranchNode ast_root = ast.getRoot();
      HashMap var4 = new HashMap();
      HashSet var5 = new HashSet();
      this.buildRegexps(ast_root, var4, var5);
      List var6 = this.buildProductions(ast_root, var4, var5);
      if (var6.isEmpty()) {
         throw new GrammarException("no productions!");
      } else {
         String var7 = ast_root.getBranchChild("productions").getBranchChild("production").getLeafString("nonterminal", this.ast);
         Grammar var8 = new Grammar(var7, var6);
         if (var2 != null) {
            for (String var10 : this.unused_regexps) {
               var2.println("*** unused regular expression '" + var10 + "'");
            }

            new GrammarChecker().check(var8, var2);
         }

         if (!this.derive_checks.isEmpty()) {
            Parser var15 = new Parser(var8, null);

            for (AST2Grammar.DeriveCheck var11 : this.derive_checks) {
               try {
                  var8.setStart(var11.nt);
                  if (!var15.check(var11.example, null)) {
                     throw new GrammarException("example string '" + Chars.escape(var11.example) + "' does not match nonterminal " + var11.nt);
                  }
               } finally {
                  var8.setStart(var7);
               }
            }
         }

         this.ast = null;
         this.unused_regexps = null;
         this.derive_checks = null;
         return var8;
      }
   }

   private void buildRegexps(BranchNode var1, Map<String, Automaton> var2, Set<String> var3) {
      for (BranchNode var4 = var1.getBranchChild("regexps"); var4.getLabel().equals("nonempty"); var4 = var4.getBranchChild("more")) {
         BranchNode var5 = var4.getBranchChild("regexp");
         String var6 = var5.getLeafString("name", this.ast);
         if (var6.equals("EOF")) {
            throw new GrammarException("regular expression name 'EOF' is predefined!");
         }

         if (var2.containsKey(var6)) {
            throw new GrammarException("multiple definitions of regular expression '" + var6 + "'!");
         }

         Automaton var7 = this.buildAutomaton(var5.getBranchChild("exp"), var2);

         assert var7 != null;

         var2.put(var6, var7);
         if (var5.getBranchChild("max").getLabel().equals("present")) {
            var3.add(var6);
         }

         this.unused_regexps.add(var6);
      }
   }

   private List<Production> buildProductions(BranchNode var1, Map<String, Automaton> var2, Set<String> var3) {
      ArrayList var4 = new ArrayList();
      BranchNode var5 = var1.getBranchChild("productions");

      for (HashMap var6 = new HashMap(); var5.getLabel().equals("nonempty"); var5 = var5.getBranchChild("more")) {
         BranchNode var7 = var5.getBranchChild("production");
         String var8 = var7.getLeafString("nonterminal", this.ast);

         do {
            List var9 = this.buildEntities(var7.getBranchChild("entities"), var2, var3);
            boolean var10 = var7.getBranchChild("unordered").getLabel().equals("present");
            String var11 = null;
            BranchNode var12 = var7.getBranchChild("label");
            if (var12.getLabel().equals("present")) {
               var11 = var12.getLeafString("label", this.ast);
            }

            Integer var13 = (Integer)var6.get(var8);
            if (var13 == null) {
               var13 = 0;
            }

            if (var7.getBranchChild("priority").getLabel().equals("higher")) {
               var13 = var13 - 1;
               var6.put(var8, var13);
            }

            var4.add(new Production(var8, var9, var10, new ProductionID(var11), var13));
            var7 = var7.getBranchChild("more");
            var label = var7.getLabel();
            var myint = label;
         } while (var7.getLabel().equals("nonempty"));
      }

      return var4;
   }

   private List<Entity> buildEntities(BranchNode var1, Map<String, Automaton> var2, Set<String> var3) {
      ArrayList var4 = new ArrayList();

      while (var1.getLabel().equals("nonempty")) {
         BranchNode var5 = var1.getBranchChild("entity");
         Object var6 = null;
         String var7 = var5.getLabel();
         if (var7.equals("nonterminal")) {
            String var8 = var5.getLeafString("nonterminal", this.ast);
            BranchNode var9 = var5.getBranchChild("labelexample");
            String var10 = var9.getLabel();
            if (var10.equals("label")) {
               var6 = new NonterminalEntity(var8, var9.getLeafString("label", this.ast), null);
            } else if (var10.equals("example")) {
               String var11 = this.unescape(var9.getLeafString("example", this.ast));
               var6 = new NonterminalEntity(var8, null, var11);
               this.derive_checks.add(new AST2Grammar.DeriveCheck(var8, var11));
            } else if (var10.equals("absent")) {
               var6 = new NonterminalEntity(var8, null, null);
            }
         } else if (var7.equals("regexp_terminal")) {
            String var14 = var5.getLeafString("regexp", this.ast);
            this.unused_regexps.remove(var14);
            BranchNode var15 = var5.getBranchChild("labelexample");
            String var16 = var15.getLabel();
            if (var14.equals("EOF")) {
               var6 = new EOFTerminalEntity();
               if (var16.equals("example")) {
                  throw new GrammarException("can't set example string for EOF terminal!");
               }

               if (var16.equals("label")) {
                  throw new GrammarException("can't set label for EOF terminal!");
               }
            } else {
               Automaton var17 = (Automaton)var2.get(var14);
               if (var17 == null) {
                  throw new GrammarException("regular expression '" + var14 + "' not defined!");
               }

               boolean var12 = var3.contains(var14);
               if (var16.equals("label")) {
                  var6 = new RegexpTerminalEntity(var17, var12, var14, var15.getLeafString("label", this.ast), null);
               } else if (var16.equals("example")) {
                  String var13 = this.unescape(var15.getLeafString("example", this.ast));
                  var6 = new RegexpTerminalEntity(var17, var12, var14, null, var13);
                  if (var13 != null && !var17.run(var13)) {
                     throw new GrammarException("example string '" + Chars.escape(var13) + "' does not match regexp " + var14);
                  }
               } else if (var16.equals("absent")) {
                  var6 = new RegexpTerminalEntity(var17, var12, var14, null, null);
               }
            }
         } else if (var7.equals("string_terminal")) {
            var6 = new StringTerminalEntity(this.unescape(var5.getLeafString("string", this.ast)));
         }

         var4.add(var6);
         var1 = var1.getBranchChild("more");
      }

      return var4;
   }

   private Automaton buildAutomaton(BranchNode var1, Map<String, Automaton> var2) {
      return this.buildAutomatonUnionExp(var1.getBranchChild("e"), var2);
   }

   private Automaton buildAutomatonUnionExp(BranchNode var1, Map<String, Automaton> var2) {
      Automaton var3 = null;
      String var4 = var1.getLabel();
      if (var4.equals("union")) {
         var3 = this.buildAutomatonInterExp(var1.getBranchChild("e"), var2).union(this.buildAutomatonUnionExp(var1.getBranchChild("more"), var2));
         var3.minimize();
      } else if (var4.equals("other")) {
         var3 = this.buildAutomatonInterExp(var1.getBranchChild("e"), var2);
      }

      assert var3 != null;

      return var3;
   }

   private Automaton buildAutomatonInterExp(BranchNode var1, Map<String, Automaton> var2) {
      Automaton var3 = null;
      String var4 = var1.getLabel();
      if (var4.equals("inter")) {
         var3 = this.buildAutomatonConcatExp(var1.getBranchChild("e"), var2).intersection(this.buildAutomatonInterExp(var1.getBranchChild("more"), var2));
         var3.minimize();
      } else if (var4.equals("other")) {
         var3 = this.buildAutomatonConcatExp(var1.getBranchChild("e"), var2);
      }

      assert var3 != null;

      return var3;
   }

   private Automaton buildAutomatonConcatExp(BranchNode var1, Map<String, Automaton> var2) {
      Automaton var3 = null;
      String var4 = var1.getLabel();
      if (var4.equals("concat")) {
         var3 = this.buildAutomatonRepeatExp(var1.getBranchChild("e"), var2).concatenate(this.buildAutomatonConcatExp(var1.getBranchChild("more"), var2));
         var3.minimize();
      } else if (var4.equals("other")) {
         var3 = this.buildAutomatonRepeatExp(var1.getBranchChild("e"), var2);
      }

      assert var3 != null;

      return var3;
   }

   private Automaton buildAutomatonRepeatExp(BranchNode var1, Map<String, Automaton> var2) {
      Automaton var3 = null;
      String var4 = var1.getLabel();
      if (var4.equals("optional")) {
         var3 = this.buildAutomatonRepeatExp(var1.getBranchChild("e"), var2).optional();
      } else if (var4.equals("star")) {
         var3 = this.buildAutomatonRepeatExp(var1.getBranchChild("e"), var2).repeat();
      } else if (var4.equals("plus")) {
         var3 = this.buildAutomatonRepeatExp(var1.getBranchChild("e"), var2).repeat(1);
      } else if (var4.equals("number")) {
         int var5 = Integer.parseInt(var1.getLeafString("n", this.ast));
         var3 = this.buildAutomatonRepeatExp(var1.getBranchChild("e"), var2).repeat(var5, var5);
      } else if (var4.equals("min")) {
         int var7 = Integer.parseInt(var1.getLeafString("n", this.ast));
         var3 = this.buildAutomatonRepeatExp(var1.getBranchChild("e"), var2).repeat(var7);
      } else if (var4.equals("interval")) {
         int var8 = Integer.parseInt(var1.getLeafString("n", this.ast));
         int var6 = Integer.parseInt(var1.getLeafString("m", this.ast));
         var3 = this.buildAutomatonRepeatExp(var1.getBranchChild("e"), var2).repeat(var8, var6);
      } else if (var4.equals("other")) {
         var3 = this.buildAutomatonComplExp(var1.getBranchChild("e"), var2);
      }

      assert var3 != null;

      var3.minimize();
      return var3;
   }

   private Automaton buildAutomatonComplExp(BranchNode var1, Map<String, Automaton> var2) {
      Automaton var3 = null;
      String var4 = var1.getLabel();
      if (var4.equals("complement")) {
         var3 = this.buildAutomatonComplExp(var1.getBranchChild("e"), var2).complement();
      } else if (var4.equals("other")) {
         var3 = this.buildAutomatonCharclassExp(var1.getBranchChild("e"), var2);
      }

      assert var3 != null;

      return var3;
   }

   private Automaton buildAutomatonCharclassExp(BranchNode var1, Map<String, Automaton> var2) {
      Automaton var3 = null;
      String var4 = var1.getLabel();
      if (var4.equals("charclass")) {
         var3 = this.buildAutomatonCharclasses(var1.getBranchChild("c"));
      } else if (var4.equals("negativeclass")) {
         var3 = this.buildAutomatonCharclasses(var1.getBranchChild("c")).complement().intersection(Automaton.makeAnyChar());
      } else if (var4.equals("other")) {
         var3 = this.buildAutomatonSimpleExp(var1.getBranchChild("e"), var2);
      }

      assert var3 != null;

      return var3;
   }

   private Automaton buildAutomatonCharclasses(BranchNode var1) {
      Automaton var2 = null;
      String var3 = var1.getLabel();
      if (var3.equals("first")) {
         var2 = this.buildAutomatonCharclass(var1.getBranchChild("c")).union(this.buildAutomatonCharclasses(var1.getBranchChild("more")));
      } else if (var3.equals("last")) {
         var2 = this.buildAutomatonCharclass(var1.getBranchChild("c"));
      }

      assert var2 != null;

      return var2;
   }

   private Automaton buildAutomatonCharclass(BranchNode var1) {
      Automaton var2 = null;
      String var3 = var1.getLabel();
      if (var3.equals("interval")) {
         var2 = Automaton.makeCharRange(this.getChar(var1.getBranchChild("c1")), this.getChar(var1.getBranchChild("c2")));
      } else if (var3.equals("single")) {
         var2 = Automaton.makeChar(this.getChar(var1.getBranchChild("c")));
      }

      assert var2 != null;

      return var2;
   }

   private Automaton buildAutomatonSimpleExp(BranchNode var1, Map<String, Automaton> var2) {
      Automaton var3 = null;
      String var4 = var1.getLabel();
      if (var4.equals("char") || var4.equals("escape")) {
         var3 = Automaton.makeChar(this.getChar(var1));
      } else if (var4.equals("dot")) {
         var3 = Automaton.makeAnyChar();
      } else if (var4.equals("empty")) {
         var3 = Automaton.makeEmpty();
      } else if (var4.equals("all")) {
         var3 = Automaton.makeAnyString();
      } else if (var4.equals("string")) {
         var3 = Automaton.makeString(this.unescape(var1.getLeafString("string", this.ast)));
      } else if (var4.equals("epsilon")) {
         var3 = Automaton.makeEmptyString();
      } else if (var4.equals("exp")) {
         var3 = this.buildAutomatonUnionExp(var1.getBranchChild("e"), var2);
      } else if (var4.equals("named")) {
         String var5 = var1.getLeafString("id", this.ast);
         if (var5.equals("EOF")) {
            throw new GrammarException("EOF not allowed in regular expressions!");
         }

         var3 = (Automaton)var2.get(var5);
         if (var3 == null) {
            throw new GrammarException("regular expression '" + var5 + "' not defined!");
         }

         this.unused_regexps.remove(var5);
      } else if (var4.equals("numeric")) {
         int var7 = Integer.parseInt(var1.getLeafString("n", this.ast));
         int var6 = Integer.parseInt(var1.getLeafString("m", this.ast));
         var3 = Automaton.makeInterval(var7, var6, 0);
      }

      assert var3 != null;

      return var3;
   }

   private String unescape(String var1) {
      StringBuilder var2 = new StringBuilder();

      for (int var3 = 0; var3 < var1.length(); var3++) {
         char var4 = var1.charAt(var3);
         if (var4 == '\\') {
            if (var1.charAt(var3 + 1) == 'u') {
               var2.append(this.unescapeChar(var1.substring(var3, var3 + 6)));
               var3 += 5;
            } else {
               var2.append(this.unescapeChar(var1.substring(var3, var3 + 2)));
               var3++;
            }
         } else {
            var2.append(var4);
         }
      }

      return var2.toString();
   }

   private char getChar(BranchNode var1) {
      Character var2 = null;
      String var3 = var1.getLabel();
      if (var3.equals("char")) {
         var2 = var1.getLeafString("c", this.ast).charAt(0);
      } else if (var3.equals("escape")) {
         var2 = this.unescapeChar(var1.getLeafString("c", this.ast));
      }

      assert var2 != null;

      return var2;
   }

   private Character unescapeChar(String var1) {
      char var3 = var1.charAt(1);
      Character var2;
      switch (var3) {
         case 'b':
            var2 = '\b';
            break;
         case 'c':
         case 'd':
         case 'e':
         case 'g':
         case 'h':
         case 'i':
         case 'j':
         case 'k':
         case 'l':
         case 'm':
         case 'o':
         case 'p':
         case 'q':
         case 's':
         default:
            var2 = var3;
            break;
         case 'f':
            var2 = '\f';
            break;
         case 'n':
            var2 = '\n';
            break;
         case 'r':
            var2 = '\r';
            break;
         case 't':
            var2 = '\t';
            break;
         case 'u':
            var2 = (char)Integer.parseInt(var1.substring(2), 16);
      }

      return var2;
   }

   private static class DeriveCheck {
      String nt;
      String example;

      DeriveCheck(String var1, String var2) {
         this.nt = var1;
         this.example = var2;
      }
   }
}
