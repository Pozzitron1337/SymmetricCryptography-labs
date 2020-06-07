package Entrophy;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Entrophy {
    private String text;

    private HashMap<Character,Integer> map;
    private TreeSet<Character> alphabet;
    private int[] symbolFrequency;
    private Map<String,Integer> sortedSymbolsFrequency;
    private int[][] bigramWithIntersectionFrequency;
    private int[][] bigramWithoutIntersectionFrequency;

    public Map<String, Integer> getSortedSymbolsFrequency() {
        return sortedSymbolsFrequency;
    }

    public Entrophy(String text){
        this.text=text;
        alphabet=new TreeSet<>();
        for(int i=0;i<text.length();i++){
            alphabet.add(text.charAt(i));
        }
        symbolFrequency=new int[alphabet.size()];
        bigramWithIntersectionFrequency=new int[alphabet.size()][alphabet.size()];
        bigramWithoutIntersectionFrequency=new int[alphabet.size()][alphabet.size()];
        map=new HashMap<>();
        int i=0;
        for(char c:alphabet){
            map.put(c,i);
            i++;
        }
        sortedSymbolsFrequency =new LinkedHashMap<>();
    }

    public void printSymbolsFrequency(){
        for (Character c:alphabet){
            System.out.println(c+" "+symbolFrequency[map.get(c)]);
        }
    }

    public void calculateSymbolsFrequency(){
        for (int i=0;i<text.length();i++){
            symbolFrequency[map.get(text.charAt(i))]++;
        }

        Map<String,Integer> unsortMap=new HashMap<>();
        for (Character c:alphabet){
            unsortMap.put(c.toString(),symbolFrequency[map.get(c)]);
        }
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return -(o1.getValue()).compareTo(o2.getValue());
            }
        });

        for (Map.Entry<String, Integer> entry : list) {
            sortedSymbolsFrequency.put(entry.getKey(), entry.getValue());
        }

    }

    public void printBigramWithIntersectionFrequency(){
        for (int k=0;k<alphabet.size();k++){
            for (int l=0;l<alphabet.size();l++){
                System.out.print(bigramWithIntersectionFrequency[k][l]+" ");
            }
            System.out.println();
        }
    }

    public void calculateBigramWithIntersectionFrequency(){
        int i=0;
        int j=1;
        while (j<text.length()){
            bigramWithIntersectionFrequency[map.get(text.charAt(j))][map.get(text.charAt(i))]++;
            i++;
            j++;
        }
    }

    public void printBigramWithoutIntersectionFrequency(){
        for (int k=0;k<alphabet.size();k++){
            for (int l=0;l<alphabet.size();l++){
                System.out.print(bigramWithoutIntersectionFrequency[k][l]+" ");
            }
            System.out.println();
        }
    }

    public void calculateBigramWithoutIntersectionFrequency(){
        int i=0;
        int j=1;
        while (j<text.length()){
            bigramWithoutIntersectionFrequency[map.get(text.charAt(j))][map.get(text.charAt(i))]++;
            i+=2;
            j+=2;
        }
    }

    public void calculateEntrophyForSymbolFrequency(){
        calculateSymbolsFrequency();
        double result=0;
        double probability=0;
        double log2Probability=0;
        for (int i=0;i<symbolFrequency.length;i++){
            probability=((double)symbolFrequency[i])/((double)text.length());
            log2Probability=Math.log10(probability)/Math.log10(2.0);
            result+=(probability*log2Probability);
        }
        result=-result;
        System.out.println("Entrophy for symbol:"+result);
    }

    public void calculateEntrophyForBigramWithIntersection(){
        calculateBigramWithIntersectionFrequency();
        double result=0;
        double probability=0;
        double log2Probability=0;
        for(int i=0;i<alphabet.size();i++){
            for(int j=0;j<alphabet.size();j++){
                if(bigramWithIntersectionFrequency[i][j]==0){
                    continue;
                }else {
                    probability=(double)bigramWithIntersectionFrequency[i][j]/(double) text.length();
                    log2Probability=Math.log10(probability)/Math.log10(2.0);
                    result+=(probability*log2Probability);
                }
            }
        }
        result=-(result/2);
        System.out.println("Entrophy of bigram with intersection:"+result);
    }

    public void calculateEntrophyForBigramWithoutIntersection(){
        calculateBigramWithoutIntersectionFrequency();
        double result=0;
        double probability=0;
        double log2Probability=0;
        for(int i=0;i<alphabet.size();i++){
            for(int j=0;j<alphabet.size();j++){
                if(bigramWithoutIntersectionFrequency[i][j]==0){
                    continue;
                }else {
                    probability=(double)bigramWithoutIntersectionFrequency[i][j]/(double) text.length();
                    log2Probability=Math.log10(probability)/Math.log10(2.0);
                    result+=(probability*log2Probability);
                }
            }
        }
        result=-result;
        System.out.println("Entrophy of bigram without intersection:"+result);
    }

    public void printToFileSortedSymbolFrequency(){
        if(alphabet.isEmpty()){
            return;
        }
        try(FileWriter writer = new FileWriter("notes3.txt"))
        {
           for (String c:sortedSymbolsFrequency.keySet()) {
               writer.write(c+" "+sortedSymbolsFrequency.get(c)+"\n");
               writer.flush();
           }
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }



}
