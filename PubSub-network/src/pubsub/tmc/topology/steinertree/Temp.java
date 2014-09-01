/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pubsub.tmc.topology.steinertree;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author gaspar
 */
public class Temp {

    public static void main(String args[]) throws FileNotFoundException {
        File f = new File("/home/gaspar/graphSt.txt");
        Scanner scanner = new Scanner(f);
        String line;
        int prev;
        int num;
        Map<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();
        Set<Integer> temp;
        int totalSteiners = 0;
        int totalSubExp = 0;
        int total = 0;
        List<String> list = new ArrayList<String>();

        /* while (scanner.hasNextLine()) {
        list.add(scanner.nextLine());
        }
        
        scanner.close();
        
        total = list.size();
        
        Random r = new Random();
        List<String> output = new ArrayList<String>();
        temp = new HashSet<Integer>();
        int kk = 0;
        for (int i = 50; i < 935; i += 50) {
        for (int j = 0; j < 10; j++) {
        
        while (temp.size() != i) {
        temp.add(r.nextInt(list.size()));
        }
        String out = "int steinerPoints" + kk + "[] = {507,";
        kk++;
        for (Integer in : temp) {
        String str = list.get(in);
        String[] arStr = str.split(":");
        prev = Integer.parseInt(arStr[0]);
        
        out += prev + ",";
        for (int k = 1; k < arStr.length; k++) {
        int next = Integer.parseInt(arStr[k]);
        Set<Integer> set;
        if (!map.containsKey(prev)) {
        set = new HashSet<Integer>();
        map.put(prev, set);
        } else {
        set = map.get(prev);
        }
        
        set.add(next);
        prev = next;
        }
        }
        out = out.substring(0, out.length() - 1);
        System.out.println(out + "};");
        
        int totalEdges = 0;
        for (Set<Integer> s : map.values()) {
        totalEdges += s.size();
        }
        
        output.add(i + "[" + j + "] " + totalEdges);
        //System.out.println();
        
        map.clear();
        temp.clear();
        }
        }
        
        System.out.println("List<int []> list = new ArrayList<int[]>();");
        for (int b = 0; b < kk; b++) {
        System.out.println("list.add(steinerPoints" + b + ");");
        }
        
        for (String s : output) {
        System.out.println(s);
        }
        
        
        /* 
        System.out.println("TotalLines: " + totalLines);
        for (int i = 2; i <= totalLines; i++) {
        scanner.close();
        scanner = new Scanner(f);
         */
        List<Entry> sList = new ArrayList<Entry>();

        int ll = 0;
        int i = 50;
        while (scanner.hasNextLine()) { //&& total < i) {
            line = scanner.nextLine();
            if (line.equals("-------")) {
                int totalEdges = 0;
                for (Set<Integer> set : map.values()) {
                    totalEdges += set.size();
                }
                sList.add(new Entry(i, totalEdges, ll++));
                //System.out.println(i + " " + totalEdges);
                map.clear();
                continue;
            } else if (line.equals("#######")) {
                for (Entry e : sList) {
                    System.out.println(e.totalVertices + "[" + e.j + "] " + e.totalEdges);
                }

                i += 50;

                map.clear();
                sList.clear();
                ll = 0;
                continue;
            }

            String[] str = line.split(":");
            prev = Integer.parseInt(str[0]);
            for (int j = 1; j < str.length; j++) {
                num = Integer.parseInt(str[j]);
                if (!map.containsKey(prev)) {
                    temp = new HashSet<Integer>();
                    map.put(prev, temp);
                } else {
                    temp = map.get(prev);
                }
                temp.add(num);
                prev = num;
            }
            //total++;
        }


        // total = 0;
        //}

    }

    private static class Entry {

        public int j;
        public int totalVertices;
        public int totalEdges;

        public Entry(int a, int b, int j) {
            this.totalVertices = a;
            this.totalEdges = b;
            this.j = j;
        }
    }
    //}
}
