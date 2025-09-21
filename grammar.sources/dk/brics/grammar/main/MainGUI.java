package dk.brics.grammar.main;

import dk.brics.misc.Loader;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

public class MainGUI {
   private static final String[] encodings = new String[]{
      "default",
      "utf-8 (Unicode, worldwide)",
      "utf-16 (Unicode, worldwide)",
      "iso-8859-1 (Western Europe)",
      "iso-8859-2 (Central Europe)",
      "iso-8859-3 (Southern Europe)",
      "iso-8859-4 (North European)",
      "iso-8859-5 (Cyrillic)",
      "iso-8859-6-i (Arabic)",
      "iso-8859-7 (Greek)",
      "iso-8859-8 (Hebrew, visual)",
      "iso-8859-8-i (Hebrew, logical)",
      "iso-8859-9 (Turkish)",
      "iso-8859-10 (Latin 6)",
      "iso-8859-11 (Latin/Thai)",
      "iso-8859-13 (Latin 7, Baltic Rim)",
      "iso-8859-14 (Latin 8, Celtic)",
      "iso-8859-15 (Latin 9)",
      "iso-8859-16 (Latin 10)",
      "us-ascii (basic English)",
      "euc-jp (Japanese, Unix)",
      "shift_jis (Japanese, Win/Mac)",
      "iso-2022-jp (Japanese, email)",
      "euc-kr (Korean)",
      "ksc_5601 (Korean)",
      "gb2312 (Chinese, simplified)",
      "gb18030 (Chinese, simplified)",
      "big5 (Chinese, traditional)",
      "Big5-HKSCS (Chinese, Hong Kong)",
      "tis-620 (Thai)",
      "koi8-r (Russian)",
      "koi8-u (Ukrainian)",
      "iso-ir-111 (Cyrillic KOI-8)",
      "macintosh (MacRoman)",
      "windows-1250 (Central Europe)",
      "windows-1251 (Cyrillic)",
      "windows-1252 (Western Europe)",
      "windows-1253 (Greek)",
      "windows-1254 (Turkish)",
      "windows-1255 (Hebrew)",
      "windows-1256 (Arabic)",
      "windows-1257 (Baltic Rim)"
   };

   private MainGUI() {
   }

   public static void main(String[] var0) {
      EventQueue.invokeLater(new Runnable() {
         @Override
         public void run() {
            try {
               UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
               new MainGUI.Window().setVisible(true);
            } catch (Exception var2) {
               System.err.println(var2.getMessage());
            }
         }
      });
   }

   static final class TextAreaWriter extends Writer {
      private final JTextArea textarea;

      public TextAreaWriter(JTextArea var1) {
         this.textarea = var1;
      }

      @Override
      public void flush() {
      }

      @Override
      public void close() {
      }

      @Override
      public void write(char[] var1, int var2, int var3) {
         this.textarea.append(new String(var1, var2, var3));
         this.textarea.setCaretPosition(this.textarea.getText().length());
      }
   }

   static class Window extends JFrame {
      JTextField grammarfile_textfield;
      JComboBox grammarencoding_combobox;
      JTextField textfile_textfield;
      JComboBox textencoding_combobox;
      JCheckBox verbose_checkbox;
      JCheckBox dumpast_checkbox;
      JCheckBox tokenize_checkbox;
      JTextField unfoldlevel_textfield;
      JTextField unfoldleft_textfield;
      JTextField unfoldright_textfield;
      JTextArea output_textarea;
      JFileChooser grammarfile_chooser;
      JFileChooser textfile_chooser;
      JButton check_button;
      JButton parse_button;
      JButton analyze_button;
      JButton abort_button;
      JButton graphviz_button;
      PrintWriter out;
      Process dotproc;
      boolean graphviz_aborted;

