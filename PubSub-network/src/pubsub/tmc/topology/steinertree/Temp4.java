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
public class Temp4 {
    public static void main(String args[]) throws FileNotFoundException {
        File f = new File("/home/gaspar/Desktop/USB/3356.cch");
        Scanner scanner = new Scanner(f);
        PrintWriter writer = new PrintWriter(new File("/home/gaspar/Desktop/USB/3356.parsed"));
        
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            int pos = line.indexOf("@");
            String v0 = line.substring(0, pos-1);
            String v1 = "";
            pos = line.indexOf("<");
            
            while (pos != -1) {
                int posE = line.indexOf(">", pos);
                v1 = line.substring(pos + 1);
                v1 = v1.substring(0, posE - pos-1);
                pos = line.indexOf("<", posE);
                writer.println(v0 + ":" + v1);            }            
        }
        
        scanner.close();
        writer.close();
    }
}
