package dk.brics.grammar;

import dk.brics.automaton.Automaton;

public class RegexpTerminalEntity extends TerminalEntity {
   private Automaton automaton;
   private boolean max;
   private String automaton_name;

   public RegexpTerminalEntity(Automaton var1, boolean var2, String var3, String var4, String var5) {
      super(var4, var5);
      var1.determinize();
      this.automaton = var1;
      this.max = var2;
      this.automaton_name = var3;
   }

   @Override
   public String toString() {
      return "<" + this.automaton_name + ">";
   }

   public Automaton getAutomaton() {
      return this.automaton;
   }

   public boolean isMax() {
      return this.max;
   }

   public String getAutomatonName() {
      return this.automaton_name;
   }

   @Override
   public <T> T visitBy(EntityVisitor<T> var1) {
      return (T)var1.visitRegexpTerminalEntity(this);
   }
}
