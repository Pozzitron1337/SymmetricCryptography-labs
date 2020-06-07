import AffineCipher.AffineCipher;
import AffineCipher.BigramFrequency;
import java.io.*;


public class Main {

    public static void findFrequentBigram(){
        String ciphertext="";
        try(FileReader reader = new FileReader("variants.utf8/16.txt")) {
            int c;
            while((c=reader.read())!=-1){
                ciphertext+=(char)c;
            }
        }
        catch(IOException ex){System.out.println(ex.getMessage());}
        BigramFrequency bigramFrequency=new BigramFrequency(ciphertext);
        System.out.println(bigramFrequency.getListOfFrequentBigrams());
    }

    public static void breakAllCipherTexts(File folder){
        for(File fileWithCipherText:folder.listFiles()){
            String ciphertext="";
            System.out.println(fileWithCipherText.toString()+":");
            try(FileReader reader = new FileReader(fileWithCipherText)) {
                int c;
                while((c=reader.read())!=-1){
                    ciphertext+=(char)c;
                }
            }
            catch(IOException ex){System.out.println(ex.getMessage());}
            ciphertext=ciphertext.replaceAll("[^а-я]","");
            AffineCipher a =new AffineCipher();
            System.out.println("CipherText:\n"+ciphertext.substring(0,100));
            var key=a.breaking(ciphertext);
            String deciphered=a.decipher(ciphertext,key.get(0),key.get(1));
            System.out.println("Key: ("+key.get(0)+","+key.get(1)+")");
            System.out.println("Deciphered:\n"+deciphered.substring(0,100));
            System.out.println();
        }
    }

    public static void main(String[] args) {
        breakAllCipherTexts(new File("variants.utf8"));
    }
}
