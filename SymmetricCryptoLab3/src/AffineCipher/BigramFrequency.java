package AffineCipher;

import java.util.*;


public class BigramFrequency {
    Map<String,Integer> frequency;
    LinkedHashMap<String,Integer> sortedFrequency;

    public BigramFrequency(String text){
        frequency=new TreeMap<>();
        sortedFrequency=new LinkedHashMap<>();
        calculateBigramFrequency(text);
        sortFrequencyByFall();
    }

    public Map<String, Integer> getFrequency() {
        return frequency;
    }

    public LinkedHashMap<String, Integer> getSortedFrequency() {
        return sortedFrequency;
    }

    public ArrayList<String> getListOfFrequentBigrams(){
        ArrayList<String> result=new ArrayList<>();
        for(String bigram:sortedFrequency.keySet()){
            result.add(bigram);
        }
        return result;
    }


    public void calculateBigramFrequency(String text){
        String a1;
        String a2;
        String bigram;
        for(int i=0;i<text.length()/2;i++){
            a1=Character.toString(text.charAt(2*i));
            a2=Character.toString(text.charAt(2*i+1));
            bigram=a1+a2;
            if (frequency.containsKey(bigram)){
                frequency.put(bigram,frequency.get(bigram)+1);
            }else {
                frequency.put(bigram,1);
            }
        }
    }

    public void sortFrequencyByFall(){
        Map<String,Integer> tempFrequency=new HashMap<>();
        tempFrequency.putAll(frequency);
        while(!tempFrequency.isEmpty()) {
            var entrySet = tempFrequency.entrySet();
            var iterator = entrySet.iterator();
            Map.Entry<String, Integer> entry = null;
            Map.Entry<String, Integer> maxEntry = iterator.next();
            while (iterator.hasNext()) {
                entry = iterator.next();
                if (maxEntry.getValue() < entry.getValue()) {
                    maxEntry = entry;
                }
            }
            sortedFrequency.put(maxEntry.getKey(), maxEntry.getValue());
            tempFrequency.remove(maxEntry.getKey(), maxEntry.getValue());
        }
    }

    public static double I(String text){
        Map<String,Integer> letterFrequency=new HashMap<>();
        String a;
        String bigram;
        for(int i=0;i<text.length();i++){
            a=Character.toString(text.charAt(i));
            if (letterFrequency.containsKey(a)){
                letterFrequency.put(a,letterFrequency.get(a)+1);
            }else {
                letterFrequency.put(a,1);
            }
        }
        double result=0;
        double n=text.length();
        for (String c:letterFrequency.keySet()){
            result+=letterFrequency.get(c)*(letterFrequency.get(c)-1);
        }
        result/=n*(n-1);
        return result;


    }


}
