
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.Box;
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

/**
 * @author Kevin
 */
public class GUI extends JFrame {

    JTextArea terms;
    JTextArea defs;
    JTabbedPane tp;
    JCheckBox replace;
    JTextField sep;
    JComboBox defType;

    String definitionList = "";

    public GUI() {

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
        }

        setTitle("Automatic Definition Finder");
        setResizable(false);
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        tp = new JTabbedPane();

        JPanel bottomRow = new JPanel(new FlowLayout());

        JButton search = new JButton("Find Definitions");

        search.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                defs.setText("Searching for definitions...");
                defs.update(defs.getGraphics());
                
                for (String term : terms.getText().split("\n")) {
                    if (term.replaceAll("[^A-Za-z]", "").trim().length() == term.trim().length() && term.trim().length() > 1) {
                        try {
                            String line = term + sep.getText() + " " + AutoDefinitionFinder.GetDef(term, (String) defType.getSelectedItem()) + "\n";

                            if (defs.getText().equals("Searching for definitions...")) {
                                defs.setText("");
                            }

                            defs.append(line);

                            defs.update(defs.getGraphics());
                        } catch (IOException ex) {
                            System.out.println(ex.toString());
                        }

                    } else {
                        defs.append(term + sep.getText() + " Invalid term text format. Make sure it is only Alphabetic and one-word." + "\n");
                    }
                }

                definitionList = defs.getText();

                CheckSettings();
            }
        });

        defType = new JComboBox();

        defType.addItem("Dictionary.com");
        defType.addItem("Merriam-Webster");
        defType.addItem("Google");

        bottomRow.add(defType);
        bottomRow.add(Box.createHorizontalStrut(440));
        bottomRow.add(search);

        add(tp, BorderLayout.CENTER);
        add(bottomRow, BorderLayout.SOUTH);

        TermTab();
        DefTab();

        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/iconTransparent.png")));

        setVisible(true);
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

        terms.setVisible(false);
        terms.setVisible(true);
    }

    public void TermTab() {
        JPanel termPanel = new JPanel();
        termPanel.setLayout(new BorderLayout());

        tp.addTab("Term List ", termPanel);

        terms = new JTextArea(20, 90);
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

        sep.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CheckSettings();
            }
        });

        JLabel repText = new JLabel("Let term be in definition:");
        replace = new JCheckBox();

        replace.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CheckSettings();
            }
        });

        sep.setColumns(3);

        defs = new JTextArea(20, 90);
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
