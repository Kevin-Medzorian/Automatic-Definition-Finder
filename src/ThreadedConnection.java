/*
File:   ThreadedConnection.java 
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

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

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