      Window() {
         JLabel var1 = new JLabel("Grammar URL or file:");
         this.grammarfile_textfield = new JTextField(40);
         JButton var2 = new JButton("Open file...");
         JLabel var3 = new JLabel("Encoding:");
         this.grammarencoding_combobox = new JComboBox<>(MainGUI.encodings);
         JLabel var4 = new JLabel("Text URL or file:");
         this.textfile_textfield = new JTextField(40);
         JButton var5 = new JButton("Open file...");
         JLabel var6 = new JLabel("Encoding:");
         this.textencoding_combobox = new JComboBox<>(MainGUI.encodings);
         this.check_button = new JButton("Check grammar");
         this.parse_button = new JButton("Parse text");
         this.analyze_button = new JButton("Analyze grammar ambiguity");
         this.abort_button = new JButton("Abort");
         this.graphviz_button = new JButton("Run Graphviz");
         JButton var7 = new JButton("About");
         JButton var8 = new JButton("Exit");
         this.verbose_checkbox = new JCheckBox("Verbose");
         this.dumpast_checkbox = new JCheckBox("Dump AST");
         this.tokenize_checkbox = new JCheckBox("Tokenize grammar");
         JLabel var9 = new JLabel("Unfold level:");
         this.unfoldlevel_textfield = new JTextField(1);
         this.unfoldlevel_textfield.setText("0");
         JLabel var10 = new JLabel("Left parentheses:");
         this.unfoldleft_textfield = new JTextField(3);
         JLabel var11 = new JLabel("Right parentheses:");
         this.unfoldright_textfield = new JTextField(3);
         this.output_textarea = new JTextArea();
         this.output_textarea.setEditable(false);
         this.grammarfile_chooser = new JFileChooser();
         this.grammarfile_chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File var1) {
               return var1.isDirectory() || var1.getName().endsWith(".cfg");
            }

