package dk.brics.grammar.operations;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import dk.brics.grammar.EOFTerminalEntity;
import dk.brics.grammar.Entity;
import dk.brics.grammar.EntityVisitor;
import dk.brics.grammar.NonterminalEntity;
import dk.brics.grammar.RegexpTerminalEntity;
import dk.brics.grammar.StringTerminalEntity;
import dk.brics.string.Misc;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Stack;

public class CharSet {
   private LinkedList<CharSet.Interval> intervals = new LinkedList<>();
   private boolean eof;

   public CharSet() {
   }

   public CharSet(String var1, boolean var2, boolean var3, boolean var4) {
      this();
      if (var1 != null && var1.length() > 0) {
         if (var4) {
            if (var2) {
               int var5 = this.two(var1.charAt(0), var1.charAt(1));
               this.intervals.add(new CharSet.Interval(var5, var5));
            } else if (var3) {
               int var7 = this.two(var1.charAt(var1.length() - 2), var1.charAt(var1.length() - 1));
               this.intervals.add(new CharSet.Interval(var7, var7));
            } else {
               for (byte var8 = 0; var8 < var1.length(); var8 += 2) {
                  int var6 = this.two(var1.charAt(var8), var1.charAt(var8 + 1));
                  this.intervals.add(new CharSet.Interval(var6, var6));
               }
            }
         } else if (var2) {
            char var9 = var1.charAt(0);
            this.intervals.add(new CharSet.Interval(var9, var9));
         } else if (var3) {
            char var10 = var1.charAt(var1.length() - 1);
            this.intervals.add(new CharSet.Interval(var10, var10));
         } else {
            for (int var11 = 0; var11 < var1.length(); var11++) {
               char var12 = var1.charAt(var11);
               this.intervals.add(new CharSet.Interval(var12, var12));
            }
         }
      }
   }

   public CharSet(Automaton var1, boolean var2, boolean var3, boolean var4) {
      this(var1.getSingleton(), var2, var3, var4);
      if (var1.getSingleton() == null) {
         if (var4) {
            if (var2) {
               for (Transition var6 : var1.getInitialState().getTransitions()) {
                  for (int var7 = var6.getMin(); var7 <= var6.getMax(); var7++) {
                     for (Transition var9 : var6.getDest().getTransitions()) {
                        this.add(new CharSet.Interval(this.two((char)var7, var9.getMin()), this.two((char)var7, var9.getMax())));
                     }
                  }
               }
            } else if (var3) {
               for (State var18 : var1.getStates()) {
                  for (Transition var27 : var18.getTransitions()) {
                     for (Transition var10 : var27.getDest().getTransitions()) {
                        if (var10.getDest().isAccept()) {
                           for (int var11 = var27.getMin(); var11 <= var27.getMax(); var11++) {
                              this.add(new CharSet.Interval(this.two((char)var11, var10.getMin()), this.two((char)var11, var10.getMax())));
                           }
                        }
                     }
                  }
               }
            } else {
               HashSet var14 = new HashSet();
               Stack var19 = new Stack();
               var19.push(var1.getInitialState());
               var14.add(var1.getInitialState());

               while (!var19.isEmpty()) {
                  State var24 = (State)var19.pop();

                  for (Transition var32 : var24.getTransitions()) {
                     for (int var33 = var32.getMin(); var33 <= var32.getMax(); var33++) {
                        for (Transition var12 : var32.getDest().getTransitions()) {
                           this.add(new CharSet.Interval(this.two((char)var33, var12.getMin()), this.two((char)var33, var12.getMax())));
                           if (!var14.contains(var12.getDest())) {
                              var19.push(var12.getDest());
                              var14.add(var12.getDest());
                           }
                        }
                     }
                  }
               }
            }
         } else if (var2) {
            for (Transition var20 : var1.getInitialState().getTransitions()) {
               this.add(new CharSet.Interval(var20.getMin(), var20.getMax()));
            }
         } else if (var3) {
            for (State var21 : var1.getStates()) {
               for (Transition var29 : var21.getTransitions()) {
                  if (var29.getDest().isAccept()) {
                     this.add(new CharSet.Interval(var29.getMin(), var29.getMax()));
                  }
               }
            }
         } else {
            for (State var22 : var1.getStates()) {
               for (Transition var30 : var22.getTransitions()) {
                  this.add(new CharSet.Interval(var30.getMin(), var30.getMax()));
               }
            }
         }
      }
   }

   public boolean add(CharSet var1) {
      boolean var2 = false;

      for (CharSet.Interval var4 : var1.getIntervals()) {
         var2 |= this.add(var4);
      }

      if (var1.containsEOF()) {
         var2 |= this.addEOF();
      }

      return var2;
   }

   public boolean addEOF() {
      boolean var1 = !this.eof;
      this.eof = true;
      return var1;
   }

