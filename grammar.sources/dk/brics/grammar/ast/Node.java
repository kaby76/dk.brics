package dk.brics.grammar.ast;

public abstract class Node {
   private int i;
   private int j;

   protected Node(int var1, int var2) {
      this.i = var1;
      this.j = var2;
   }

   public abstract void print(String var1, StringBuilder var2);


   public static String mystring;
   public void setMystring(String x)
   {
      mystring = x;
   }
   public abstract void myprint(int level, Node parent);


   public int getFromIndex() {
      return this.i;
   }

   public int getToIndex() {
      return this.j;
   }

   public String getString(String var1) {
      return var1.substring(this.i, this.j);
   }

   abstract void traverse(NodeVisitor var1);

   public abstract void visitBy(NodeVisitor var1);
}
