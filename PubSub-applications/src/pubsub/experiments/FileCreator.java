package pubsub.experiments;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 *
 * @author John Gasparis
 */
public class FileCreator {

    private String path;
    private Random rand;
    byte[] buffer;
    private final int BUFSIZE = 1024;
    private List<String> fileNames;

    public FileCreator(String path) {
        this.path = path;
        if (!path.endsWith(System.getProperty("file.separator"))) {
            this.path += System.getProperty("file.separator");
        }
        this.rand = new Random(System.currentTimeMillis());
        this.buffer = new byte[BUFSIZE];
        this.fileNames = new ArrayList<String>();
    }

    public void createFile(int fileSize) throws FileNotFoundException, IOException {
        String fileName = UUID.randomUUID().toString();
        int length;
        BufferedOutputStream bufOut;

        fileName = fileName.replaceAll("-", "");
        fileName = path + fileName;
        bufOut = new BufferedOutputStream(new FileOutputStream(fileName));

        fileNames.add(fileName);

        try {
            for (int i = 0; i < fileSize; i += BUFSIZE) {
                rand.nextBytes(buffer);

                length = i + BUFSIZE > fileSize ? fileSize - i : BUFSIZE;
                bufOut.write(buffer, 0, length);
            }
        } finally {
            bufOut.close();
        }
    }

    public void createTxtFile(String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

        for (String str : fileNames) {
            writer.write(str);
            writer.newLine();
        }

        writer.close();
    }
}
