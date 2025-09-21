package dk.brics.grammar.ambiguity;

import dk.brics.grammar.Grammar;
import dk.brics.grammar.Production;
import java.io.PrintWriter;

public abstract class ApproximationStrategy {
   protected Grammar g;
   protected boolean debug;
   protected PrintWriter out;
   private int passed_vertical_checks;
   private int failed_vertical_checks;
   private int na_vertical_checks;
   private int passed_horizontal_checks;
   private int failed_horizontal_checks;
   private int na_horizontal_checks;
   public static final HorizontalOverlapString HORIZONTAL_NOT_APPLICABLE = new HorizontalOverlapString("?", 0, 1);
   public static final VerticalOverlapString VERTICAL_NOT_APPLICABLE = new VerticalOverlapString("?");

   abstract String getName();

   protected ApproximationStrategy() {
   }

   public final void init(Grammar var1, PrintWriter var2, boolean var3) {
      this.g = var1;
      this.out = var2;
      this.debug = var3;
      this.passed_horizontal_checks = this.failed_horizontal_checks = this.na_horizontal_checks = this.passed_vertical_checks = this.failed_vertical_checks = this.na_vertical_checks = 0;
      this.init();
   }

   protected void init() {
   }

   public final VerticalOverlapString verticalCheck(Production var1, Production var2) {
      VerticalOverlapString var3 = this.checkVerticalOverlap(var1, var2);
      if (var3 != VERTICAL_NOT_APPLICABLE) {
         if (var3 == null) {
            this.passed_vertical_checks++;
         } else {
            this.failed_vertical_checks++;
         }
      } else {
         this.na_vertical_checks++;
      }

      return var3;
   }

   public final HorizontalOverlapString horizontalCheck(Production var1, int var2) {
      HorizontalOverlapString var3 = this.checkHorizontalOverlap(var1, var2);
      if (var3 != HORIZONTAL_NOT_APPLICABLE) {
         if (var3 == null) {
            this.passed_horizontal_checks++;
         } else {
            this.failed_horizontal_checks++;
         }
      } else {
         this.na_horizontal_checks++;
      }

      return var3;
   }

   protected VerticalOverlapString checkVerticalOverlap(Production var1, Production var2) {
      return VERTICAL_NOT_APPLICABLE;
   }

   protected HorizontalOverlapString checkHorizontalOverlap(Production var1, int var2) {
      return HORIZONTAL_NOT_APPLICABLE;
   }

   protected void verticalDone() {
   }

   protected void horizontalDone() {
   }

   public void printStatistics(PrintWriter var1) {
      var1.println(
         "  "
            + this.getName()
            + ": "
            + this.passed_vertical_checks
            + " passed, "
            + this.failed_vertical_checks
            + " failed, "
            + this.na_vertical_checks
            + " n/a vertical; "
            + this.passed_horizontal_checks
            + " passed, "
            + this.failed_horizontal_checks
            + " failed, "
            + this.na_horizontal_checks
            + " n/a horizontal"
      );
   }
}
