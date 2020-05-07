package SE250A4;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Utility methods for the graph program.
 */
public class Util {
    /**
     * reads a file if given a path
     *
     * @param filepath the path to the file to read
     * @return Line by line string list of the file contents
     * @throws IOException if a file error occurs
     */
    public static List<String> read(String filepath) throws IOException {
        List<String> output = new ArrayList<>();

        FileInputStream fis = new FileInputStream(filepath);
        Scanner sc = new Scanner(fis);

        while (sc.hasNextLine()) {
            output.add(sc.nextLine());
        }
        sc.close();
        return output;
    }

    /**
     * Adds a title and separator to a list of strings
     *
     * @param info the title to add
     * @param list the list to add to
     */
    static void printSeparator(String info, List<String> list) {
        list.add(System.lineSeparator());
        list.add(info);
        list.add("----------------------------------------------------------------------------------");
    }

    /**
     * Processes the graph, finding information about it and adding the info to a list
     *
     * @param G    the digraph
     * @param list the list to add the information to
     */
    static void processGraph(Graph G, List<String> list) {
        list.add(G.sameDegree().toString());
        list.add(G.averageDegree().toString());
        list.add(G.getCycles());
    }
}