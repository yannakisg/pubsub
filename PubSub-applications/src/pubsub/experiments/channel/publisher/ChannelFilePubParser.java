/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pubsub.experiments.channel.publisher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 *
 * @author gaspar
 */
public class ChannelFilePubParser {

    public static void main(String args[]) throws FileNotFoundException, IOException, Exception {
        String directory = "/home/gaspar/logs/fifth/unicast/publisher";
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
        int totalSub = 0;
        int totalUnSub = 0;
        float total = 0;
        int len = "Total Subscribers[sub]: ".length();
        int len2 = "Total Subscribers[unsub]: ".length();
        PrintWriter pw;
        int i = 0;
        int temp;
        int minSubscribers = Integer.MAX_VALUE;
        int maxSubscribers = -1;

        for (i = 0; i < files.length; i++) {
            System.out.println("\n" + files[i].getName());
            scanner = new Scanner(files[i]);
            pw = new PrintWriter(new FileWriter(new File(newDir.getPath() + "/" + files[i].getName() + ".parsed")));

            while (scanner.hasNextLine()) {
                line = scanner.nextLine();

                if ((index = line.indexOf("Total Subscribers[sub]: ")) > 0) {
                    temp = Integer.parseInt(line.substring(index + len));
                    if (temp > maxSubscribers) {
                        maxSubscribers = temp;
                    }
                    
                    total++;
                    totalSub += temp;
                }

                if ((index = line.indexOf("Total Subscribers[unsub]: ")) > 0) {
                    temp = Integer.parseInt(line.substring(index + len2));
                    totalUnSub += temp;
                    if (temp < minSubscribers && temp != 0) {
                        minSubscribers = temp;
                    }
                    total++;
                }
            }
            
            pw.println("Max Subscribers: " + maxSubscribers);
            pw.println("Min Subscribers: " + minSubscribers);
            pw.println("Average Subscribers: " + Math.round((totalSub + totalUnSub) / total));
            pw.close();
            
            totalSub = 0;
            totalUnSub = 0;
            total = 0;
            minSubscribers = Integer.MAX_VALUE;
            maxSubscribers = -1;
        }
    }
}
