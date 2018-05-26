/*
File:   AutoDefinitionFinder.java 
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
