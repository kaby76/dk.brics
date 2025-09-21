package dk.brics.grammar.ast;

import dk.brics.grammar.ProductionID;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class BranchNode extends Node {
   private String name;
   private Map<String, Node> map;
   private ProductionID id;

   public BranchNode(ProductionID var1, String var2, int var3, int var4) {
      super(var3, var4);
      this.id = var1;
      this.name = var2;
   }

   public BranchNode(BranchNode var1, String var2, Node var3) {
      super(var1.getFromIndex(), var3.getToIndex());
      this.id = var1.id;
      this.name = var1.name;
      if (var1.map == null) {
         this.map = new LinkedHashMap<>();
      } else {
         this.map = new LinkedHashMap<>(var1.map);
      }

      this.map.put(var2, var3);
   }

   public BranchNode(BranchNode var1, int var2) {
      super(var1.getFromIndex(), var2);
      this.id = var1.id;
      this.name = var1.name;
      this.map = var1.map;
   }

   public Node getChild(String var1) {
      return this.map == null ? null : this.map.get(var1);
   }

   public BranchNode getBranchChild(String var1) throws ClassCastException {
      return this.map == null ? null : (BranchNode)this.map.get(var1);
   }

   public ProductionID getProductionID() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public Set<String> getChildNames() {
      return (Set<String>)(this.map == null ? new HashSet<>() : this.map.keySet());
   }

   public void replaceChild(String var1, BranchNode var2) {
      this.map.remove(var1);
      if (var2.map != null) {
         this.map.putAll(var2.map);
      }
   }

   public String getLeafString(String var1, AST var2) throws ClassCastException {
      LeafNode var3 = (LeafNode)this.getChild(var1);
      return var3 == null ? null : var3.getString(var2.getOriginalString());
   }

   public String getLabel() {
      return this.getProductionID().getLabel();
   }

   @Override
   public void print(String var1, StringBuilder sb) {
      sb.append(this.name);
      if (this.id.getLabel() != null) {
         sb.append("{" + this.id.getLabel() + "}");
      }

      sb.append("(").append(this.getFromIndex()).append('-').append(this.getToIndex()).append(")[");
      boolean var3 = true;
      if (this.map != null) {
         for (String var5 : this.map.keySet()) {
            if (!var3) {
               sb.append(",");
            }

            sb.append(var5).append("->");
            this.map.get(var5).print(var1, sb);
            var3 = false;
         }
      }

      sb.append("]");
   }

   @Override public void myprint(int level, Node parent)
   {
      for (int i = 0; i < level; ++i)
          System.out.print("    ");
      System.out.println(/* "BranchNode " + */ this.name);
      if (this.map != null) {
         for (String var5 : this.map.keySet()) {
            this.map.get(var5).myprint(level + 1, this);
         }
      }
   }

   @Override
   void traverse(NodeVisitor var1) {
      if (this.map != null) {
         for (Entry var3 : this.map.entrySet()) {
            ((Node)var3.getValue()).traverse(var1);
         }
      }

      var1.visitBranchNode(this);
   }

   @Override
   public void visitBy(NodeVisitor var1) {
      var1.visitBranchNode(this);
   }
}
