
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author Kevin
 */
public class AutoDefinitionFinder {

    public final static String TERM_PATH = new File(AutoDefinitionFinder.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile().getPath() + "/config";

    static File termFile = new File(TERM_PATH + "/terms.txt"),
            outputFile = new File(TERM_PATH + "/output.txt"),
            logFile = new File(TERM_PATH + "/log.txt");

    static ArrayList<String> LOG = new ArrayList();

    public static void main(String[] args) throws IOException {

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                WriteLogFile();
            }
        });
        
            new GUI();
            
        if (termFile.exists()) {

            Log("Process started.");

            String[] terms = ReadTerms();

            String[] output = new String[terms.length];
            

            for (int i = 0; i < terms.length; i++) {
                output[i] = terms[i]
                        + ": "
                        + GetDef(terms[i].toLowerCase());
            }

            RewriteTerms(output);
            Log("Process completed.");
        } else {
            Log("Creating term file. Please fill in terms and re-run program.");
            CreateTermFile();
        }

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

            Files.write(Paths.get(logFile.toURI()), (getCurrentTimeStamp() + "\tClosing...").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
        }
    }

    public static void RewriteTerms(String[] lines) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));

        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }

        for (String line : lines) {
            bw.write(line);
            bw.newLine();
        }

        Log("Terms rewritten. Closing writer...");

        bw.close();
    }

    public static String GetDef(String term) throws IOException {
        Document doc = null;

        try {

            doc = Jsoup.connect("http://www.dictionary.com/browse/" + term + "?s=t").get();

        } catch (IOException e) {
            
            return "No definition found. Invalid input or network.";
            
        }
        
        
        String text = doc.select("div.def-content").text();

        return text.substring(0, text.indexOf(". "));
    }

    public static String[] ReadTerms() throws IOException {
        Scanner scan = new Scanner(termFile);

        ArrayList<String> lines = new ArrayList();

        for (String s = "-1"; scan.hasNextLine() && s.trim().length() > 0;) {
            lines.add(s = scan.nextLine());

            if (s.replaceAll("[A-Za-z]", "").length() > 0) {
                Log("FATAL: Term format error with: \"" + s + "\"");
                System.exit(0);
            }

        }

        Log("Terms read successfully.");

        return lines.toArray(new String[lines.size()]);
    }

    public static void CreateTermFile() throws IOException {
        new File(TERM_PATH).mkdirs();

        termFile.createNewFile();

        BufferedWriter bw = new BufferedWriter(new FileWriter(termFile));

        bw.write("Single-Worded-Term");
        bw.newLine();
        bw.write("Single-Worded-Term-2");
        bw.newLine();
        bw.write("etc...");

        bw.close();
        bw = null;

    }

    public static void Log(String log) {

        LOG.add(getCurrentTimeStamp() + "\t" + log + "\n");

        System.out.println(log);

    }

    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }
}
