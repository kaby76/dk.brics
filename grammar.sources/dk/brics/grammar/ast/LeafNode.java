package dk.brics.grammar.ast;

public class LeafNode extends Node {
   private String override;

   public LeafNode(int var1, int var2) {
      super(var1, var2);
   }

   @Override
   public void print(String var1, StringBuilder var2) {
      var2.append(this.getString(var1));
   }

   @Override public void myprint(int level, Node parent)
   {
      for (int i = 0; i < level; ++i)
          System.out.print("    ");
      //System.out.println("LeafNode " + this.getMystring());
      boolean add_quotes = false;
      if (parent instanceof BranchNode)
      {
         var bn = (BranchNode)parent;
         var pid = bn.getProductionID();
         var label = pid.getLabel();
         if (label == "string_terminal") add_quotes = true;
      }
      if (add_quotes) System.out.print("'");
      System.out.print(mystring.substring(this.getFromIndex(), this.getToIndex()));
      if (add_quotes) System.out.print("'");
      System.out.println();
   }

   @Override
   void traverse(NodeVisitor var1) {
      var1.visitLeafNode(this);
   }

   @Override
   public void visitBy(NodeVisitor var1) {
      var1.visitLeafNode(this);
   }

   public void setString(String var1) {
      this.override = var1;
   }

   @Override
   public String getString(String var1) {
      return this.override != null ? this.override : super.getString(var1);
   }
}
