package SE250A4;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main {

    public static void main(String[] args) {

        List<String> output = new ArrayList<>();

        try {
            // setting up the file dialog
            final JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
            fc.setDialogTitle("Please select input graph(s)");
            fc.setFileFilter(filter);
            fc.setMultiSelectionEnabled(true);
            fc.showOpenDialog(fc);

            // stores all selected files
            List<File> files = new ArrayList<>(Arrays.asList((fc.getSelectedFiles())));

            //for each file
            files.forEach(el -> {
                try {
                    // Reads data and puts into usable form
                    Util.printSeparator("Processing tree: " + el.getName(), output);
                    var data = Util.read(el.getPath());
                    var nodeNumber = data.get(0).matches("^\\d+$") ? Integer.parseInt(data.get(0)) : 0;
                    var linkData = data.subList(1, data.size());

                    //process the graph
                    Util.processGraph(new Graph(nodeNumber, linkData), output);

                } catch (Exception e) {
                    System.out.println("Error in reading graph");
                }
            });

            //add helpful tip if multiple selections weren't done
            if (files.size() < 2) {
                Util.printSeparator("Did you know?", output);
                output.add("You can shift-select in the file chooser to select multiple files and test them all at once.");
            }

            //write the file
            FileWriter fw = new FileWriter("output.txt");
            for (String str : output) {
                fw.write(str + System.lineSeparator());
            }
            fw.close();

        } catch (Exception e) {
            System.out.println("Invalid file selection");
        }
    }

}