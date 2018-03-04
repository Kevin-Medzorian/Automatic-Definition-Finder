
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * @author Kevin
 */
public class ThreadedConnection implements Runnable {

    private final String site;
    private final GUI gui;
    private final String[] terms;

    public ThreadedConnection(String s, String[] t, GUI g) {
        site = s;
        terms = t;
        gui = g;
    }

    @Override
    public void run() {
        gui.loading.setVisible(true);
        
        for (String term : terms) {
            if (term.replaceAll("[^A-Za-z]", "").trim().length() == term.trim().length() && term.trim().length() > 1) {
                GetDef(term);
            }
        }
        
        gui.definitionList = gui.defs.getText();

        gui.CheckSettings();
        
        gui.loading.setVisible(false);
    }

    public void GetDef(String term) {
        Document doc = null;
        String url = "",
                element = "";

        int defAmount = 3;

        switch (site) {
            case "Dictionary.com":
                url = "http://www.dictionary.com/browse/";
                element = "div.def-content";
                break;
            case "Merriam-Webster":
                url = "http://www.merriam-webster.com/dictionary/";
                element = "span.dt";
                defAmount = 2;
                break;
            case "Google":
                url = "http://www.google.com/search?q=define+";
                element = "div._Jig";
                defAmount = 1;
                break;
        }

        try {

            doc = Jsoup.connect(url + term).get();

        } catch (IOException e) {

            AutoDefinitionFinder.Log("IOException: No definition found. Invalid input or network.");

        }

        String text = "";

        Elements e = doc.select(element);

        //Take all definitions available up to 3 definitions.
        for (int i = 0; i < Math.min(defAmount, e.size()); i++) {
            text += (i+1) + ") " + (!site.equals("Merriam-Webster") ? e.get(i).text() : e.get(i).text().substring(1)) + ". ";
        }
        
        text = text.replaceAll("\\.", ". ").replaceAll("\\.\\.", "\\.").replaceAll("\\. \\.", "\\.").replaceAll(":", ";").replaceAll(" ;", ";").replaceAll("  ", " ");
        
        String line = term + gui.sep.getText() + " " + text + "\n";
        
        gui.defs.setText(gui.defs.getText().replaceAll("Searching for definitions...", ""));
        
        gui.defs.append(line);
        
        if((Object)term != terms[terms.length-1])
            gui.defs.append("Searching for definitions...");

        if (gui.tp.getSelectedIndex() == 1) {
            gui.defs.update(gui.defs.getGraphics());
        }
    }
}
