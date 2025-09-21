package dk.brics.grammar;

import dk.brics.misc.Chars;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class Grammar {
   private String start;
   private Collection<Production> productions;
   private Map<String, Collection<Production>> productionmap;
   private Map<ProductionID, Production> idmap;
   private Collection<String> nonterminals;
   private boolean unfolded;

   public Grammar(String var1, Collection<Production> var2) {
      this.start = var1;
      this.productions = var2;
      this.productionmap = new HashMap<>();
      this.idmap = new HashMap<>();
      this.nonterminals = new TreeSet<>();
      boolean var3 = true;

      for (Production var5 : var2) {
         this.idmap.put(var5.getID(), var5);
         var var6 = this.productionmap.get(var5.getNonterminal());
         if (var6 == null) {
            var6 = new ArrayList();
            this.productionmap.put(var5.getNonterminal(), (Collection<Production>)var6);
            this.nonterminals.add(var5.getNonterminal());
         }

         var6.add(var5);
         ProductionID var7 = var5.getID();
         if (var7.getLabel() == null) {
            var7.setLabel("#" + var6.size());
         }

         for (Entity var9 : var5.getEntities()) {
            if (var9.getLabel() != null) {
               var3 = false;
            }
         }
      }

      if (var3) {
         for (Production var11 : var2) {
            int var12 = 1;

            for (Entity var14 : var11.getEntities()) {
               if (var14 instanceof NonterminalEntity || var14 instanceof RegexpTerminalEntity) {
                  var14.setLabel("#" + var12++);
               }
            }
         }
      }
   }

   public Grammar(Grammar var1) {
      this(var1.getStart(), var1.cloneProductions());
   }

   private Collection<Production> cloneProductions() {
      ArrayList var1 = new ArrayList();

      for (Production var3 : this.productions) {
         var1.add(new Production(var3.getNonterminal(), var3.getEntities(), var3.isUnordered(), var3.getID(), var3.getPriority()));
      }

      return var1;
   }

   public void addProductions(Collection<Production> var1) {
      this.productions.addAll(var1);

      for (Production var3 : this.productions) {
         this.idmap.put(var3.getID(), var3);
         var var4 = this.productionmap.get(var3.getNonterminal());
         if (var4 == null) {
            var4 = new ArrayList();
            this.productionmap.put(var3.getNonterminal(), (Collection<Production>)var4);
            this.nonterminals.add(var3.getNonterminal());
         }

         var4.add(var3);
      }
   }

   public Production getProduction(ProductionID var1) {
      return this.idmap.get(var1);
   }

   public Collection<Production> getProductions(String var1) {
      return this.productionmap.get(var1);
   }

   public Collection<Production> getProductions() {
      return this.productions;
   }

   public String getStart() {
      return this.start;
   }

   public void setStart(String var1) {
      this.start = var1;
   }

   public Collection<String> getNonterminals() {
      return this.nonterminals;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      Production[] prods = this.productions.toArray(new Production[0]);
      Arrays.sort(prods, new Comparator<Production>() {
         public int compare(Production var1, Production var2) {
            int var3 = var1.getNonterminal().compareTo(var2.getNonterminal());
            if (var3 == 0) {
               var3 = var2.getPriority() - var1.getPriority();
            } else if (var1.getNonterminal().equals(Grammar.this.start)) {
               var3 = -1;
            } else if (var2.getNonterminal().equals(Grammar.this.start)) {
               var3 = 1;
            }

            if (var3 == 0 && var1.getID().hasExplicitLabel() && var2.getID().hasExplicitLabel()) {
               var3 = var1.getID().getLabel().compareTo(var2.getID().getLabel());
            }

            return var3;
         }
      });
      Production var3 = null;

      for (Production prod : prods) {
         boolean var8 = false;
         if (var3 != null && var3.getNonterminal().equals(prod.getNonterminal())) {
            var1.append(' ');
         } else {
            var1.append(prod.getNonterminal());
            var8 = true;
         }

         if (prod.getID().hasExplicitLabel()) {
            var1.append('[').append(prod.getID().getLabel()).append("]");
         }

         if (var8 || prod.getID().hasExplicitLabel()) {
            var1.append(" ");
         }

         if (var3 != null && var3.getNonterminal().equals(prod.getNonterminal()) && var3.getPriority() > prod.getPriority()) {
            var1.append(">");
         }

         if (var8) {
            var1.append(":");
         } else {
            var1.append("|");
         }

         if (prod.unordered) {
            var1.append('&');
         }

         for (Entity var10 : prod.getEntities()) {
            var1.append(' ').append(var10);
            boolean var11 = var10.isExplicitlyLabeled();
            String var12 = var10.getExample();
            if (var11 || var12 != null) {
               var1.append('[');
            }

            if (var11) {
               var1.append(var10.getLabel());
            }

            if (var11 && var12 != null) {
               var1.append(' ');
            }

            if (var12 != null) {
               var1.append('"').append(Chars.escape(var12)).append('"');
            }

            if (var11 || var12 != null) {
               var1.append(']');
            }
         }

         var1.append('\n');
         var3 = prod;
      }

      return var1.toString();
   }

   public void setUnfolded(boolean var1) {
      this.unfolded = var1;
   }

   public boolean isUnfolded() {
      return this.unfolded;
   }
}
