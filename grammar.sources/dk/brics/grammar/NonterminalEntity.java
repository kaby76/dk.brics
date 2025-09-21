package dk.brics.grammar;

public class NonterminalEntity extends Entity {
   private String nonterminal;

   public NonterminalEntity(String var1, String var2, String var3) throws GrammarException {
      super(var2, var3);
      this.nonterminal = var1;
   }

   @Override
   public String toString() {
      return this.nonterminal;
   }

   public String getNonterminal() {
      return this.nonterminal;
   }

   @Override
   public <T> T visitBy(EntityVisitor<T> var1) {
      return (T)var1.visitNonterminalEntity(this);
   }
}
