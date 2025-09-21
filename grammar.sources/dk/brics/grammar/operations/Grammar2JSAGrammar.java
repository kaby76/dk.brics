package dk.brics.grammar.operations;

import dk.brics.automaton.Automaton;
import dk.brics.grammar.EOFTerminalEntity;
import dk.brics.grammar.Entity;
import dk.brics.grammar.EntityVisitor;
import dk.brics.grammar.NonterminalEntity;
import dk.brics.grammar.Production;
import dk.brics.grammar.RegexpTerminalEntity;
import dk.brics.grammar.StringTerminalEntity;
import dk.brics.string.grammar.Grammar;
import dk.brics.string.grammar.Nonterminal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Grammar2JSAGrammar {
   private Map<String, Nonterminal> nt_map;

   public Grammar convert(dk.brics.grammar.Grammar var1) throws IllegalArgumentException {
      Grammar var2 = new Grammar();
      this.nt_map = new HashMap<>();

      for (Production var4 : var1.getProductions()) {
         List var5 = var4.getEntities();
         Nonterminal var6 = this.getNonterminal(var4.getNonterminal(), var2);
         if (var5.size() == 0) {
            var2.addEpsilonProduction(var6);
         } else if (var5.size() == 1) {
            var2.addUnitProduction(var6, this.convertEntity((Entity)var5.get(0), var2));
         } else {
            if (var5.size() != 2) {
               throw new IllegalArgumentException("grammar is not normalized");
            }

            var2.addPairProduction(var6, this.convertEntity((Entity)var5.get(0), var2), this.convertEntity((Entity)var5.get(1), var2));
         }
      }

      return var2;
   }

   private Nonterminal getNonterminal(String var1, Grammar var2) {
      Nonterminal var3 = this.nt_map.get(var1);
      if (var3 == null) {
         var3 = var2.addNonterminal();
      }

      this.nt_map.put(var1, var3);
      return var3;
   }

   private Nonterminal convertEntity(Entity var1, final Grammar var2) {
      return var1.visitBy(new EntityVisitor<Nonterminal>() {
         public Nonterminal visitNonterminalEntity(NonterminalEntity var1) {
            return Grammar2JSAGrammar.this.getNonterminal(var1.getNonterminal(), var2);
         }

         public Nonterminal visitRegexpTerminalEntity(RegexpTerminalEntity var1) {
            Nonterminal var2x = var2.addNonterminal();
            var2.addAutomatonProduction(var2x, var1.getAutomaton());
            return var2x;
         }

         public Nonterminal visitStringTerminalEntity(StringTerminalEntity var1) {
            Nonterminal var2x = var2.addNonterminal();
            var2.addAutomatonProduction(var2x, Automaton.makeString(var1.getString()));
            return var2x;
         }

         public Nonterminal visitEOFTerminalEntity(EOFTerminalEntity var1) {
            Nonterminal var2x = var2.addNonterminal();
            var2.addEpsilonProduction(var2x);
            return var2x;
         }
      });
   }

   public Map<String, Nonterminal> getNonterminalMap() {
      return this.nt_map;
   }
}
