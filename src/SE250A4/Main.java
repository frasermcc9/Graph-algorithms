package SE250A4;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main {

    public final static boolean VERBOSE = true;

    public static void main(String[] args) {
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

            files.forEach(el -> {
                try {
                    // Reads data and puts into usable form
                    Util.printSeparator("Processing tree: " + el.getName());
                    var data = Util.read(el.getPath());
                    var nodeNumber = data.get(0).matches("^\\d+$") ? Integer.parseInt(data.get(0)) : 0;
                    var linkData = data.subList(1, data.size());

                    Util.processGraph(new Graph(nodeNumber, linkData));

                } catch (Exception e) {
                    System.out.println("Error in reading graph");
                }
            });

            System.out.println(
                    "Did you know: you can shift-select in the file chooser to select multiple files and test them all at once.");

        } catch (Exception e) {
            System.out.println("Invalid file selection");
        }
    }

}