package dk.brics.grammar.operations;

import dk.brics.grammar.Grammar;
import dk.brics.grammar.NonterminalEntity;
import dk.brics.grammar.Production;
import dk.brics.grammar.ProductionID;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ProductionLengthNormalizer {
   private PrintWriter out;

   public ProductionLengthNormalizer(PrintWriter var1) {
      this.out = var1;
   }

   public Grammar normalize(Grammar var1) {
      int var2 = 1;
      ArrayList var3 = new ArrayList();
      boolean var4 = false;

      for (Production var6 : var1.getProductions()) {
         if (var6.isUnordered()) {
            var4 = true;
         }

         String var7 = var6.getNonterminal();
         String var8 = var6.getID().getLabel();
         int var9 = var6.getPriority();
         List var10 = var6.getEntities();

         int var11;
         for (var11 = 0; var11 + 2 < var10.size(); var11++) {
            String var12 = var6.getNonterminal() + "'" + var2++;
            NonterminalEntity var13 = new NonterminalEntity(var12, "$", null);
            ArrayList var14 = new ArrayList();
            var14.add(var10.get(var11));
            var14.add(var13);
            var3.add(new Production(var7, var14, false, new ProductionID(var8), var9));
            var8 = null;
            var9 = 0;
            var7 = var12;
         }

         ArrayList var15;
         for (var15 = new ArrayList(); var11 < var10.size(); var11++) {
            var15.add(var10.get(var11));
         }

         var3.add(new Production(var7, var15, false, new ProductionID(var8), var9));
      }

      if (var4) {
         this.out.println("*** warning: grammar contains unordered productions (treated as ordered)");
      }

      return new Grammar(var1.getStart(), var3);
   }
}
