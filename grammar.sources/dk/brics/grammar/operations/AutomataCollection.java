package dk.brics.grammar.operations;

import dk.brics.automaton.Automaton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;

public class AutomataCollection implements Iterable<Automaton> {
   public static AutomataCollection emptyAutomataCollection = new AutomataCollection(false);
   private IdentityHashMap<Automaton, Automaton> h;
   private Set<String> singletons;
   private boolean modifiable;

   public AutomataCollection() {
      this(true);
   }

   private AutomataCollection(boolean var1) {
      this.h = new IdentityHashMap<>();
      this.singletons = new HashSet<>();
      this.modifiable = var1;
   }

   public AutomataCollection(AutomataCollection var1) {
      this.h = new IdentityHashMap<>(var1.h);
      this.singletons = new HashSet<>(var1.singletons);
      this.modifiable = true;
   }

   public boolean add(Automaton var1) {
      this.checkModifiable();
      return this.addInternal(var1);
   }

   public boolean addAll(AutomataCollection var1) {
      this.checkModifiable();
      boolean var2 = false;
      if (var1 != null) {
         for (Automaton var4 : var1) {
            var2 |= this.addInternal(var4);
         }
      }

      return var2;
   }

   private final boolean addInternal(Automaton var1) {
      String var2 = var1.getSingleton();
      if (var2 != null) {
         if (this.singletons.contains(var2)) {
            return false;
         } else {
            this.singletons.add(var2);
            return true;
         }
      } else {
         return this.h.put(var1, var1) == null;
      }
   }

   public boolean retainAll(AutomataCollection var1) {
      this.checkModifiable();
      boolean var2 = this.singletons.retainAll(var1.singletons);
      ArrayList var3 = new ArrayList();

      for (Automaton var5 : this.h.values()) {
         if (!var1.h.containsKey(var5)) {
            var3.add(var5);
         }
      }

      if (!var3.isEmpty()) {
         for (var var7 : var3) {
            this.h.remove(var7);
         }

         var2 = true;
      }

      return var2;
   }

   private final void checkModifiable() {
      if (!this.modifiable) {
         throw new UnsupportedOperationException("automata collection is immutable");
      }
   }

   @Override
   public Iterator<Automaton> iterator() {
      return this.getCollection().iterator();
   }

   public Collection<Automaton> getCollection() {
      ArrayList var1 = new ArrayList();

      for (String var3 : this.singletons) {
         var1.add(Automaton.makeString(var3));
      }

      var1.addAll(this.h.values());
      return var1;
   }

   public void makeImmutable() {
      this.modifiable = false;
   }

   @Override
   public String toString() {
      return this.getCollection().toString();
   }
}
