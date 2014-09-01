/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pubsub.node.controller.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import pubsub.bloomfilter.BloomFilter;
import pubsub.forwarding.FwdComponent;
import pubsub.util.FwdConfiguration;

/**
 *
 * @author gaspar
 */
public class Temp {

    public static void main(String args[]) {
        int length = 700;
        BloomFilter[] bls = new BloomFilter[length];
        for (int i = 0; i < length; i++) {
            bls[i] = BloomFilter.createRandom(32, 1);
        }
        BloomFilter finBl = BloomFilter.createZero();
        
        
        for (int i = 0; i < length; i++) {
            //BloomFilter.add(finBl, bls[i].getBytes(), 5);
            finBl.or(bls[i]);
        }
        String binaryString = finBl.toBinaryString();
        System.out.println(binaryString);
        int index = binaryString.indexOf("1");
        int total = 0;
        while (index >= 0) {
            total++;
            index++;
            index = binaryString.indexOf("1", index);
        }
        System.out.println(total);
        
        total = 0;
        int zero = 0;
        for (int i = 0; i < FwdConfiguration.ZFILTER_LENGTH_BITS; i++) {
            if (getBit(finBl.getBytes(), i) == 1) {
                total++;
            } else {
                zero++;
            }
        }
        
        System.out.println("One: " + total);
        System.out.println("Zero: " + zero);
        System.out.println("One + Zero: " + (total + zero));
        /*byte[] bytes = finBl.getBytes();
        
        for (int i = 0; i < bytes.length; i++) {
            
        }*/
        
        
        
        
        
        
        
        
        /*throws FileNotFoundException, IOException {
        
        
        
       /* File file = new File("/home/gaspar/rvp.log");
        Scanner scanner = new Scanner(file);
        String line;
        int index;
        PrintWriter pw = new PrintWriter(new FileWriter(new File("rvp.log")));
        
        int time;
        while (scanner.hasNext()) {
            line = scanner.nextLine();
            index = line.indexOf("Time[PubToSub]: ");
            
            if (index > 0) {
                time = Integer.parseInt(line.substring(index + "Time[PubToSub]: ".length()));
                
                pw.println("Total Time: " + time + " ms");
            }
        }
        
        pw.close();*/
    }
    
    private static int getBit(byte[] data, int pos) {
      int posByte = pos/8; 
      int posBit = pos%8;
      byte valByte = data[posByte];
      int valInt = valByte>>(8-(posBit+1)) & 0x0001;
      return valInt;
   }
}
