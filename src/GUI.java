
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * @author Kevin
 */
public class GUI extends JFrame implements ActionListener{

    JTextArea ta;
    JTabbedPane tp;

    public GUI() {

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
        }

        setTitle("Automatic Definition Finder");
        setResizable(false);
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        setLayout(new BorderLayout());
        tp = new JTabbedPane();
        
        JPanel bottomRow = new JPanel(new FlowLayout());
        
        JButton search = new JButton("Find Definitions");
        JComboBox defType = new JComboBox();
        defType.addItem("Dictionary.com");
        defType.addItem("Merriam-Webster");
        defType.addItem("Google");        
        
        bottomRow.add(defType);
        bottomRow.add(search);
        
        add(tp, BorderLayout.CENTER);
        add(bottomRow, BorderLayout.SOUTH);

        InitTerms();
        InitDefs();
    }

    public void InitTerms() {
        JPanel termPanel = new JPanel();
        termPanel.setLayout(new BorderLayout());
        
        tp.addTab("Term List ", termPanel);
        
        ta = new JTextArea(20, 90);
        JScrollPane sp = new JScrollPane(ta);
        
        JLabel infoLbl = new JLabel("Enter one-word English terms below, separated by a single line.");
                
        ((AbstractDocument) ta.getDocument()).setDocumentFilter(new TermAreaFilter());
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        ta.setVisible(true);        
        
        
        termPanel.add(infoLbl, BorderLayout.NORTH);
        termPanel.add(sp, BorderLayout.CENTER);
        
        pack();

    }
    
    public void InitDefs() {
        JPanel defPanel = new JPanel(new BorderLayout());
        
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JLabel sepText = new JLabel("Separating character: ");
        JTextField sep = new JTextField(":");
        
        JLabel repText = new JLabel("Let term be in definition:");
        JCheckBox replace = new JCheckBox();
        
        sep.setColumns(3);
        
        tp.addTab("Terms with Definitions", defPanel);
        
        ta = new JTextArea(20, 90);
        JScrollPane sp = new JScrollPane(ta);
        ta.setEditable(false);
                        
        ((AbstractDocument) ta.getDocument()).setDocumentFilter(new TermAreaFilter());
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        ta.setVisible(true);        
        
        topRow.add(sepText);
        topRow.add(sep);
        topRow.add(Box.createHorizontalStrut(10));
        topRow.add(repText);
        topRow.add(replace);
        
        defPanel.add(topRow, BorderLayout.NORTH);
        defPanel.add(sp, BorderLayout.CENTER);
        
        pack();

    }
    
    
    public void actionPerformed(ActionEvent e){
        System.out.println(((JComponent) e.getSource()).getClass().toString());
        
        
    }
    
    
    //Restricts Text Area to only Alphabetic characters.
    class TermAreaFilter extends DocumentFilter {

        @Override
        public void replace(FilterBypass fb, int i, int i1, String string, AttributeSet as) throws BadLocationException {
            for (int n = string.length(); n > 0; n--) {
                char c = string.charAt(n - 1);

                if (Character.isAlphabetic(c) || c == '\n'); 
                    super.replace(fb, i, i1, String.valueOf(c), as);
                
                
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
