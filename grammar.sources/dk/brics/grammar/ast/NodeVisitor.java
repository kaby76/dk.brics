package dk.brics.grammar.ast;

public interface NodeVisitor {
   void visitLeafNode(LeafNode var1);

   void visitBranchNode(BranchNode var1);
}
