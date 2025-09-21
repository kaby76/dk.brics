package dk.brics.grammar;

public class VoidEntityVisitor implements EntityVisitor<Object> {
   @Override
   public final Object visitNonterminalEntity(NonterminalEntity var1) {
      this.visitNonterminal(var1);
      return null;
   }

   @Override
   public final Object visitRegexpTerminalEntity(RegexpTerminalEntity var1) {
      this.visitTerminal(var1);
      this.visitRegexpTerminal(var1);
      return null;
   }

   @Override
   public final Object visitStringTerminalEntity(StringTerminalEntity var1) {
      this.visitTerminal(var1);
      this.visitStringTerminal(var1);
      return null;
   }

   @Override
   public final Object visitEOFTerminalEntity(EOFTerminalEntity var1) {
      this.visitTerminal(var1);
      this.visitEOFTerminal(var1);
      return null;
   }

   public void visitNonterminal(NonterminalEntity var1) {
   }

   public void visitTerminal(TerminalEntity var1) {
   }

   public void visitRegexpTerminal(RegexpTerminalEntity var1) {
   }

   public void visitStringTerminal(StringTerminalEntity var1) {
   }

   public void visitEOFTerminal(EOFTerminalEntity var1) {
   }
}
