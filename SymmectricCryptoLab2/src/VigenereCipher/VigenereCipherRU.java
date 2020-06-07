package VigenereCipher;

import Entrophy.Entrophy;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VigenereCipherRU {

    private static final String alphabet="абвгдежзийклмнопрстуфхцчшщъыьэюя";
    private static final Map<String,Double> symbolsFrequencyInLanguage =new HashMap<>(){{
        put("о",0.10983);
        put("е",0.08483);
        put("а",0.07998);
        put("и",0.07367);
        put("н",0.067);
        put("т",0.06318);
        put("с",0.05473);
        put("р",0.04746);
        put("в",0.04533);
        put("л",0.04343);
        put("к",0.03486);
        put("м",0.03203);
        put("д",0.02977);
        put("п",0.02804);
        put("у",0.02615);
        put("я",0.02001);
        put("ы",0.01898);
        put("ь",0.01735);
        put("г",0.01687);
        put("з",0.01641);
        put("б",0.01592);
        put("ч",0.0145);
        put("й",0.01208);
        put("х",0.00966);
        put("ж",0.0094);
        put("ш",0.00718);
        put("ю",0.00639);
        put("ц",0.00486	);
        put("щ",0.00361	);
        put("э",0.00331);
        put("ф",0.00267	);
        put("ъ",0.00037);
    }};

    private Map<String,Integer> charToInt;//code of symbol
    private Map<Integer,String> intToChar;//decode to symbol

    public VigenereCipherRU(){
        charToInt =new HashMap<>();
        intToChar =new HashMap<>();
        for (int i=0;i<alphabet.length();i++){
            charToInt.put(Character.toString(alphabet.charAt(i)),i);
            intToChar.put(i,Character.toString(alphabet.charAt(i)));
        }

    }

    public String cipher(String openText,String key){
        String cipherText="";
        for(int i=0;i<openText.length();i++){
            cipherText+= intToChar.get(
                            (charToInt.get(Character.toString(openText.charAt(i)))+
                            charToInt.get(Character.toString(key.charAt(i%key.length()))))%
                            alphabet.length());
        }
        return cipherText;
    }
    public String decipher(String cipherText,String key){
        String openText="";
        int tempres;
        for (int i=0;i<cipherText.length();i++){
            tempres= charToInt.get(Character.toString(cipherText.charAt(i)))-
                    charToInt.get(Character.toString(key.charAt(i%key.length())));
            if(tempres<0){
                tempres+=alphabet.length();
                openText+= intToChar.get(tempres);
            }else {
                openText+= intToChar.get(tempres);
            }
        }
        return openText;
    }

    public double I(String text){
        double result=0;
        Entrophy e=new Entrophy(text);
        e.calculateSymbolsFrequency();
        Map<String,Integer> textFrequency=e.getSortedSymbolsFrequency();
        double n=text.length();
        for(String c:textFrequency.keySet()){
            result+=textFrequency.get(c)*(textFrequency.get(c)-1);
        }
        result/=(n*(n-1));
        return result;
    }

    public ArrayList<String> splitOnBlocks(String cipherText,int blockPeriod){
        ArrayList<String> blocks=new ArrayList<>();
        String block="";
        for (int j = 0; j < blockPeriod; j++) {
            for (int i = j; i < cipherText.length(); i += blockPeriod) {
                block = block + cipherText.charAt(i);
            }
            blocks.add(block);
            block = "";
        }
        return blocks;
    }

    public int findKeyLengthUsingIndexOfIndentity(String cipherText){
        ArrayList<String> blocks;
        String block="";
        double[] bestAverage={0.0,0.0};
        double averageI=0;
        for(int r=1;r<31;r++) {
            blocks = splitOnBlocks(cipherText,r);
            try(FileWriter writer = new FileWriter("index of indentity.txt", true))
            {
                writer.write("Key length = "+r+":"+"\n");
                writer.flush();
                for (String b : blocks) {
                    double temp=I(b);
                    averageI+=temp;
                }
                averageI/=r;
                writer.write("average: "+averageI+"\n");
                writer.flush();
            }
            catch(IOException ex){
                System.out.println(ex.getMessage());
            }

            if(Math.abs(0.05-averageI)<0.009){
                bestAverage[0]=averageI;
                bestAverage[1]=r;
                return r;
            }
            else {
                averageI=0.0;
                continue;
            }

        }
        return 0;
    }

    public double M(String block,int g,Map<String,Double> symbolsFrequencyInLanguage ){
        double res=0;
        Entrophy e=new Entrophy(block);
        e.calculateSymbolsFrequency();
        Map<String,Integer> symbolsFrequency=e.getSortedSymbolsFrequency();
        for(int i=0;i<alphabet.length();i++){
            if(!symbolsFrequency.containsKey(Character.toString(alphabet.charAt(i)))){
                symbolsFrequency.put(Character.toString(alphabet.charAt(i)),0);
            }
        }
        int x;
        for(String t:symbolsFrequencyInLanguage.keySet()){
            x=(charToInt.get(t)+g)%alphabet.length();
            res+=symbolsFrequencyInLanguage.get(t)
                    *(symbolsFrequency.get(intToChar.get(x)));
        }
        return res;
    }


    public String breaking(String cipherText){
        int candidateToKeysLength=findKeyLengthUsingIndexOfIndentity(cipherText);
        String key="";
            ArrayList<String> blocks=splitOnBlocks(cipherText,candidateToKeysLength);

            for(String block:blocks){
                int maxG=0;
                double maxM=M(block,0, symbolsFrequencyInLanguage);
                for(int g=1;g<alphabet.length();g++){
                    double tempM=M(block,g, symbolsFrequencyInLanguage);
                    if(tempM>=maxM){
                        maxM=tempM;
                        maxG=g;
                    }
                }
                key+=intToChar.get(maxG);
            }
            System.out.println("Key length: "+candidateToKeysLength+";\nKey: "+key);
        return key;
    }
}
