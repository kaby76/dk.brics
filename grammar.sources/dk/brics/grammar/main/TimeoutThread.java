package dk.brics.grammar.main;

class TimeoutThread extends Thread {
   private boolean cancel = false;
   private boolean done = false;
   private Thread current = Thread.currentThread();
   private int secs;

   TimeoutThread(int var1) {
      this.secs = var1;
      this.start();
   }

   @Override
   public void run() {
      try {
         sleep((long)(this.secs * 1000));
         synchronized (this) {
            if (!this.cancel) {
//               this.current.stop(new TimeoutException());
            }

            this.done = true;
         }
      } catch (InterruptedException var4) {
         this.done = true;
      }
   }

   public void cancel() {
      synchronized (this) {
         if (!this.cancel) {
            this.cancel = true;
            if (!this.done) {
               this.interrupt();
            }
         }
      }
   }
}
