

import Entrophy.*;

import java.io.*;
import java.nio.charset.Charset;


public class Main {

    public static void main(String[] args) {
        String text ="";
        String text1mb="";
        long t1=System.currentTimeMillis();
        BufferedReader reader = null;
        try {
            String line;
            reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream("TEXT"),
                            Charset.forName("IBM866")));

            while ((line = reader.readLine()) != null) {
                text+=line;
            }
            reader.close();
            reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream("opentext.txt"),
                            Charset.forName("UTF-8")));
            line="";
            while ((line = reader.readLine()) != null) {
                text1mb+=line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long t2=System.currentTimeMillis();
        System.out.println("Reading files time:"+(t2-t1));
        long t3=System.currentTimeMillis();
        Entrophy e;
        /*text = text.replaceAll("\\n"," ");
        System.out.println();
        e=new Entrophy(text);
        e.calculateEntrophyForSymbolFrequency();
        e.printSymbolsFrequency();
        e.calculateEntrophyForBigramWithIntersection();
        e.calculateEntrophyForBigramWithoutIntersection();
        text=text.replaceAll("\\s","");
        System.out.println();
        e=new Entrophy(text);
        e.calculateEntrophyForSymbolFrequency();
        e.calculateEntrophyForBigramWithIntersection();
        e.calculateEntrophyForBigramWithoutIntersection();
*/
        System.out.println();
        text1mb=text1mb.toLowerCase().replaceAll("[^а-я\\s]","");
        System.out.println(text1mb.substring(0,100));
        e=new Entrophy(text1mb);
        e.calculateEntrophyForSymbolFrequency();
        e.calculateEntrophyForBigramWithIntersection();
        e.calculateEntrophyForBigramWithoutIntersection();
        text1mb=text1mb.toLowerCase().replaceAll("[^а-я]","");
        System.out.println(text1mb);
        e=new Entrophy(text1mb);
        e.calculateEntrophyForSymbolFrequency();
        e.calculateEntrophyForBigramWithIntersection();
        e.calculateEntrophyForBigramWithoutIntersection();
        long t4=System.currentTimeMillis();
        System.out.println("Calculating all entrophy time:"+(t4-t3));



    }
}
