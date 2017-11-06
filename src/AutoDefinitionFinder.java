
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

/**
 * @author Kevin
 */
public class AutoDefinitionFinder {

    public final static String TERM_PATH = new File(AutoDefinitionFinder.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile().getPath();

    static File logFile = new File(TERM_PATH + "/log.txt");

    static ArrayList<String> LOG = new ArrayList();

    public static void main(String[] args) throws IOException {
        Log("Started");
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                WriteLogFile();
            }
        });

        new GUI();
    }

    public static void WriteLogFile() {
        try {
            if (!logFile.exists()) {
                logFile.createNewFile();
                Files.write(Paths.get(logFile.toURI()), (getCurrentTimeStamp() + "\tLog file created. ").getBytes(), StandardOpenOption.APPEND);
            }

            Files.write(Paths.get(logFile.toURI()), System.getProperty("line.separator").getBytes(), StandardOpenOption.APPEND);

            for (String line : LOG) {

                line = line.replaceAll("\n", System.getProperty("line.separator"));

                Files.write(Paths.get(logFile.toURI()), line.getBytes(), StandardOpenOption.APPEND);
            }

            Files.write(Paths.get(logFile.toURI()), (getCurrentTimeStamp() + "\tExiting...").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
        }
    }


    public static String GetDef(String term, String site) throws IOException {
        Document doc = null;
        String url = "",
                element = "";

        switch (site) {
            case "Dictionary.com":
                url = "http://www.dictionary.com/browse/";
                element = "div.def-content";
                break;
            case "Merriam-Webster":
                url = "http://www.merriam-webster.com/dictionary/";
                element = "span.dt";
                break;
            case "Google":
                url = "http://www.google.com/search?q=define+";
                element = "div._Jig";
                break;
        }

        try {

            doc = Jsoup.connect(url + term).get();

        } catch (IOException e) {

            return "No definition found. Invalid input or network.";

        }

        String text = doc.select(element).text().trim();

        if (site.equals("Merriam-Webster")) {
            String line = "-1";

            for (Node c : doc.selectFirst(element).childNodes()) {
                if (c.toString().replaceAll("[<>/\"_=]", "").length() == c.toString().length() && c.toString().replaceAll("[^A-Za-z]", "").trim().length() > 5) {
                    line = c.toString().trim();
                    break;
                }
            }

            text = line;
        }

        if (text.contains(".")) {
            return text.substring(0, text.indexOf(".") + 1);
        }

        return text;
    }

    public static void Log(String log) {

        LOG.add(getCurrentTimeStamp() + "\t" + log + "\n");

        System.out.println(log);

    }

    public static String getCurrentTimeStamp() {
        
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        
    }
    
}
