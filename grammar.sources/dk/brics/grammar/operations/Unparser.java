package dk.brics.grammar.operations;

import dk.brics.grammar.Entity;
import dk.brics.grammar.Grammar;
import dk.brics.grammar.ast.AST;
import dk.brics.grammar.ast.BranchNode;
import dk.brics.grammar.ast.LeafNode;
import dk.brics.grammar.ast.Node;
import dk.brics.grammar.ast.NodeVisitor;
import java.io.CharArrayWriter;
import java.io.PrintWriter;

public class Unparser {
   private Grammar g;
   private ExampleStringDeriver deriver;

   public Unparser(Grammar var1) {
      this.g = var1;
      this.deriver = new ExampleStringDeriver(var1);
   }

   public String unparse(AST var1) {
      return this.unparse(var1.getRoot(), var1.getOriginalString());
   }

   public void unparse(AST var1, PrintWriter var2) {
      this.unparse(var1.getRoot(), var2, var1.getOriginalString());
   }

   public String unparse(Node var1, String var2) {
      CharArrayWriter var3 = new CharArrayWriter();
      this.unparse(var1, new PrintWriter(var3, true), var2);
      return var3.toString();
   }

   public void unparse(Node var1, final PrintWriter var2, final String var3) {
      var1.visitBy(new NodeVisitor() {
         @Override
         public void visitBranchNode(BranchNode var1) {
            for (Entity var3x : Unparser.this.g.getProduction(var1.getProductionID()).getEntities()) {
               if (var3x.isExplicitlyLabeled()) {
                  Unparser.this.unparse(var1.getChild(var3x.getLabel()), var2, var3);
               } else if (var3x.getExample() != null) {
                  var2.append(var3x.getExample());
               } else {
                  var2.append(Unparser.this.deriver.getExample(var3x));
               }
            }
         }

         @Override
         public void visitLeafNode(LeafNode var1) {
            var2.append(var1.getString(var3));
         }
      });
   }
}
