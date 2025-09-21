package dk.brics.grammar.operations;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import dk.brics.grammar.Grammar;
import dk.brics.grammar.NonterminalEntity;
import dk.brics.grammar.Production;
import dk.brics.grammar.ProductionID;
import dk.brics.grammar.RegexpTerminalEntity;
import dk.brics.string.Misc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Automaton2Grammar {
   private int next_key = 1;

   public Grammar convert(Automaton var1) {
      Grammar var2 = new Grammar(null, new ArrayList<>());
      var2.setStart(this.extend(var2, var1));
      return var2;
   }

   public String extend(Grammar var1, Automaton var2) {
      HashMap var3 = new HashMap();
      Set var4 = var2.getStates();
      System.out.println(var2);

      for (var var6 : var4) {
         String var7 = "@" + this.next_key++;
         var3.put(var6, var7);
      }

      ArrayList var13 = new ArrayList();

      for (var var15 : var4) {
         String var8 = (String)var3.get(var15);
         if (((State)var15).isAccept()) {
            var13.add(new Production(var8, new ArrayList<>(), false, new ProductionID(), 0));
         }

         for (Transition var10 : ((State)var15).getSortedTransitions(false)) {
            ArrayList var11 = new ArrayList();
            String var12 = Misc.escape(Character.toString(var10.getMin()));
            if (var10.getMin() < var10.getMax()) {
               var12 = var12 + "-" + Misc.escape(Character.toString(var10.getMax()));
            }

            var11.add(new RegexpTerminalEntity(Automaton.makeCharRange(var10.getMin(), var10.getMax()), false, var12, null, null));
            var11.add(new NonterminalEntity((String)var3.get(var10.getDest()), null, null));
            var13.add(new Production(var8, var11, false, new ProductionID(), 0));
         }
      }

      var1.addProductions(var13);
      return (String)var3.get(var2.getInitialState());
   }
}
