package dk.brics.grammar.parser;

import dk.brics.grammar.Grammar;
import dk.brics.grammar.GrammarException;
import java.io.PrintWriter;

public class String2Grammar {
   public Grammar convert(String var1, String var2, PrintWriter var3) throws ParseException, GrammarException {
      var p = new Parser(MetaGrammar.getMetaGrammar(), var3);
      var ast = p.parse(var1, var2);
      if (ast != null) var3.println(ast.toString());
      return new AST2Grammar().convert(ast, var3);
   }
}