            @Override
            public String getDescription() {
               return "grammar files (.cfg)";
            }
         });
         this.textfile_chooser = new JFileChooser();
         var1.setLabelFor(this.grammarfile_textfield);
         var3.setLabelFor(this.grammarencoding_combobox);
         var4.setLabelFor(this.textfile_textfield);
         var6.setLabelFor(this.textencoding_combobox);
         JPanel var12 = new JPanel();
         var12.setLayout(new GridBagLayout());
         GridBagConstraints var13 = new GridBagConstraints();
         var13.fill = 0;
         var13.gridx = 0;
         var13.gridy = 0;
         var13.anchor = 17;
         var12.add(var1, var13);
         var13.fill = 2;
         var13.gridx = 1;
         var13.weightx = 1.0;
         var12.add(this.grammarfile_textfield, var13);
         var13.fill = 0;
         var13.gridx = 2;
         var13.weightx = 0.0;
         var12.add(var2, var13);
         var13.fill = 0;
         var13.gridx = 3;
         var13.insets = new Insets(0, 20, 0, 0);
         var12.add(var3, var13);
         var13.fill = 0;
         var13.gridx = 4;
         var13.insets = new Insets(0, 0, 0, 0);
         var12.add(this.grammarencoding_combobox, var13);
         var13.fill = 0;
         var13.gridx = 0;
         var13.gridy = 1;
         var12.add(var4, var13);
         var13.fill = 2;
         var13.gridx = 1;
         var13.weightx = 1.0;
         var12.add(this.textfile_textfield, var13);
         var13.fill = 0;
         var13.gridx = 2;
         var13.weightx = 0.0;
         var12.add(var5, var13);
         var13.fill = 0;
         var13.gridx = 3;
         var13.insets = new Insets(0, 20, 0, 0);
         var12.add(var6, var13);
         var13.fill = 0;
         var13.gridx = 4;
         var13.insets = new Insets(0, 0, 0, 0);
         var12.add(this.textencoding_combobox, var13);
         var12.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Input files"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
         JPanel var14 = new JPanel();
         var14.setLayout(new GridBagLayout());
         var13.fill = 0;
         var13.gridx = 0;
         var13.gridy = 0;
         var13.insets = new Insets(0, 0, 0, 0);
         var14.add(this.verbose_checkbox, var13);
         var13.gridx = 1;
         var14.add(this.dumpast_checkbox, var13);
         var13.gridx = 2;
         var13.insets = new Insets(0, 0, 0, 30);
         var14.add(this.tokenize_checkbox, var13);
         var13.gridx = 3;
         var13.insets = new Insets(0, 0, 0, 0);
         var14.add(var9, var13);
         var13.gridx = 4;
         var13.insets = new Insets(0, 0, 0, 10);
         var14.add(this.unfoldlevel_textfield, var13);
         var13.gridx = 5;
         var13.insets = new Insets(0, 0, 0, 0);
         var14.add(var10, var13);
         var13.gridx = 6;
         var13.insets = new Insets(0, 0, 0, 10);
         var14.add(this.unfoldleft_textfield, var13);
         var13.gridx = 7;
         var13.insets = new Insets(0, 0, 0, 0);
         var14.add(var11, var13);
         var13.gridx = 8;
         var14.add(this.unfoldright_textfield, var13);
         var14.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
         JPanel var15 = new JPanel();
         var15.setLayout(new GridBagLayout());
         var13.anchor = 10;
         var13.gridx = 0;
         var15.add(this.check_button, var13);
         var13.gridx = 1;
         var13.insets = new Insets(0, 5, 0, 0);
         var15.add(this.parse_button, var13);
         var13.gridx = 2;
         var15.add(this.analyze_button, var13);
         var13.insets = new Insets(0, 0, 0, 0);
         var15.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
         JPanel var16 = new JPanel();
         var16.setLayout(new GridBagLayout());
         var13.anchor = 10;
         var13.gridx = 0;
         var16.add(this.abort_button, var13);
         var13.gridx = 1;
         var13.insets = new Insets(0, 40, 0, 0);
         var16.add(this.graphviz_button, var13);
         var13.gridx = 2;
         var13.insets = new Insets(0, 50, 0, 0);
         var16.add(var7, var13);
         var13.gridx = 3;
         var13.insets = new Insets(0, 20, 0, 0);
         var16.add(var8, var13);
         var13.insets = new Insets(0, 0, 0, 0);
         var16.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
         this.abort_button.setEnabled(false);
         this.graphviz_button.setEnabled(false);
         JScrollPane var17 = new JScrollPane(this.output_textarea);
         var17.setVerticalScrollBarPolicy(22);
         var17.setPreferredSize(new Dimension(400, 250));
         var17.setBorder(
            BorderFactory.createCompoundBorder(
               BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Output"), BorderFactory.createEmptyBorder(5, 5, 5, 5)), var17.getBorder()
            )
         );
         this.out = new PrintWriter(new MainGUI.TextAreaWriter(this.output_textarea));
         this.setTitle("dk.brics.grammar");
         this.setMinimumSize(new Dimension(700, 300));
         this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), 1));
         this.add(var12);
         this.add(var14);
         this.add(var15);
         this.add(var16);
         this.add(var17);
         var12.setMaximumSize(new Dimension(Integer.MAX_VALUE, var12.getHeight()));
         var14.setMaximumSize(new Dimension(Integer.MAX_VALUE, var14.getHeight()));
         var15.setMaximumSize(new Dimension(Integer.MAX_VALUE, var15.getHeight()));
         var16.setMaximumSize(new Dimension(Integer.MAX_VALUE, var16.getHeight()));
         this.pack();
         this.setDefaultCloseOperation(0);
         this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent var1) {
               Window.this.exit();
            }
         });
         var7.addActionListener(
            new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent var1) {
                  JOptionPane.showMessageDialog(
                     Window.this,
                     "Copyright (C) 2005-2008 Anders Møller\n\nPlease see http://www.brics.dk/grammar/ for information about this software.",
                     "dk.brics.grammar",
                     1
                  );
               }
            }
         );
         var8.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent var1) {
               Window.this.exit();
            }
         });
         var2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent var1) {
               if (Window.this.grammarfile_chooser.showOpenDialog(Window.this) == 0) {
                  Window.this.grammarfile_textfield.setText(Window.this.grammarfile_chooser.getSelectedFile().getPath());
               }
            }
         });
         var5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent var1) {
               if (Window.this.textfile_chooser.showOpenDialog(Window.this) == 0) {
                  Window.this.textfile_textfield.setText(Window.this.textfile_chooser.getSelectedFile().getPath());
               }
            }
         });
         this.check_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent var1) {
               Window.this.action("check");
            }
         });
         this.parse_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent var1) {
               Window.this.action("parse");
            }
         });
         this.analyze_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent var1) {
               Window.this.action("analyze");
            }
         });
         this.graphviz_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent var1) {
               Window.this.graphviz();
            }
         });
         this.verbose_checkbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent var1) {
               synchronized (Window.this) {
                  Window.this.dumpast_checkbox.setSelected(false);
               }
            }
         });
         this.dumpast_checkbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent var1) {
               synchronized (Window.this) {
                  Window.this.verbose_checkbox.setSelected(false);
               }
            }
         });
         this.grammarfile_textfield.setToolTipText("location of the context-free grammar");
         var1.setToolTipText(this.grammarfile_textfield.getToolTipText());
         var2.setToolTipText("select a grammar file");
         this.textfile_textfield.setToolTipText("location of the text file to parse");
         var4.setToolTipText(this.textfile_textfield.getToolTipText());
         var5.setToolTipText("select a text file to parse");
         this.grammarencoding_combobox.setToolTipText("character encoding of grammar file");
         var3.setToolTipText(this.grammarencoding_combobox.getToolTipText());
         this.textencoding_combobox.setToolTipText("character encoding of text file");
         var6.setToolTipText(this.textencoding_combobox.getToolTipText());
         this.verbose_checkbox.setToolTipText("if checked, verbose output is generated");
         this.dumpast_checkbox.setToolTipText("if checked, the abstract syntax tree is output after successful parsing");
         this.tokenize_checkbox.setToolTipText("if checked, the ambiguity analyzer may assume that all terminals are maximal match tokens");
         this.unfoldlevel_textfield.setToolTipText("number of times to unfold grammar (default 0)");
         var9.setToolTipText(this.unfoldlevel_textfield.getToolTipText());
         this.unfoldleft_textfield.setToolTipText("left parenthesis symbol for unfolding");
         var10.setToolTipText(this.unfoldleft_textfield.getToolTipText());
         this.unfoldright_textfield.setToolTipText("right parenthesis symbol for unfolding");
         var11.setToolTipText(this.unfoldright_textfield.getToolTipText());
         this.check_button.setToolTipText("check that the grammar is syntactically correct");
         this.parse_button.setToolTipText("try to parse the text according to the grammar");
         this.analyze_button.setToolTipText("analyze the grammar for ambiguity");
         this.abort_button.setToolTipText("abort current action");
         this.graphviz_button.setToolTipText("run Graphviz to show the AST graphically (after parsing with 'Dump AST' enabled')");
         var7.setToolTipText("information about this tool");
      }

      @Override
      public void setVisible(boolean var1) {
         if (var1) {
            Dimension var2 = Toolkit.getDefaultToolkit().getScreenSize();
            this.setLocation((var2.width - this.getWidth()) / 2, (var2.height - this.getHeight()) / 2);
         }

         super.setVisible(var1);
      }

      private synchronized void action(final String var1) {
         (new SwingWorker<Void, Void>() {
               Thread worker;

               protected Void doInBackground() throws Exception {
                  try {
                     try {
                        this.worker = Thread.currentThread();
                        Window.this.abort_button.addActionListener(new ActionListener() {
                           @Override
                           public void actionPerformed(ActionEvent var1x) {
                              //worker.stop(new TimeoutException());
                           }
                        });
                        Window.this.abort_button.setEnabled(true);
                        Window.this.check_button.setEnabled(false);
                        Window.this.parse_button.setEnabled(false);
                        Window.this.analyze_button.setEnabled(false);
                        String var2;
                        String var1x = var2 = Charset.defaultCharset().name();
                        String var3 = (String)Window.this.grammarencoding_combobox.getSelectedItem();
                        if (!var3.equals("default")) {
                           var1x = var3.substring(0, var3.indexOf(32));
                        }

                        String var4 = (String)Window.this.textencoding_combobox.getSelectedItem();
                        if (!var4.equals("default")) {
                           var2 = var4.substring(0, var4.indexOf(32));
                        }

                        String var6 = null;

                        String var5;
                        try {
                           String var7 = Window.this.grammarfile_textfield.getText();
                           if (var7.trim().length() == 0) {
                              Window.this.fail("No grammar specified!");
                              return null;
                           }

                           var5 = Loader.getString(var7, var1x);
                        } catch (IOException var21) {
                           Window.this.fail("Unable to load grammar: " + var21.getMessage());
                           return null;
                        }

                        if (var1.equals("parse")) {
                           try {
                              String var30 = Window.this.textfile_textfield.getText();
                              if (var30.trim().length() == 0) {
                                 Window.this.fail("No text file specified!");
                                 return null;
                              }

                              var6 = Loader.getString(var30, var2);
                           } catch (IOException var22) {
                              Window.this.fail("Unable to load text: " + var22.getMessage());
                              return null;
                           }
                        }

                        int var31 = 0;
                        if (var1.equals("analyze")) {
                           try {
                              var31 = Integer.parseInt(Window.this.unfoldlevel_textfield.getText());
                           } catch (NumberFormatException var20) {
                              var31 = -1;
                           }
                        }

                        if (var31 < 0) {
                           Window.this.fail("Invalid unfold level!");
                           return null;
                        }

                        Window.this.output_textarea.setText("");
                        Window.this.graphviz_button.setEnabled(false);
                        Main.run(
                           var5,
                           Window.this.grammarfile_textfield.getText(),
                           var6,
                           var6 != null ? Window.this.textfile_textfield.getText() : null,
                           var1.equals("analyze"),
                           var31,
                           Window.this.unfoldleft_textfield.getText().trim(),
                           Window.this.unfoldright_textfield.getText().trim(),
                           Window.this.verbose_checkbox.isSelected(),
                           Window.this.dumpast_checkbox.isSelected(),
                           Window.this.tokenize_checkbox.isSelected(),
                           false,
                           Window.this.out
                        );
                        Window.this.abort_button.setEnabled(false);
                        String var33 = Window.this.output_textarea.getText();
                        if (var33.indexOf("digraph AST {") != -1 && var33.endsWith("}" + System.getProperty("line.separator"))) {
                           Window.this.graphviz_button.setEnabled(true);
                           return null;
                        }
                     } catch (TimeoutException var23) {
                        Window.this.out.println("- execution aborted!");
                     } catch (IllegalArgumentException var24) {
                        Window.this.fail(var24.getMessage());
                     } catch (InstantiationException var25) {
                        Window.this.fail(var25.getMessage());
                     } catch (IllegalAccessException var26) {
                        Window.this.fail(var26.getMessage());
                     } catch (ClassNotFoundException var27) {
                        Window.this.fail(var27.getMessage());
                     } catch (Exception var28) {
                        Window.this.fail(var28.toString());
                     }

                     return null;
                  } finally {
                     Window.this.abort_button.setEnabled(false);
                     Window.this.check_button.setEnabled(true);
                     Window.this.parse_button.setEnabled(true);
                     Window.this.analyze_button.setEnabled(true);
                  }
               }
            })
            .execute();
      }

      private synchronized void graphviz() {
         (new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
               try {
                  Window.this.abort_button.addActionListener(new ActionListener() {
                     @Override
                     public void actionPerformed(ActionEvent var1) {
                        if (Window.this.dotproc != null) {
                           Window.this.graphviz_aborted = true;
                           Window.this.dotproc.destroy();
                        }
                     }
                  });
                  Window.this.abort_button.setEnabled(true);
                  Window.this.check_button.setEnabled(false);
                  Window.this.parse_button.setEnabled(false);
                  Window.this.analyze_button.setEnabled(false);
                  Window.this.graphviz_button.setEnabled(false);
                  String var1 = System.getProperty("dk.brics.grammar.dot");
                  if (var1 == null) {
                     Window.this.fail("Graphviz dot not found!\nUse -Ddk.brics.grammar.dot=<path to Graphviz dot> when starting the tool.");
                     return null;
                  }

                  try {
                     File var2 = File.createTempFile("ast", ".dot");
                     var2.deleteOnExit();
                     String var3 = var2.getPath();
                     String var4 = var3.substring(0, var3.length() - 4) + ".png";
                     new File(var4).deleteOnExit();
                     FileWriter var5 = new FileWriter(var2);
                     String var6 = Window.this.output_textarea.getText();
                     int var7 = var6.indexOf("digraph AST {");
                     if (var7 < 0) {
                        var7 = 0;
                     }

                     var5.append(var6.substring(var7));
                     var5.close();
                     String[] var8 = new String[]{var1, "-Tpng", var3, "-o", var4};
                     Window.this.output_textarea.setText("executing Graphviz dot... ");
                     int var9 = -1;
                     Window.this.graphviz_aborted = false;

                     try {
                        Window.this.dotproc = Runtime.getRuntime().exec(var8);
                        var9 = Window.this.dotproc.waitFor();
                        Window.this.dotproc = null;
                     } catch (Exception var15) {
                        Window.this.out.println("failed");
                        Window.this.fail("Execution of Graphviz dot failed: " + var15.getMessage());
                     }

                     if (var9 == 0) {
                        Window.this.out.println("done");
                        Window.this.out.println("generated image file: " + var4);
                        if (!ImageViewer.open("AST", var4)) {
                           Window.this.fail("Unable to show image.");
                        }
                     } else if (Window.this.graphviz_aborted) {
                        Window.this.out.println("aborted");
                     } else {
                        Window.this.out.println("failed");
                        Window.this.fail("Execution of Graphviz dot failed: return code " + var9);
                     }

                     var2.delete();
                  } catch (IOException var16) {
                     Window.this.fail("Unable to store temporary file.");
                  }
               } finally {
                  Window.this.abort_button.setEnabled(false);
                  Window.this.check_button.setEnabled(true);
                  Window.this.parse_button.setEnabled(true);
                  Window.this.analyze_button.setEnabled(true);
               }

               return null;
            }
         }).execute();
      }

      private void fail(String var1) {
         JOptionPane.showMessageDialog(this, "Error: " + var1, "dk.brics.grammar", 0);
      }

      private void exit() {
         if (JOptionPane.showConfirmDialog(this, "You really want to quit?", "dk.brics.grammar", 0) == 0) {
            System.exit(0);
         }
      }
   }
}
