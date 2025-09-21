package dk.brics.grammar.ast;

public class AST {
   private BranchNode root;
   private String x;

   public AST(BranchNode var1, String var2) {
      this.root = var1;
      this.x = var2;
   }

   public BranchNode getRoot() {
      return this.root;
   }

   public String getOriginalString() {
      return this.x;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      this.root.print(this.x, var1);
      return var1.toString();
   }

   public void traverse(NodeVisitor var1) {
      this.root.traverse(var1);
   }
}
