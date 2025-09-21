package dk.brics.grammar.parser;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.grammar.EOFTerminalEntity;
import dk.brics.grammar.Grammar;
import dk.brics.grammar.NonterminalEntity;
import dk.brics.grammar.Production;
import dk.brics.grammar.ProductionID;
import dk.brics.grammar.RegexpTerminalEntity;
import dk.brics.grammar.StringTerminalEntity;
import java.util.ArrayList;

public class MetaGrammar {
   static Grammar meta;

   private MetaGrammar() {
   }

   public static Grammar getMetaGrammar() {
      if (meta == null) {
         Automaton var0 = new RegExp("[a-zA-Z0-9_\\.]+").toAutomaton();
         String var1 = "ID";
         Automaton var2 = new RegExp("[\\ \t\n\r\f]+").toAutomaton();
         String var3 = "WHITESPACE";
         Automaton var4 = new RegExp("\"/*\"~(.*\"*/\".*)\"*/\"|\"//\"[^\n\r]*").toAutomaton();
         String var5 = "COMMENT";
         Automaton var6 = new RegExp("[0-9]+").toAutomaton();
         String var7 = "NUMBER";
         Automaton var8 = new RegExp("[^\\ \\.\\\"\\#\\@\\|\\&\\~\\?\\*\\+\\{\\}\\(\\)\\<\\>\\[\\]\\-\\\\\b\t\n\r\f]").toAutomaton();
         String var9 = "NONRESERVEDCHAR";
         Automaton var10 = new RegExp("\\\\(u[0-9a-fA-F]{4}|[^u\b\t\n\r\f]|[btnrf])").toAutomaton();
         String var11 = "ESCAPEDCHAR";
         Automaton var12 = new RegExp("[^\\^\\]\\\\\\-\b\t\n\r\f]").toAutomaton();
         String var13 = "CHARCLASSCHAR";
         Automaton var14 = new RegExp("([^\"\\\\\b\t\n\r\f]|\\\\(u[0-9a-fA-F]{4}|[^u\b\t\n\r\f]|[btnrf]))*").toAutomaton();
         String var15 = "STRING";
         ArrayList var16 = new ArrayList();
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("_", null, null));
         var17.add(new NonterminalEntity("RegexpDefs", "regexps", null));
         var17.add(new NonterminalEntity("Productions", "productions", null));
         var16.add(new Production("Grammar", var17, false, new ProductionID(), 0));
         }

         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("RegexpDef", "regexp", null));
         var17.add(new NonterminalEntity("__", null, null));
         var17.add(new NonterminalEntity("RegexpDefs", "more", null));
         var16.add(new Production("RegexpDefs", var17, false, new ProductionID("nonempty"), 0));
         }

         {ArrayList var17 = new ArrayList();
         var16.add(new Production("RegexpDefs", var17, false, new ProductionID("empty"), 0));
         }

         {ArrayList var17 = new ArrayList();
         var17.add(new RegexpTerminalEntity(var0, true, var1, "name", null));
         var17.add(new NonterminalEntity("_", null, null));
         var17.add(new StringTerminalEntity("="));
         var17.add(new NonterminalEntity("_", null, null));
         var17.add(new NonterminalEntity("Regexp", "exp", null));
         var17.add(new NonterminalEntity("OptMax", "max", null));
         var16.add(new Production("RegexpDef", var17, false, new ProductionID(), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("ProductionGroup", "production", null));
         var17.add(new NonterminalEntity("Productions", "more", null));
         var16.add(new Production("Productions", var17, false, new ProductionID("nonempty"), 0));
         }

         {ArrayList var17 = new ArrayList();
         var16.add(new Production("Productions", var17, false, new ProductionID("empty"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new RegexpTerminalEntity(var0, true, var1, "nonterminal", null));
         var17.add(new NonterminalEntity("_", null, null));
         var17.add(new NonterminalEntity("OptLabel", "label", null));
         var17.add(new NonterminalEntity("OptPriority", "priority", null));
         var17.add(new StringTerminalEntity(":"));
         var17.add(new NonterminalEntity("OptUnordered", "unordered", null));
         var17.add(new NonterminalEntity("Entities", "entities", null));
         var17.add(new NonterminalEntity("__", null, null));
         var17.add(new NonterminalEntity("MoreProductions", "more", null));
         var16.add(new Production("ProductionGroup", var17, false, new ProductionID(), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("OptLabel", "label", null));
         var17.add(new NonterminalEntity("OptPriority", "priority", null));
         var17.add(new StringTerminalEntity("|"));
         var17.add(new NonterminalEntity("OptUnordered", "unordered", null));
         var17.add(new NonterminalEntity("Entities", "entities", null));
         var17.add(new NonterminalEntity("__", null, null));
         var17.add(new NonterminalEntity("MoreProductions", "more", null));
         var16.add(new Production("MoreProductions", var17, false, new ProductionID("nonempty"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var16.add(new Production("MoreProductions", var17, false, new ProductionID("empty"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new StringTerminalEntity("["));
         var17.add(new NonterminalEntity("_", null, null));
         var17.add(new RegexpTerminalEntity(var0, true, var1, "label", null));
         var17.add(new NonterminalEntity("_", null, null));
         var17.add(new StringTerminalEntity("]"));
         var17.add(new NonterminalEntity("_", null, null));
         var16.add(new Production("OptLabel", var17, false, new ProductionID("present"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var16.add(new Production("OptLabel", var17, false, new ProductionID("absent"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new StringTerminalEntity(">"));
         var17.add(new NonterminalEntity("_", null, null));
         var16.add(new Production("OptPriority", var17, false, new ProductionID("higher"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var16.add(new Production("OptPriority", var17, false, new ProductionID("same"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("_", null, null));
         var17.add(new StringTerminalEntity("&"));
         var16.add(new Production("OptUnordered", var17, false, new ProductionID("present"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var16.add(new Production("OptUnordered", var17, false, new ProductionID("absent"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("_", null, null));
         var17.add(new NonterminalEntity("Entity", "entity", null));
         var17.add(new NonterminalEntity("Entities", "more", null));
         var16.add(new Production("Entities", var17, false, new ProductionID("nonempty"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var16.add(new Production("Entities", var17, false, new ProductionID("empty"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new RegexpTerminalEntity(var0, true, var1, "nonterminal", null));
         var17.add(new NonterminalEntity("OptLabelOrExample", "labelexample", null));
         var16.add(new Production("Entity", var17, false, new ProductionID("nonterminal"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new StringTerminalEntity("<"));
         var17.add(new RegexpTerminalEntity(var0, true, var1, "regexp", null));
         var17.add(new StringTerminalEntity(">"));
         var17.add(new NonterminalEntity("OptLabelOrExample", "labelexample", null));
         var16.add(new Production("Entity", var17, false, new ProductionID("regexp_terminal"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new StringTerminalEntity("\""));
         var17.add(new RegexpTerminalEntity(var14, true, var15, "string", null));
         var17.add(new StringTerminalEntity("\""));
         var16.add(new Production("Entity", var17, false, new ProductionID("string_terminal"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new StringTerminalEntity("["));
         var17.add(new NonterminalEntity("_", null, null));
         var17.add(new RegexpTerminalEntity(var0, true, var1, "label", null));
         var17.add(new NonterminalEntity("_", null, null));
         var17.add(new StringTerminalEntity("]"));
         var16.add(new Production("OptLabelOrExample", var17, false, new ProductionID("label"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new StringTerminalEntity("["));
         var17.add(new NonterminalEntity("_", null, null));
         var17.add(new StringTerminalEntity("\""));
         var17.add(new RegexpTerminalEntity(var14, true, var15, "example", null));
         var17.add(new StringTerminalEntity("\""));
         var17.add(new NonterminalEntity("_", null, null));
         var17.add(new StringTerminalEntity("]"));
         var16.add(new Production("OptLabelOrExample", var17, false, new ProductionID("example"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var16.add(new Production("OptLabelOrExample", var17, false, new ProductionID("absent"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("UnionExp", "e", null));
         var16.add(new Production("Regexp", var17, false, new ProductionID(), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("InterExp", "e", null));
         var17.add(new StringTerminalEntity("|"));
         var17.add(new NonterminalEntity("UnionExp", "more", null));
         var16.add(new Production("UnionExp", var17, false, new ProductionID("union"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("InterExp", "e", null));
         var16.add(new Production("UnionExp", var17, false, new ProductionID("other"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("ConcatExp", "e", null));
         var17.add(new StringTerminalEntity("&"));
         var17.add(new NonterminalEntity("InterExp", "more", null));
         var16.add(new Production("InterExp", var17, false, new ProductionID("inter"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("ConcatExp", "e", null));
         var16.add(new Production("InterExp", var17, false, new ProductionID("other"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("RepeatExp", "e", null));
         var17.add(new NonterminalEntity("ConcatExp", "more", null));
         var16.add(new Production("ConcatExp", var17, false, new ProductionID("concat"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("RepeatExp", "e", null));
         var16.add(new Production("ConcatExp", var17, false, new ProductionID("other"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("RepeatExp", "e", null));
         var17.add(new StringTerminalEntity("?"));
         var16.add(new Production("RepeatExp", var17, false, new ProductionID("optional"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("RepeatExp", "e", null));
         var17.add(new StringTerminalEntity("*"));
         var16.add(new Production("RepeatExp", var17, false, new ProductionID("star"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("RepeatExp", "e", null));
         var17.add(new StringTerminalEntity("+"));
         var16.add(new Production("RepeatExp", var17, false, new ProductionID("plus"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("RepeatExp", "e", null));
         var17.add(new StringTerminalEntity("{"));
         var17.add(new RegexpTerminalEntity(var6, true, var7, "n", null));
         var17.add(new StringTerminalEntity("}"));
         var16.add(new Production("RepeatExp", var17, false, new ProductionID("number"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("RepeatExp", "e", null));
         var17.add(new StringTerminalEntity("{"));
         var17.add(new RegexpTerminalEntity(var6, true, var7, "n", null));
         var17.add(new StringTerminalEntity(",}"));
         var16.add(new Production("RepeatExp", var17, false, new ProductionID("min"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("RepeatExp", "e", null));
         var17.add(new StringTerminalEntity("{"));
         var17.add(new RegexpTerminalEntity(var6, true, var7, "n", null));
         var17.add(new StringTerminalEntity(","));
         var17.add(new RegexpTerminalEntity(var6, true, var7, "m", null));
         var17.add(new StringTerminalEntity("}"));
         var16.add(new Production("RepeatExp", var17, false, new ProductionID("interval"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("ComplExp", "e", null));
         var16.add(new Production("RepeatExp", var17, false, new ProductionID("other"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new StringTerminalEntity("~"));
         var17.add(new NonterminalEntity("ComplExp", "e", null));
         var16.add(new Production("ComplExp", var17, false, new ProductionID("complement"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("CharclassExp", "e", null));
         var16.add(new Production("ComplExp", var17, false, new ProductionID("other"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new StringTerminalEntity("["));
         var17.add(new NonterminalEntity("Charclasses", "c", null));
         var17.add(new StringTerminalEntity("]"));
         var16.add(new Production("CharclassExp", var17, false, new ProductionID("charclass"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new StringTerminalEntity("[^"));
         var17.add(new NonterminalEntity("Charclasses", "c", null));
         var17.add(new StringTerminalEntity("]"));
         var16.add(new Production("CharclassExp", var17, false, new ProductionID("negativeclass"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("SimpleExp", "e", null));
         var16.add(new Production("CharclassExp", var17, false, new ProductionID("other"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("Charclass", "c", null));
         var17.add(new NonterminalEntity("Charclasses", "more", null));
         var16.add(new Production("Charclasses", var17, false, new ProductionID("first"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("Charclass", "c", null));
         var16.add(new Production("Charclasses", var17, false, new ProductionID("last"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("Charclasschar", "c1", null));
         var17.add(new StringTerminalEntity("-"));
         var17.add(new NonterminalEntity("Charclasschar", "c2", null));
         var16.add(new Production("Charclass", var17, false, new ProductionID("interval"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("Charclasschar", "c", null));
         var16.add(new Production("Charclass", var17, false, new ProductionID("single"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new RegexpTerminalEntity(var12, false, var13, "c", null));
         var16.add(new Production("Charclasschar", var17, false, new ProductionID("char"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new RegexpTerminalEntity(var10, false, var11, "c", null));
         var16.add(new Production("Charclasschar", var17, false, new ProductionID("escape"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new RegexpTerminalEntity(var8, false, var9, "c", null));
         var16.add(new Production("SimpleExp", var17, false, new ProductionID("char"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new RegexpTerminalEntity(var10, false, var11, "c", null));
         var16.add(new Production("SimpleExp", var17, false, new ProductionID("escape"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new StringTerminalEntity("."));
         var16.add(new Production("SimpleExp", var17, false, new ProductionID("dot"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new StringTerminalEntity("#"));
         var16.add(new Production("SimpleExp", var17, false, new ProductionID("empty"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new StringTerminalEntity("@"));
         var16.add(new Production("SimpleExp", var17, false, new ProductionID("all"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new StringTerminalEntity("\""));
         var17.add(new RegexpTerminalEntity(var14, true, var15, "string", null));
         var17.add(new StringTerminalEntity("\""));
         var16.add(new Production("SimpleExp", var17, false, new ProductionID("string"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new StringTerminalEntity("()"));
         var16.add(new Production("SimpleExp", var17, false, new ProductionID("epsilon"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new StringTerminalEntity("("));
         var17.add(new NonterminalEntity("UnionExp", "e", null));
         var17.add(new StringTerminalEntity(")"));
         var16.add(new Production("SimpleExp", var17, false, new ProductionID("exp"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new StringTerminalEntity("<"));
         var17.add(new RegexpTerminalEntity(var0, true, var1, "id", null));
         var17.add(new StringTerminalEntity(">"));
         var16.add(new Production("SimpleExp", var17, false, new ProductionID("named"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new StringTerminalEntity("<"));
         var17.add(new RegexpTerminalEntity(var6, true, var7, "n", null));
         var17.add(new StringTerminalEntity("-"));
         var17.add(new RegexpTerminalEntity(var6, true, var7, "m", null));
         var17.add(new StringTerminalEntity(">"));
         var16.add(new Production("SimpleExp", var17, false, new ProductionID("numeric"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("__", null, null));
         var17.add(new StringTerminalEntity("("));
         var17.add(new StringTerminalEntity("MAX"));
         var17.add(new StringTerminalEntity(")"));
         var16.add(new Production("OptMax", var17, false, new ProductionID("present"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var16.add(new Production("OptMax", var17, false, new ProductionID("absent"), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("Ignorable", null, null));
         var17.add(new NonterminalEntity("_", null, null));
         var16.add(new Production("_", var17, false, new ProductionID(), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var16.add(new Production("_", var17, false, new ProductionID(), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new NonterminalEntity("Ignorable", null, null));
         var17.add(new NonterminalEntity("_", null, null));
         var16.add(new Production("__", var17, false, new ProductionID(), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new RegexpTerminalEntity(var2, true, var3, null, null));
         var16.add(new Production("Ignorable", var17, false, new ProductionID(), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new RegexpTerminalEntity(var4, true, var5, null, null));
         var16.add(new Production("Ignorable", var17, false, new ProductionID(), 0));
         }
         
         {ArrayList var17 = new ArrayList();
         var17.add(new EOFTerminalEntity());
         var16.add(new Production("Ignorable", var17, false, new ProductionID(), 0));
         }
         
         meta = new Grammar("Grammar", var16);
      }

      return meta;
   }
}
