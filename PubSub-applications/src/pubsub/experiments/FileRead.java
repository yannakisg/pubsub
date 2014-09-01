package pubsub.experiments;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author John Gasparis
 */
public class FileRead {
    private final String encoding = "UTF-8";
    private Scanner scanner;
    private List<String> records;

    public FileRead(String fileName) throws FileNotFoundException {
        this.scanner = new Scanner(new FileInputStream(fileName), encoding);
        this.records = new ArrayList<String>();
    }

    public void read() {
        while (scanner.hasNextLine()) {
            records.add(scanner.nextLine());
        }
    }

    public List<String> getRecords() {
        return this.records;
    }
}
