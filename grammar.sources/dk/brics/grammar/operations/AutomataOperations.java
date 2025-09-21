package dk.brics.grammar.operations;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import dk.brics.grammar.ambiguity.HorizontalOverlapString;

public class AutomataOperations {
   private static final Automaton marker_sigmaplus = new RegExp("\u0001\u0000(\u0000.)+").toAutomaton();
   private static final Automaton marker_sigmastar = new RegExp("\u0001\u0000(\u0000.)*").toAutomaton();
   private static final Automaton sigmaplus_marker = new RegExp("(\u0000.)+\u0001\u0000").toAutomaton();
   private static final Automaton sigmastar_marker = new RegExp("(\u0000.)*\u0001\u0000").toAutomaton();

   private AutomataOperations() {
   }

   public static Automaton expandAlphabet(Automaton var0, char var1) {
      return expandAlphabet(var0, false, var1);
   }

   private static Automaton expandAlphabet(Automaton var0, boolean var1, char var2) {
      Automaton var3;
      if (var0.getSingleton() != null && !var1) {
         StringBuilder var9 = new StringBuilder();

         for (char var14 : var0.getSingleton().toCharArray()) {
            var9.append('\u0000').append(var14);
         }

         var3 = Automaton.makeString(var9.toString());
      } else {
         var3 = var0.clone();

         for (State var5 : var3.getStates()) {
            State var6 = new State();

            for (Transition var8 : var5.getTransitions()) {
               var6.addTransition(var8);
            }

            var5.getTransitions().clear();
            var5.addTransition(new Transition(var2, var6));
            if (var1) {
               State var12 = new State();
               var5.addTransition(new Transition('\u0001', var12));
               var12.addTransition(new Transition('\u0000', var5));
            }
         }
      }

      return var3;
   }

   public static Automaton getOverlap(Automaton var0, Automaton var1) {
      Automaton var2 = expandAlphabet(var0, false, '\u0000');
      Automaton var3 = expandAlphabet(var0, true, '\u0000');
      Automaton var4 = expandAlphabet(var1, false, '\u0000');
      Automaton var5 = expandAlphabet(var1, true, '\u0000');
      Automaton var6 = var2.concatenate(marker_sigmaplus).intersection(var3).concatenate(marker_sigmastar);
      Automaton var7 = sigmastar_marker.concatenate(sigmaplus_marker.concatenate(var4).intersection(var5));
      return var6.intersection(var7);
   }

   public static HorizontalOverlapString getOverlapString(Automaton var0) {
      String var1 = var0.getShortestExample(true);
      HorizontalOverlapString var2 = null;
      StringBuilder var3 = new StringBuilder();
      if (var1 != null) {
         int var4;
         for (var4 = 0; var1.charAt(var4 * 2) == 0; var4++) {
            var3.append(var1.charAt(var4 * 2 + 1));
         }

         int var5;
         for (var5 = var4++; var1.charAt(var4 * 2) == 0; var4++) {
            var3.append(var1.charAt(var4 * 2 + 1));
         }

         int var6;
         for (var6 = var4++ - 1; var4 * 2 + 1 < var1.length(); var4++) {
            var3.append(var1.charAt(var4 * 2 + 1));
         }

         var2 = new HorizontalOverlapString(var3.toString(), var5, var6);
      }

      return var2;
   }
}