   private LinkedList<CharSet.Interval> getIntervals() {
      return this.intervals;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append('[');
      boolean var2 = true;

      for (CharSet.Interval var4 : this.intervals) {
         if (var2) {
            var2 = false;
         } else {
            var1.append(',');
         }

         var1.append(var4);
      }

      if (this.eof) {
         if (!var2) {
            var1.append(',');
         }

         var1.append("EOF");
      }

      var1.append(']');
      return var1.toString();
   }

   private boolean add(CharSet.Interval var1) {
      boolean var2 = false;
      ListIterator var3 = this.intervals.listIterator();
      ListIterator var4 = this.intervals.listIterator();

      while (var3.hasNext()) {
         CharSet.Interval var5 = (CharSet.Interval)var3.next();
         if (var1.getMax() + 1 < var5.getMin()) {
            var4.add(new CharSet.Interval(var1));
            return true;
         }

         if (var1.getMin() <= var5.getMax() + 1) {
            if (var1.getMin() < var5.getMin()) {
               var5.setMin(var1.getMin());
               var2 = true;
            }

            if (var1.getMax() > var5.getMax()) {
               var5.setMax(var1.getMax());
               var2 = true;
            }

            var4.next();

            while (var4.hasNext()) {
               CharSet.Interval var6 = (CharSet.Interval)var4.next();
               if (var5.getMax() + 1 < var6.getMin()) {
                  return var2;
               }

               if (var5.getMax() < var6.getMax()) {
                  var5.setMax(var6.getMax());
                  var4.remove();
                  return true;
               }

               var4.remove();
               var2 = true;
            }

            return var2;
         }

         var4.next();
      }

      var3.add(new CharSet.Interval(var1));
      return true;
   }

   public boolean contains(char var1) {
      for (CharSet.Interval var3 : this.intervals) {
         if (var3.contains(var1)) {
            return true;
         }
      }

      return false;
   }

   public boolean containsEOF() {
      return this.eof;
   }

   public boolean disjoint(CharSet var1) {
      if (this.eof && var1.eof) {
         return false;
      } else {
         Iterator var2 = this.intervals.iterator();
         Iterator var3 = var1.intervals.iterator();
         if (var2.hasNext() && var3.hasNext()) {
            CharSet.Interval var4 = (CharSet.Interval)var2.next();
            CharSet.Interval var5 = (CharSet.Interval)var3.next();

            while (true) {
               if (!var4.disjoint(var5)) {
                  return false;
               }

               if (var4.max < var5.max) {
                  if (!var2.hasNext()) {
                     break;
                  }

                  var4 = (CharSet.Interval)var2.next();
               } else {
                  if (!var3.hasNext()) {
                     break;
                  }

                  var5 = (CharSet.Interval)var3.next();
               }
            }

            return true;
         } else {
            return true;
         }
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof CharSet)) {
         return false;
      } else {
         CharSet var2 = (CharSet)var1;
         return this.eof == var2.eof && this.intervals.equals(var2.intervals);
      }
   }

   @Override
   public int hashCode() {
      return this.intervals.hashCode() + (this.eof ? 1 : 0);
   }

   private int two(char var1, char var2) {
      return var1 << 16 ^ var2;
   }

   static CharSet getCharSet(Entity var0, final boolean var1, final boolean var2) {
      return var0.visitBy(new EntityVisitor<CharSet>() {
         public CharSet visitNonterminalEntity(NonterminalEntity var1x) {
            return null;
         }

         public CharSet visitRegexpTerminalEntity(RegexpTerminalEntity var1x) {
            return new CharSet(var1x.getAutomaton(), var2, !var2, var1);
         }

         public CharSet visitStringTerminalEntity(StringTerminalEntity var1x) {
            return var1x.getString().length() == 0 ? new CharSet() : new CharSet(var1x.getString(), var2, !var2, var1);
         }

         public CharSet visitEOFTerminalEntity(EOFTerminalEntity var1x) {
            CharSet var2x = new CharSet();
            var2x.addEOF();
            return var2x;
         }
      });
   }

   static class Interval {
      private int min;
      private int max;

      Interval(int var1, int var2) {
         this.min = var1;
         this.max = var2;
      }

      Interval(CharSet.Interval var1) {
         this.min = var1.min;
         this.max = var1.max;
      }

      boolean contains(int var1) {
         return this.min <= var1 && var1 <= this.max;
      }

      boolean disjoint(CharSet.Interval var1) {
         return var1.max < this.min || this.max < var1.min;
      }

      int getMin() {
         return this.min;
      }

      int getMax() {
         return this.max;
      }

      void setMin(int var1) {
         this.min = var1;
      }

      void setMax(int var1) {
         this.max = var1;
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof CharSet.Interval)) {
            return false;
         } else {
            CharSet.Interval var2 = (CharSet.Interval)var1;
            return this.min == var2.min && this.max == var2.max;
         }
      }

      @Override
      public int hashCode() {
         return this.min * 3 + this.max * 2;
      }

      @Override
      public String toString() {
         return this.min == this.max
            ? Misc.escape(String.valueOf(this.min))
            : Misc.escape(String.valueOf(this.min)) + "-" + Misc.escape(String.valueOf(this.max));
      }
   }
}
