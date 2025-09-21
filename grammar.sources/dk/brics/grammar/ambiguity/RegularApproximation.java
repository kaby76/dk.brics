package dk.brics.grammar.ambiguity;

import dk.brics.automaton.Automaton;
import dk.brics.grammar.EOFTerminalEntity;
import dk.brics.grammar.Entity;
import dk.brics.grammar.EntityVisitor;
import dk.brics.grammar.Grammar;
import dk.brics.grammar.NonterminalEntity;
import dk.brics.grammar.Production;
import dk.brics.grammar.RegexpTerminalEntity;
import dk.brics.grammar.StringTerminalEntity;
import dk.brics.grammar.operations.AutomataOperations;
import dk.brics.grammar.operations.Grammar2JSAGrammar;
import dk.brics.grammar.operations.ProductionLengthNormalizer;
import dk.brics.string.grammar.Nonterminal;
import dk.brics.string.grammar.operations.Grammar2MLFA;
import dk.brics.string.mlfa.MLFA;
import dk.brics.string.mlfa.operations.MLFA2Automaton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegularApproximation extends ApproximationStrategy {
   private Map<String, Automaton> nt2a;
   private Map<Production, Automaton> p2a;
   private Grammar2MLFA g2m;
   private MLFA2Automaton m2a;
   private Map<String, Nonterminal> nt_map;

   @Override
   public String getName() {
      return "regular";
   }

   @Override
   public void init() {
      Grammar var1 = new ProductionLengthNormalizer(this.out).normalize(this.g);
      if (this.debug) {
         this.out.print("normalized grammar:\n" + var1.toString());
      }

      Grammar2JSAGrammar var2 = new Grammar2JSAGrammar();
      dk.brics.string.grammar.Grammar var3 = var2.convert(var1);
      this.nt_map = var2.getNonterminalMap();
      ArrayList var4 = new ArrayList();

      for (String var6 : this.g.getNonterminals()) {
         var4.add(this.nt_map.get(var6));
      }

      int var8 = -1;
      if (this.debug) {
         var8 = var3.getNumberOfNonLinearComponents();
         this.out.println("components: " + var3.getNumberOfComponents() + ", nonlinear: " + var8);
      }

      var3.approximateNonLinear(var4);
      if (this.debug && var8 > 0) {
         this.out.print("approximated grammar:\n" + var3.toString());

         for (String var7 : this.g.getNonterminals()) {
            this.out.println(var7 + " ~ " + this.nt_map.get(var7));
         }
      }

      this.g2m = new Grammar2MLFA(var3);
      MLFA var10 = this.g2m.convert();
      this.m2a = new MLFA2Automaton(var10);
      this.nt2a = new HashMap<>();
      this.clearProductionCache();
   }

   @Override
   public void verticalDone() {
      this.clearProductionCache();
   }

   private void clearProductionCache() {
      this.p2a = new HashMap<>();
   }

   private Automaton getAutomaton(String var1) {
      Automaton var2 = this.nt2a.get(var1);
      if (var2 == null) {
         var2 = this.m2a.extract(this.g2m.getMLFAStatePair(this.nt_map.get(var1)));
         this.nt2a.put(var1, var2);
      }

      return var2;
   }

   private Automaton getAutomaton(Production var1) {
      Automaton var2 = this.p2a.get(var1);
      if (var2 == null) {
         var2 = this.getAutomaton(var1.getEntities(), 0, var1.getEntities().size());
         this.p2a.put(var1, var2);
      }

      return var2;
   }

   private Automaton getAutomaton(List<Entity> var1, int var2, int var3) {
      ArrayList var4 = new ArrayList();

      for (int var5 = var2; var5 < var3; var5++) {
         Entity var6 = (Entity)var1.get(var5);
         Automaton var7 = var6.visitBy(new EntityVisitor<Automaton>() {
            public Automaton visitNonterminalEntity(NonterminalEntity var1) {
               return RegularApproximation.this.getAutomaton(var1.getNonterminal());
            }

            public Automaton visitRegexpTerminalEntity(RegexpTerminalEntity var1) {
               return var1.getAutomaton();
            }

            public Automaton visitStringTerminalEntity(StringTerminalEntity var1) {
               return Automaton.makeString(var1.getString());
            }

            public Automaton visitEOFTerminalEntity(EOFTerminalEntity var1) {
               return Automaton.makeEmptyString();
            }
         });
         var4.add(var7);
      }

      return Automaton.concatenate(var4);
   }

   @Override
   public HorizontalOverlapString checkHorizontalOverlap(Production var1, int var2) {
      Automaton var3 = this.getAutomaton(var1.getEntities(), 0, var2);
      Automaton var4 = this.getAutomaton(var1.getEntities(), var2, var1.getEntities().size());
      Automaton var5 = AutomataOperations.getOverlap(var3, var4);
      return AutomataOperations.getOverlapString(var5);
   }

   @Override
   public VerticalOverlapString checkVerticalOverlap(Production var1, Production var2) {
      Automaton var3 = this.getAutomaton(var1);
      Automaton var4 = this.getAutomaton(var2);
      Automaton var5 = var3.intersection(var4);
      String var6 = var5.getShortestExample(true);
      return var6 != null ? new VerticalOverlapString(var6) : null;
   }
}
