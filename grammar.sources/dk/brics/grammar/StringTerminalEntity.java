package dk.brics.grammar;

import dk.brics.misc.Chars;

public class StringTerminalEntity extends TerminalEntity {
   String string;

   public StringTerminalEntity(String var1) {
      super(null, null);
      this.string = var1;
   }

   @Override
   public String toString() {
      return "\"" + Chars.escape(this.string) + "\"";
   }

   public String getString() {
      return this.string;
   }

   @Override
   public <T> T visitBy(EntityVisitor<T> var1) {
      return (T)var1.visitStringTerminalEntity(this);
   }
}
