package SE250A4;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Util {
    public static List<String> read(String filepath) throws IOException {
        List<String> output = new ArrayList<String>();

        FileInputStream fis = new FileInputStream(filepath);
        Scanner sc = new Scanner(fis);

        while (sc.hasNextLine()) {
            output.add(sc.nextLine());
        }
        sc.close();
        return output;
    }

    static void printSeparator(String info) {
        System.out.println();
        System.out.println(info);
        System.out.println("----------------------------------------------------------------------------------");
    }

    static void processGraph(Graph G) {
        System.out.println(G.sameDegree());
        System.out.println(G.averageDegree());
        System.out.println(G.getCycles());
    }
}