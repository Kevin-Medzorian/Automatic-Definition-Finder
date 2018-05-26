/*
File:   GUI.java 
Copyright 2018, Kevin Medzorian

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and 
to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of 
the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO 
THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.Scanner;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public final class GUI extends JFrame {

    JTextArea terms;
    JTextArea defs;
    JTabbedPane tp;
    JCheckBox replace;
    JTextField sep;
    JComboBox defType;
    JLabel loading;
    GUI thisObject;

    String definitionList = "";

    public GUI() {
        thisObject = this;

        String os = new Scanner(System.getProperty("os.name")).next().toLowerCase();

        try {
            if (os.equals("windows")) {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } else if (os.equals("linux")) {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            }
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
        }

        setTitle("Automatic Definition Finder");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        tp = new JTabbedPane();

        JPanel bottomRow = new JPanel(new BorderLayout());
        JPanel west = new JPanel(new FlowLayout());
        JPanel east = new JPanel(new FlowLayout());

        loading = new JLabel(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/images/loader.gif")).getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT)));

        JButton search = new JButton("Find Definitions");

        search.addActionListener((ActionEvent e) -> {
            defs.setText("Searching for definitions...");

            if (tp.getSelectedIndex() == 1) {
                defs.update(defs.getGraphics());
            }

            //Finds definitions in a new thread
            Thread findDefinitions = new Thread(new ThreadedConnection((String) defType.getSelectedItem(), terms.getText().split("\n"), thisObject));
            findDefinitions.start();
        });

        defType = new JComboBox();

        defType.addItem("Dictionary.com");
        defType.addItem("Merriam-Webster");
        defType.addItem("Google");

        west.add(defType);
        east.add(loading);
        east.add(search);

        bottomRow.add(west, BorderLayout.WEST);
        bottomRow.add(east, BorderLayout.EAST);

        add(tp, BorderLayout.CENTER);
        add(bottomRow, BorderLayout.SOUTH);

        TermTab();
        DefTab();

        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/iconTransparent.png")));

        setVisible(true);

        loading.setVisible(false);
    }

    public void CheckSettings() {

        String text = "";

        for (String line : definitionList.split("\n")) {

            String term = line.substring(0, line.indexOf(" ") - 1);

            line = line.substring(line.indexOf(" ") + 1);

            if ((line.replaceAll("^[a-z]", " ").contains(term + " ") || line.replaceAll("^[a-z]", " ").contains(" " + term)) && !replace.isSelected()) {
                line = line.replaceAll(term + " ", "____________ ");
                line = line.replaceAll(" " + term, " ____________");
            }

            if ((line.replaceAll("^[a-z]", " ").contains(term.toLowerCase() + " ") || line.replaceAll("^[a-z]", " ").contains(" " + term.toLowerCase())) && !replace.isSelected()) {
                line = line.replaceAll(term.toLowerCase() + " ", "____________ ");
                line = line.replaceAll(" " + term.toLowerCase(), " ____________");
            }

            //Plural occurences with S
            if ((line.replaceAll("^[a-z]", " ").contains(term + "s ") || line.replaceAll("^[a-z]", " ").contains(" " + term + "s")) && !replace.isSelected()) {
                line = line.replaceAll(term + " ", "____________ ");
                line = line.replaceAll(" " + term, " ____________");
            }

            if ((line.replaceAll("^[a-z]", " ").contains(term.toLowerCase() + "s ") || line.replaceAll("^[a-z]", " ").contains(" " + term.toLowerCase() + "s")) && !replace.isSelected()) {
                line = line.replaceAll(term.toLowerCase() + "s ", "____________ ");
                line = line.replaceAll(" " + term.toLowerCase() + "s", " ____________");
            }

            //Plural occurence with ies
            String term2 = term.substring(0, term.length() - 1);
            if ((line.replaceAll("^[a-z]", " ").contains(term2 + "ies ") || line.replaceAll("^[a-z]", " ").contains(" " + term2 + "ies")) && !replace.isSelected()) {
                line = line.replaceAll(term + " ", "____________ ");
                line = line.replaceAll(" " + term, " ____________");
            }

            if ((line.replaceAll("^[a-z]", " ").contains(term2.toLowerCase() + "ies ") || line.replaceAll("^[a-z]", " ").contains(" " + term2.toLowerCase() + "ies")) && !replace.isSelected()) {
                line = line.replaceAll(term2.toLowerCase() + "ies ", "____________ ");
                line = line.replaceAll(" " + term2.toLowerCase() + "ies", " ____________");
            }

            text += term + sep.getText() + " " + line + "\n";
        }

        defs.setText(text);

    }

    public void TermTab() {
        JPanel termPanel = new JPanel();
        termPanel.setLayout(new BorderLayout());

        tp.addTab("Term List ", termPanel);

        terms = new JTextArea(20, 80);
        JScrollPane spt = new JScrollPane(terms);

        JLabel infoLbl = new JLabel("Enter one-word English terms below, separated by a single line.");

        ((AbstractDocument) terms.getDocument()).setDocumentFilter(new TermAreaFilter());
        spt.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        spt.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        terms.setVisible(true);

        termPanel.add(infoLbl, BorderLayout.NORTH);
        termPanel.add(spt, BorderLayout.CENTER);

        pack();
    }

    public void DefTab() {
        JPanel defPanel = new JPanel(new BorderLayout());
        tp.addTab("Terms with Definitions", defPanel);
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel sepText = new JLabel("Separating text: ");
        sep = new JTextField(":");

        sep.addActionListener((ActionEvent e) -> {
            CheckSettings();
        });

        JLabel repText = new JLabel("Let term be in definition:");
        replace = new JCheckBox();

        replace.addActionListener((ActionEvent e) -> {
            CheckSettings();
        });

        sep.setColumns(2);
        defs = new JTextArea(20, 80);
        JScrollPane sp = new JScrollPane(defs);
        defs.setEditable(false);

        defs.setVisible(true);

        topRow.add(sepText);
        topRow.add(sep);
        topRow.add(Box.createHorizontalStrut(10));
        topRow.add(repText);
        topRow.add(replace);

        defPanel.add(topRow, BorderLayout.NORTH);
        defPanel.add(sp, BorderLayout.CENTER);

        pack();
    }

    //Restricts Text Area to only Alphabetic characters.
    class TermAreaFilter extends DocumentFilter {

        @Override
        public void replace(FilterBypass fb, int i, int i1, String string, AttributeSet as) throws BadLocationException {
            for (int n = string.length(); n > 0; n--) {
                char c = string.charAt(n - 1);

                if (Character.isAlphabetic(c) || c == '\n') {
                    super.replace(fb, i, i1, String.valueOf(c), as);
                }

            }
        }

        @Override
        public void remove(FilterBypass fb, int i, int i1) throws BadLocationException {
            super.remove(fb, i, i1);
        }

        @Override
        public void insertString(FilterBypass fb, int i, String string, AttributeSet as) throws BadLocationException {
            super.insertString(fb, i, string, as);
        }

    }
}
