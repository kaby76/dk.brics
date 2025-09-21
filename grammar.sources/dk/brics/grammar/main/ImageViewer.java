package dk.brics.grammar.main;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ImageViewer {
   private ImageViewer() {
   }

   public static boolean open(String var0, String var1) {
      try {
         new ImageViewer.ImageFrame(var0, ImageIO.read(new File(var1)), var1).setVisible(true);
         return true;
      } catch (IOException var3) {
         return false;
      }
   }

   static class ImageFrame extends JFrame {
      Image img;
      String path;
      int width;
      int height;

      ImageFrame(String var1, Image var2, String var3) {
         super(var1);
         this.img = var2;
         this.path = var3;
         this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), 1));
         ImageViewer.ImageFrame.ImagePanel var4 = new ImageViewer.ImageFrame.ImagePanel();
         this.width = var2.getWidth(null);
         this.height = var2.getHeight(null);
         var4.setPreferredSize(new Dimension(this.width, this.height));
         JScrollPane var5 = new JScrollPane(var4);
         var5.setVerticalScrollBarPolicy(20);
         var5.setHorizontalScrollBarPolicy(30);
         var5.setPreferredSize(new Dimension(400, 400));
         this.add(var5);
         this.pack();
      }

      @Override
      public void setVisible(boolean var1) {
         if (var1) {
            Dimension var2 = Toolkit.getDefaultToolkit().getScreenSize();
            this.setLocation((var2.width - this.getWidth()) / 2, (var2.height - this.getHeight()) / 2);
         }

         super.setVisible(var1);
      }

      class ImagePanel extends JPanel {
         @Override
         public void paint(Graphics var1) {
            super.paint(var1);
            var1.drawImage(ImageFrame.this.img, 0, 0, ImageFrame.this.width, ImageFrame.this.height, this);
         }
      }
   }
}
