package dk.brics.grammar;

public interface EntityVisitor<T> {
   T visitStringTerminalEntity(StringTerminalEntity var1);

   T visitRegexpTerminalEntity(RegexpTerminalEntity var1);

   T visitEOFTerminalEntity(EOFTerminalEntity var1);

   T visitNonterminalEntity(NonterminalEntity var1);
}
