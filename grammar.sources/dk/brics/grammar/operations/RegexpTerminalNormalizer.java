package dk.brics.grammar.operations;

import dk.brics.automaton.Automaton;
import dk.brics.grammar.Entity;
import dk.brics.grammar.Grammar;
import dk.brics.grammar.NonterminalEntity;
import dk.brics.grammar.Production;
import dk.brics.grammar.RegexpTerminalEntity;
import java.util.ArrayList;
import java.util.IdentityHashMap;

public class RegexpTerminalNormalizer {
   public void normalize(Grammar var1) {
      Automaton2Grammar var2 = new Automaton2Grammar();
      IdentityHashMap var3 = new IdentityHashMap();

      for (Production var5 : new ArrayList<>(var1.getProductions())) {
         ArrayList var6 = new ArrayList();

         for (Object var8 : var5.getEntities()) {
            if (var8 instanceof RegexpTerminalEntity) {
               RegexpTerminalEntity var9 = (RegexpTerminalEntity)var8;
               Automaton var10 = var9.getAutomaton();
               String var11 = (String)var3.get(var10);
               if (var11 == null) {
                  var11 = var2.extend(var1, var10);
                  var3.put(var10, var11);
               }

               var8 = new NonterminalEntity(var11, ((Entity)var8).getLabel(), ((Entity)var8).getExample());
            }

            var6.add(var8);
         }

         var5.setEntities(var6);
      }
   }
}
