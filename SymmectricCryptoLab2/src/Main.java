import VigenereCipher.VigenereCipherRU;
import java.io.*;
import java.util.ArrayList;

public class Main {

    public static void breaking(){
        VigenereCipherRU vigenereCipherRU=new VigenereCipherRU();
        String cipherText="";
        try (BufferedReader reader = new BufferedReader(
                new FileReader("16.txt")))  {
            String s;
            while ((s=reader.readLine())!=null){
                cipherText+=s;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("CipherText:");
        System.out.println(cipherText);
        vigenereCipherRU.breaking(cipherText);
    }

    public static void cipherTheText(){
        ArrayList<String> keys=new ArrayList<>(){{
            add("да");
            add("язь");
            add("зоря");
            add("самбо");
            add("давайвстретимсяутром");
        }};
        String openText="";
        try (BufferedReader reader = new BufferedReader(
                new FileReader("openText.txt")))  {
            String s;
            while ((s=reader.readLine())!=null){
                openText+=s;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        openText=openText.toLowerCase().replaceAll("[^а-я]","").substring(0,2500);
        System.out.println(openText);
        VigenereCipherRU vigenereCipher=new VigenereCipherRU();
        System.out.println("I for open text: "+vigenereCipher.I(openText));
        for (String key:keys){
            String ciphertext=vigenereCipher.cipher(openText,key);
            System.out.println("-------------------------------");
            //System.out.println(ciphertext);
            System.out.println("key length: "+key.length()+"\nI: "+vigenereCipher.I(ciphertext));
        }

    }


    public static void decipherCiphertext(){
        String cipherText="";
        try (BufferedReader reader = new BufferedReader(
                new FileReader("7.txt")))  {
            String s;
            while ((s=reader.readLine())!=null){
                cipherText+=s;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Cipher text:");
        System.out.println(cipherText);
        VigenereCipherRU vigenereCipherRU =new VigenereCipherRU();
        String key=vigenereCipherRU.breaking(cipherText);
        System.out.println("Key:");
        System.out.println(key);
        String openText=vigenereCipherRU.decipher(cipherText,key);
        System.out.println("Deciphered text:");
        System.out.println(openText);
    }
    public static void main(String[] args) {
        cipherTheText();
        breaking();
        decipherCiphertext();
    }
}
