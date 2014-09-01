/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pubsub.tmc.topology.steinertree;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author gaspar
 */
public class Temp5 {
    public static void main(String args[]) throws FileNotFoundException {
        File f = new File("/home/gaspar/temp");
        Scanner scanner = new Scanner(f);
        String str[];
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            line = line.trim();
            str = line.split(",");
            for (int i = 0; i < str.length; i++) {
                System.out.println(str[i]);
            }
        }
    }
}
