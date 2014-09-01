package pubsub.experiments.channel.subscriber;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author John Gasparis
 */
public class ChannelFileSubParser {

    public static void main(String args[]) throws FileNotFoundException, IOException, Exception {
        String directory = "/home/gaspar/logs/fifth/temp";
        File dir = new File(directory);
        File[] files = dir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".log");
            }
        });
        File newDir = new File(directory + "/parsed");
        if (!newDir.mkdir()) {
            throw new Exception("Directory was not created");
        }

        Scanner scanner;
        String line;
        int index;
        int seq;
        int minSeq = Integer.MAX_VALUE;
        int maxSeq = -1;
        int prevMinSeq = minSeq;
        int totalPackets = 0;
        int len = "[SEQ] => ".length();
        int len2 = "Statistics: totalPackets [".length();
        int wrong = 0;
        PrintWriter pw;
        int i = 0;
        int sum = 0;
        boolean firstParsing = true;
        Set<Integer> duplicates = new HashSet<Integer>();
        int dup;

        for (i = 0; i < files.length; i++) {
            System.out.println("\n" + files[i].getName());
            scanner = new Scanner(files[i]);
            pw = new PrintWriter(new FileWriter(new File(newDir.getPath() + "/" + files[i].getName() + ".parsed")));

            while (scanner.hasNextLine()) {
                line = scanner.nextLine();

                if ((index = line.indexOf("Flow establishment:")) > 0) {
                    if (!firstParsing) {
                        pw.println("Total: " + totalPackets);
                        //System.out.println(subStr.substring(0, subStr.indexOf("]")));
                        //  pw.println("MinSeq: " + minSeq);
                        // System.out.println("MaxSeq: " + maxSeq);
                        // pw.println("MaxSeq: " + maxSeq);
                        //System.out.println("MaxSeq: " + maxSeq);
                        pw.println("WrongPackets: " + wrong);

                        dup = totalPackets - duplicates.size();
                        pw.println("Duplicates: " + dup);

                        pw.println("Loss: " + (maxSeq - minSeq - totalPackets + 1 + wrong + dup));
                    }

                    pw.println(line.substring(index));
                    // System.out.println("[" + i + "] " + line.substring(index));                    

                    minSeq = Integer.MAX_VALUE;
                    maxSeq = -1;
                    prevMinSeq = minSeq;
                    wrong = 0;
                    totalPackets = 0;
                    firstParsing = false;
                    duplicates.clear();
                    //System.out.println(line.substring(index));
                }

                if ((index = line.indexOf("[SEQ] => ")) > 0) {
                    totalPackets++;
                    seq = Integer.parseInt(line.substring(index + len));
                    duplicates.add(seq);
                    if (seq < minSeq) {
                        if (seq == 0) {
                            if (prevMinSeq == Integer.MAX_VALUE) {
                                minSeq = seq;
                            } else {
                                wrong++;
                            }
                        } else {
                            prevMinSeq = minSeq;
                            minSeq = seq;
                        }
                    }

                    if (seq > maxSeq) {
                        maxSeq = seq;
                    }
                }

                //if ((index = line.indexOf("Statistics: totalPackets [")) > 0) {
                // String subStr = line.substring(index + len2);

                //  totalPackets = Integer.parseInt(subStr.substring(0, subStr.indexOf("]")));
                // System.out.println("[" + i + "] " + sum);

                //System.out.println("Diff: " + (maxSeq - minSeq));


                //}

            }
            firstParsing = true;
            
            pw.println("Total: " + totalPackets);
            pw.println("WrongPackets: " + wrong);

            dup = totalPackets - duplicates.size();
            pw.println("Duplicates: " + dup);

            pw.println("Loss: " + (maxSeq - minSeq - totalPackets + 1 + wrong + dup));

            pw.close();
        }
    }
}
