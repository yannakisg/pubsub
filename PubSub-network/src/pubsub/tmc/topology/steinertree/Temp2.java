/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pubsub.tmc.topology.steinertree;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author gaspar
 */
public class Temp2 {
    public static void main(String args[]) throws FileNotFoundException {
        File f = new File("/home/gaspar/NetBeansProjects/Topology/graph.txt");
        Scanner scanner = new Scanner(f);
        String line;
        String[] str;
        int fir, sec;
        Map<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();
        Set<Integer> temp, t;
        
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            str = line.split(":");
            if (str != null && str.length > 1) {
                //System.out.println(str[0]);
                fir = Integer.parseInt(str[0]);
                
                if (!map.containsKey(fir)) {
                    temp = new HashSet<Integer>();
                    map.put(fir, temp);
                } else {
                    temp = map.get(fir);
                }
                
                for (int i = 1; i < str.length; i++) {
                    sec = Integer.parseInt(str[i]);
                    
                    if ((t = map.get(sec)) != null) {
                        if (!t.contains(fir)) {
                            temp.add(sec);
                        }
                    } else {
                        temp.add(sec);
                    }
                }
            }
        }
        int totalEdges = 0;
        for (Set<Integer> s : map.values()) {
            totalEdges += s.size();
        }
        System.out.println("Total Edges: " + totalEdges);
        scanner.close();
    }
}
