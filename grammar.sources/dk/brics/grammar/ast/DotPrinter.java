package dk.brics.grammar.ast;

import dk.brics.misc.Chars;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class DotPrinter {
   private PrintWriter p;

   public DotPrinter(PrintWriter var1) {
      this.p = var1;
   }

   public void print(final AST var1) {
      this.p.println("digraph AST {");
      var1.traverse(
         new NodeVisitor() {
            private Map<Node, Integer> nodekey = new HashMap<>();
            private int next_key = 0;

            @Override
            public void visitLeafNode(LeafNode var1x) {
               DotPrinter.this.p
                  .println("  " + this.id(var1x) + " [shape=plaintext,label=\"" + Chars.escape(var1x.getString(var1.getOriginalString()), true) + "\"];");
            }

            @Override
            public void visitBranchNode(BranchNode var1x) {
               String var2 = Chars.escape(var1x.getName(), true);
               String var3 = var1x.getProductionID().getLabel();
               if (var3 != null) {
                  var2 = var2 + "\\[" + var3 + "\\]";
               }

               DotPrinter.this.p.println("  " + this.id(var1x) + " [shape=box,label=\"" + var2 + "\"];");

               for (String var5 : new TreeSet<>(var1x.getChildNames())) {
                  Node var6 = var1x.getChild(var5);
                  DotPrinter.this.p.println("  " + this.id(var1x) + " -> " + this.id(var6) + " [label=\"" + Chars.escape(var5, true) + "\"];");
               }
            }

            private String id(Node var1x) {
               Integer var2 = this.nodekey.get(var1x);
               if (var2 == null) {
                  var2 = this.next_key++;
                  this.nodekey.put(var1x, var2);
               }

               return "n" + var2;
            }
         }
      );
      this.p.println("}");
   }
}
