package dk.brics.grammar.operations;

import dk.brics.grammar.EOFTerminalEntity;
import dk.brics.grammar.Entity;
import dk.brics.grammar.EntityVisitor;
import dk.brics.grammar.Grammar;
import dk.brics.grammar.NonterminalEntity;
import dk.brics.grammar.Production;
import dk.brics.grammar.RegexpTerminalEntity;
import dk.brics.grammar.StringTerminalEntity;
import java.util.ArrayList;

public class GrammarTokenizer {
   public void tokenize(Grammar var1) {
      for (Production var3 : var1.getProductions()) {
         ArrayList var4 = new ArrayList();

         for (Entity var6 : var3.getEntities()) {
            var4.add(var6.visitBy(new EntityVisitor<Entity>() {
               public Entity visitNonterminalEntity(NonterminalEntity var1) {
                  return var1;
               }

               public Entity visitRegexpTerminalEntity(RegexpTerminalEntity var1) {
                  return new StringTerminalEntity("$" + var1.getAutomatonName());
               }

               public Entity visitStringTerminalEntity(StringTerminalEntity var1) {
                  return var1.getString().startsWith("$") ? new StringTerminalEntity("@" + var1.getString()) : var1;
               }

               public Entity visitEOFTerminalEntity(EOFTerminalEntity var1) {
                  return var1;
               }
            }));
         }

         var3.setEntities(var4);
      }
   }
}
