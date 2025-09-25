package dk.brics.grammar.parser;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import dk.brics.grammar.EOFTerminalEntity;
import dk.brics.grammar.Entity;
import dk.brics.grammar.EntityVisitor;
import dk.brics.grammar.Grammar;
import dk.brics.grammar.NonterminalEntity;
import dk.brics.grammar.Production;
import dk.brics.grammar.RegexpTerminalEntity;
import dk.brics.grammar.StringTerminalEntity;
import dk.brics.grammar.VoidEntityVisitor;
import dk.brics.grammar.ast.AST;
import dk.brics.grammar.ast.BranchNode;
import dk.brics.grammar.ast.LeafNode;
import dk.brics.grammar.ast.Node;
import dk.brics.grammar.operations.CharSet;
import dk.brics.grammar.operations.SelectFollowsFinder;
import dk.brics.grammar.operations.Unparser;
import dk.brics.misc.Properties;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class Parser {
   private Grammar g;
   private boolean debug = false;
   private PrintWriter out;
   private boolean enable_selectfollows = true;
   private int max_i;
   private SortedSet<PState> _pending;
   private Set<PState> _visited;
   private Map<Integer, Map<String, List<PState>>> ntmap;
   private Map<Integer, Map<String, List<PState>>> complmap;
   private Map<String, CharSet> first;
   private Map<String, CharSet> follow;
   private String _input;
   private long max_memory;
   private long max_states;
   private long total_states;
   private Unparser unparser;

   public Parser(Grammar grammar, PrintWriter var2) {
      this.g = grammar;
      this.out = var2;
      this.unparser = new Unparser(grammar);
      this.checkDebugMode();
      if (this.enable_selectfollows) {
         this.first = new HashMap<>();
         this.follow = new HashMap<>();
         SelectFollowsFinder first_follow_finder = new SelectFollowsFinder(grammar);
         this.first = first_follow_finder.getNonterminalFirst();
         this.follow = first_follow_finder.getNonterminalFollows();
         if (this.debug) {
            for (String var5 : grammar.getNonterminals()) {
               var2.println("select(" + var5 + ")=" + this.first.get(var5));
               var2.println("follows(" + var5 + ")=" + this.follow.get(var5));
            }
         }
      }
   }

   private void checkDebugMode() {
      this.debug = this.out != null && Properties.get("dk.brics.grammar.parser.debug");
   }

   public boolean check(String input, Production var2) {
      try {
         this.parse(input, null, var2);
         return true;
      } catch (ParseException var4) {
         return false;
      }
   }

   public AST parse(String input, String input_file_name) throws ParseException {
      return this.parse(input, input_file_name, null);
   }

   public AST parse(String var1) throws ParseException {
      return this.parse(var1, null, null);
   }

   public AST parse(String input, String input_file_name, Production var3) throws ParseException {
      this.checkDebugMode();
      AST ast = null;
      boolean var5 = this.enable_selectfollows;

      try {
         this._pending = new TreeSet<>();
         this._visited = new HashSet<>();
         this.ntmap = new HashMap<>();
         this.complmap = new HashMap<>();
         this.max_states = this.total_states = 0L;
         this._input = input;
         this.max_i = 0;
         if (var3 != null) {
            this.enable_selectfollows = false;
            this.addState(PState.makeNew(var3, 0, new BranchNode(var3.getID(), var3.getNonterminal(), 0, 0)));
         } else {
            for (Production var7 : this.g.getProductions(this.g.getStart())) {
               this.out.println("add state for prod " + var7);
               this.addState(PState.makeNew(var7, 0, new BranchNode(var7.getID(), var7.getNonterminal(), 0, 0)));
            }
         }

         while (!this._pending.isEmpty()) {
            PState var16 = this._pending.first();
            this._pending.remove(var16);
            if (this.debug) {
               this.out.println("processing " + var16.prod.print(var16.getRemaining()) + " from index " + var16.from + " to " + var16.current);
            }

            if (var16.isDone()) {
               if (this.debug) {
                  this.out.println("reducing " + var16.prod.print(var16.getRemaining()) + " from index " + var16.from + " to " + var16.current);
               }

               if ((var3 == var16.prod || var3 == null && var16.prod.getNonterminal().equals(this.g.getStart()))
                  && var16.from == 0
                  && var16.current == input.length()) {
                  ast = new AST(var16.ast, input);
                  break;
               }

               this.addCompleted(var16);
               Map var18 = this.ntmap.get(var16.from);
               if (var18 != null) {
                  List var19 = (List)var18.get(var16.prod.getNonterminal());
                  if (var19 != null) {
                     for (var var10 : new ArrayList(var19)) {
                        if (((PState)var10).prod.isUnordered()) {
                           for (Entity var12 : ((PState)var10).getRemaining()) {
                              if (var12 instanceof NonterminalEntity && ((NonterminalEntity)var12).getNonterminal().equals(var16.prod.getNonterminal())) {
                                 this.complete(var16, ((PState)var10), (NonterminalEntity)var12);
                              }
                           }
                        } else {
                           this.complete(var16, ((PState)var10), (NonterminalEntity)((PState)var10).getNextRemaining());
                        }
                     }
                  }
               }
            } else if (var16.prod.isUnordered()) {
               for (Entity var8 : var16.getRemaining()) {
                  this.parseEntity(var16, var8);
               }
            } else {
               this.parseEntity(var16, var16.getNextRemaining());
            }
         }

         if (ast == null) {
            throw new ParseException(input_file_name, input, this.max_i);
         }
      } finally {
         this._pending = null;
         this._visited = null;
         this.ntmap = null;
         this.complmap = null;
         this._input = null;
         this.enable_selectfollows = var5;
         if (this.debug) {
            this.out.println("max_states = " + this.max_states);
            this.out.println("total_states = " + this.total_states);
         }
      }
      String ast_str = null;
      if (ast != null) {
         ast_str = ast.toString();
         var root = ast.getRoot();
         ast.getRoot().setMystring(ast.getOriginalString());
         ast.getRoot().myprint(0, null);
      }
      return ast;
   }

   private void addCompleted(PState var1) {
      Object var2 = this.complmap.get(var1.from);
      if (var2 == null) {
         var2 = new HashMap();
         this.complmap.put(var1.from, (Map<String, List<PState>>)var2);
      }

      Object var3 = (List)((HashMap)var2).get(var1.prod.getNonterminal());
      if (var3 == null) {
         var3 = new ArrayList();
         ((HashMap)var2).put(var1.prod.getNonterminal(), var3);
      }

      ((List)var3).add(var1);
   }

   private void complete(PState var1, PState var2, NonterminalEntity var3) throws ParseException {
      BranchNode var4 = null;
      boolean var5 = true;
      if (var2.prod.isUnordered() && var1.getFrom() == var1.getCurrent()) {
         Entity var6 = var2.getRemaining().get(0);
         if (!(var6 instanceof NonterminalEntity) || !((NonterminalEntity)var6).getNonterminal().equals(var3.getNonterminal())) {
            if (this.debug) {
               this.out.println("cancelling epsilon completion of " + var3.getNonterminal() + " for " + var2.prod.print(var2.getRemaining()));
            }

            var5 = false;
         }
      }

      if (var5) {
         if (var3.isExplicitlyLabeled()) {
            var5 = this.checkEquality(var2.ast, var3.getLabel(), var1.ast);
            var4 = new BranchNode(var2.ast, var3.getLabel(), var1.ast);
         } else {
            var4 = new BranchNode(var2.ast, var1.current);
         }
      }

      if (var5) {
         PState var8;
         if (var2.prod.isUnordered()) {
            ArrayList var7 = new ArrayList<>(var2.getRemaining());
            var7.remove(var3);
            var8 = PState.makeNextUnordered(var2.prod, var7, var2.from, var1.current, var4);
         } else {
            var8 = PState.makeNextOrdered(var2.prod, var2.next_entity + 1, var2.from, var1.current, var4);
         }

         this.addState(var8);
      }
   }

   private boolean checkEquality(BranchNode var1, String var2, Node var3) {
      boolean var4 = true;
      Node var5 = var1.getChild(var2);
      if (var5 != null) {
         String var6 = this.unparser.unparse(var3, this._input);
         String var7 = this.unparser.unparse(var5, this._input);
         if (!var6.equals(var7)) {
            var4 = false;
         }
      }

      return var4;
   }

   private void parseEntity(final PState var1, final Entity var2) throws ParseException {
      ArrayList var3 = null;
      if (var1.prod.isUnordered()) {
         var3 = new ArrayList<>(var1.getRemaining());
         var3.remove(var2);
      }
      final ArrayList var3_final = var3;
      final ArrayList var5 = new ArrayList();
      var2.visitBy(new EntityVisitor<Object>() {
         @Override
         public Object visitStringTerminalEntity(StringTerminalEntity var1x) {
            String var2x = var1x.getString();
            int var3x = var2x.length();
            if (var1.current + var3x <= Parser.this._input.length() && Parser.this._input.substring(var1.current, var1.current + var3x).equals(var2x)) {
               if (Parser.this.debug) {
                  Parser.this.out.println("matched string terminal " + var2x);
               }

               boolean var5x = true;
               BranchNode var4;
               if (var2.isExplicitlyLabeled()) {
                  LeafNode var6 = new LeafNode(var1.current, var1.current + var3x);
                  var5x = Parser.this.checkEquality(var1.ast, var2.getLabel(), var6);
                  var4 = new BranchNode(var1.ast, var2.getLabel(), var6);
               } else {
                  var4 = new BranchNode(var1.ast, var1.current + var3x);
               }

               if (var5x) {
                  PState var7;
                  if (var1.prod.isUnordered()) {
                     var7 = PState.makeNextUnordered(var1.prod, var3_final, var1.from, var1.current + var3x, var4);
                  } else {
                     var7 = PState.makeNextOrdered(var1.prod, var1.next_entity + 1, var1.from, var1.current + var3x, var4);
                  }

                  var5.add(var7);
               }
            }

            return null;
         }

         @Override
         public Object visitRegexpTerminalEntity(RegexpTerminalEntity var1x) {
            ArrayList var2x = new ArrayList();
            Automaton var3x = var1x.getAutomaton();
            State var4 = var3x.getInitialState();
            int var5x = 0;

            while (var4 != null) {
               if (var4.isAccept()) {
                  if (Parser.this.debug) {
                     Parser.this.out.println("matched regexp terminal " + var1x.getAutomatonName());
                  }

                  boolean var7 = true;
                  BranchNode var6;
                  if (var2.isExplicitlyLabeled()) {
                     LeafNode var8 = new LeafNode(var1.current, var1.current + var5x);
                     var7 = Parser.this.checkEquality(var1.ast, var2.getLabel(), var8);
                     var6 = new BranchNode(var1.ast, var2.getLabel(), var8);
                  } else {
                     var6 = new BranchNode(var1.ast, var1.current + var5x);
                  }

                  if (var7) {
                     PState var11;
                     if (var1.prod.isUnordered()) {
                        var11 = PState.makeNextUnordered(var1.prod, var3_final, var1.from, var1.current + var5x, var6);
                     } else {
                        var11 = PState.makeNextOrdered(var1.prod, var1.next_entity + 1, var1.from, var1.current + var5x, var6);
                     }

                     var2x.add(var11);
                  }
               }

               if (var1.current + var5x >= Parser.this._input.length()) {
                  break;
               }

               var4 = var4.step(Parser.this._input.charAt(var1.current + var5x++));
            }

            if (var1x.isMax() && !var2x.isEmpty()) {
               var5.add(var2x.get(var2x.size() - 1));
            } else {
               for (var var10 : var2x) {
                  var5.add(var10);
               }
            }

            return null;
         }

         @Override
         public Object visitEOFTerminalEntity(EOFTerminalEntity var1x) {
            if (var1.current == Parser.this._input.length()) {
               if (Parser.this.debug) {
                  Parser.this.out.println("matched EOF");
               }

               PState var2x;
               if (var1.prod.isUnordered()) {
                  var2x = PState.makeNextUnordered(var1.prod, var3_final, var1.from, var1.current, var1.ast);
               } else {
                  var2x = PState.makeNextOrdered(var1.prod, var1.next_entity + 1, var1.from, var1.current, var1.ast);
               }

               var5.add(var2x);
            }

            return null;
         }

         @Override
         public Object visitNonterminalEntity(NonterminalEntity var1x) {
            Collection var2x = Parser.this.g.getProductions(var1x.getNonterminal());
            if (var2x != null) {
               for (var var4 : var2x) {
                  var5.add(PState.makeNew((Production)var4, var1.current, new BranchNode(((Production)var4).getID(), ((Production)var4).getNonterminal(), var1.current, var1.current)));
               }
            }

            return null;
         }
      });

      for (var var7 : var5) {
         this.addState((PState)var7);
      }
   }

   private void addState(PState var1) throws ParseException {
      if (var1.current > this.max_i) {
         this.max_i = var1.current;
      }

      if (!this._visited.contains(var1)) {
         if (!this.isApplicable(var1)) {
            if (this.debug) {
               this.out.println("nonmatching lookahead for " + var1.prod.print(var1.getRemaining()) + " from index " + var1.from + " to " + var1.current);
            }
         } else {
            if (this.debug) {
               this.out.println("adding " + var1.prod.print(var1.getRemaining()) + " from index " + var1.from + " to pending[" + var1.current + "]");
            }

            this._visited.add(var1);
            this.total_states++;
            this._pending.add(var1);
            if ((long)this._pending.size() > this.max_states) {
               this.max_states = (long)this._pending.size();
            }

            Runtime var2 = Runtime.getRuntime();
            long var3 = var2.totalMemory() - var2.freeMemory();
            if (var3 > this.max_memory) {
               this.max_memory = var3;
            }

            this.handlePendingCompletes(var1);
         }
      }
   }

   private boolean isApplicable(PState var1) {
      if (!var1.isDone()) {
         if (var1.prod.isUnordered()) {
            for (Entity var3 : var1.getRemaining()) {
               if (this.isApplicableEntity(var1, var3)) {
                  return true;
               }
            }

            return false;
         } else {
            return this.isApplicableEntity(var1, var1.getNextRemaining());
         }
      } else {
         if (this.enable_selectfollows) {
            CharSet var2 = this.follow.get(var1.prod.getNonterminal());
            if (var2 != null
               && (var1.current < this._input.length() && !var2.contains(this._input.charAt(var1.current)) || var1.current == this._input.length() && !var2.containsEOF())) {
               return false;
            }
         }

         return true;
      }
   }

   private Boolean isApplicableEntity(final PState var1, Entity var2) {
      return var2.visitBy(
         new EntityVisitor<Boolean>() {
            public Boolean visitNonterminalEntity(NonterminalEntity var1x) {
               if (Parser.this.enable_selectfollows) {
                  CharSet var2 = Parser.this.first.get(var1x.getNonterminal());
                  if (var2 != null
                     && (
                        var1.current < Parser.this._input.length() && !var2.contains(Parser.this._input.charAt(var1.current))
                           || var1.current == Parser.this._input.length() && !var2.containsEOF()
                     )) {
                     return false;
                  }
               }

               return true;
            }

            public Boolean visitRegexpTerminalEntity(RegexpTerminalEntity var1x) {
               State var2 = var1x.getAutomaton().getInitialState();
               int var3 = 0;

               while (var2 != null && !var2.isAccept() && var3 <= 100) {
                  if (var1.current + var3 >= Parser.this._input.length()) {
                     return false;
                  }

                  var2 = var2.step(Parser.this._input.charAt(var1.current + var3++));
               }

               return var2 != null;
            }

            public Boolean visitStringTerminalEntity(StringTerminalEntity var1x) {
               String var2 = var1x.getString();
               int var3 = var2.length();
               return var1.current + var3 <= Parser.this._input.length() && Parser.this._input.substring(var1.current, var1.current + var3).equals(var2);
            }

            public Boolean visitEOFTerminalEntity(EOFTerminalEntity var1x) {
               return var1.current == Parser.this._input.length();
            }
         }
      );
   }

   private void handlePendingCompletes(PState var1) throws ParseException {
      if (!var1.isDone()) {
         if (var1.prod.isUnordered()) {
            for (Entity var3 : var1.getRemaining()) {
               this.handlePendingCompletes(var1, var3);
            }
         } else {
            this.handlePendingCompletes(var1, var1.getNextRemaining());
         }
      }
   }

   private void handlePendingCompletes(final PState var1, Entity var2) throws ParseException {
      final ArrayList var3 = new ArrayList();
      var2.visitBy(new VoidEntityVisitor() {
         @Override
         public void visitNonterminal(NonterminalEntity var1x) {
            Object var2 = Parser.this.ntmap.get(var1.current);
            if (var2 == null) {
               var2 = new HashMap();
               Parser.this.ntmap.put(var1.current, (Map<String, List<PState>>)var2);
            }

            Object var3x = (List)((Map<String, List<PState>>)var2).get(var1x.getNonterminal());
            if (var3x == null) {
               var3x = new ArrayList();
               ((Map<String, List<PState>>)var2).put(var1x.getNonterminal(), (List)var3x);
            }

            ((List<PState>)var3x).add(var1);
            Map var4 = Parser.this.complmap.get(var1.current);
            if (var4 != null) {
               List var5 = (List)var4.get(var1x.getNonterminal());
               if (var5 != null) {
                  var3.addAll(var5);
               }
            }
         }
      });

      for (var var5 : var3) {
         this.complete((PState)var5, var1, (NonterminalEntity)var2);
      }
   }

   public long getMaxMemory() {
      return this.max_memory;
   }

   public long getMaxStates() {
      return this.max_states;
   }

   public long getTotalStates() {
      return this.total_states;
   }
}
