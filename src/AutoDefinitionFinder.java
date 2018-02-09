
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


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
            @Override
            public void run() {
                WriteLogFile();
            }
        });

        GUI gui = new GUI();
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

    public static void Log(String log) {

        LOG.add(getCurrentTimeStamp() + "\t" + log + "\n");

        System.out.println(log);

    }

    public static String getCurrentTimeStamp() {

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

    }

}
