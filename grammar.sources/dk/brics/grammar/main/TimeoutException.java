package dk.brics.grammar.main;

class TimeoutException extends RuntimeException {
   public TimeoutException() {
      super("timeout");
   }
}
