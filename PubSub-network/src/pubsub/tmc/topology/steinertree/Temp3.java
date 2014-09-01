/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pubsub.tmc.topology.steinertree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 *
 * @author gaspar
 */
public class Temp3 {
     public static void main(String args[]) throws FileNotFoundException {
        File f = new File("/home/gaspar/Desktop/USB/3269-2006-04-29-pajek.NET");
        Scanner scanner = new Scanner(f);
        PrintWriter writer = new PrintWriter(new File("/home/gaspar/Desktop/USB/pajek.parsed"));
        
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            line = line.trim();
            try {
                String fStr = line.substring(0, line.indexOf(" "));
                line =  line.substring(line.indexOf(" "));
                line = line.trim();
                String sStr =line.substring(0, line.indexOf(" "));
                
                writer.println(fStr + ":" + sStr);
                //System.out.println(fStr + " " + sStr);
            } catch (StringIndexOutOfBoundsException ex) { }
        }
        
        scanner.close();
        writer.close();
     }
}
