package dk.brics.grammar;

public class EOFTerminalEntity extends TerminalEntity {
   public EOFTerminalEntity() {
      super(null, null);
   }

   @Override
   public String toString() {
      return "<EOF>";
   }

   @Override
   public <T> T visitBy(EntityVisitor<T> var1) {
      return (T)var1.visitEOFTerminalEntity(this);
   }
}
