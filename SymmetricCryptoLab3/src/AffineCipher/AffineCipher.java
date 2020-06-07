package AffineCipher;

import java.util.*;

public class AffineCipher {

    private final String alphabet = "абвгдежзийклмнопрстуфхцчшщьыэюя";
    private final int m=alphabet.length();
    private final Map<String,Integer> symbolCode=new HashMap<>(){{
        put("а",0);
        put("б",1);
        put("в",2);
        put("г",3);
        put("д",4);
        put("е",5);
        put("ж",6);
        put("з",7);
        put("и",8);
        put("й",9);
        put("к",10);
        put("л",11);
        put("м",12);
        put("н",13);
        put("о",14);
        put("п",15);
        put("р",16);
        put("с",17);
        put("т",18);
        put("у",19);
        put("ф",20);
        put("х",21);
        put("ц",22);
        put("ч",23);
        put("ш",24);
        put("щ",25);
        put("ь",26);
        put("ы",27);
        put("э",28);
        put("ю",29);
        put("я",30);

    }};
    private final Map<Integer,String> symbolDecode=new HashMap<>(){{
        put(0,"а");
        put(1,"б");
        put(2,"в");
        put(3,"г");
        put(4,"д");
        put(5,"е");
        put(6,"ж");
        put(7,"з");
        put(8,"и");
        put(9,"й");
        put(10,"к");
        put(11,"л");
        put(12,"м");
        put(13,"н");
        put(14,"о");
        put(15,"п");
        put(16,"р");
        put(17,"с");
        put(18,"т");
        put(19,"у");
        put(20,"ф");
        put(21,"х");
        put(22,"ц");
        put(23,"ч");
        put(24,"ш");
        put(25,"щ");
        put(26,"ь");
        put(27,"ы");
        put(28,"э");
        put(29,"ю");
        put(30,"я");
    }};
    private final List<String> mostFrequentBigrams=new LinkedList<>(){{
        add("ст");
        add("но");
        add("то");
        add("на");
        add("ен");
        add("ов");
        add("ни");
        add("ра");
        add("во");
        add("не");
        add("пм");
        add("ал");
        add("по");
        add("ко");
    }};


    public int[] codeMessage(String text){
        int[] codedMessage=new int[text.length()/2];
        int x1;
        int x2;
        for (int i = 0; i <text.length()/2 ; i++) {
            x1=symbolCode.get(String.valueOf(text.charAt(2*i)));
            x2=symbolCode.get(String.valueOf(text.charAt(2*i+1)));
            codedMessage[i]=x1*m+x2;
        }
        return codedMessage;
    }
    public String decodeMessage(int[] codedMessage){
        String text="";
        int x1;
        int x2;
        for(int i=0;i<codedMessage.length;i++){
            x1=codedMessage[i]/m;
            x2=codedMessage[i]%m;
            text+=symbolDecode.get(x1);
            text+=symbolDecode.get(x2);
        }
        return text;
    }

    public AffineCipher(){
    }

    public String cipher(String text,int a,int b){
        int[] codedText=codeMessage(text);
        int[] codedCipherText=new int[codedText.length];
        for(int i=0;i<codedText.length;i++){
            codedCipherText[i]=(a*codedText[i]+b)%(m*m);
        }
        return decodeMessage(codedCipherText);
    }

    public String decipher(String text,int a,int b){
        int[] codedCipherText=codeMessage(text);
        int[] codedText=new int[codedCipherText.length];
        int a_inv;
        int temp;
        for (int i = 0; i <codedText.length ; i++) {
            a_inv=inverse(a,m*m);
            temp=subMod(codedCipherText[i],b);
            codedText[i]=(a_inv*temp)%(m*m);
        }
        return decodeMessage(codedText);
    }

    public int gcd(int a,int b){
        int temp;
        if (b > a) {
            temp = a;
            a = b;
            b = temp;
        }
        while (b !=0) {
            temp = a%b;
            a =b;
            b =temp;
        }
        return a;
    }

    //x=element^(-1) mod (mod)
    public int inverse(int element, int mod){
        if(gcd(element,mod)!=1){
            return 0;
        }
        int a=element;
        int b=mod;
        int r=0;
        int p=1;
        int x;
        while (a!=0 && b!=0) {
            if (a>=b) {
                a -= b;
                p -= r;
            } else {
                b -= a;
                r -= p;
            }
        }
        if (a!=0) {
            x = p;

        }else {
            x = r;
        }
        if(x<0){
            x+=mod;
        }
        return x;
    }

    public int codeBigram(String bigram){
        int x1=symbolCode.get(String.valueOf(bigram.charAt(0)));
        int x2=symbolCode.get(String.valueOf(bigram.charAt(1)));
        return x1*m+x2;
    }

    public String decodeBigram(int bigram){
        String s1=symbolDecode.get(bigram/m);
        String s2=symbolDecode.get(bigram%m);
        return s1+s2;
    }

    //result = (a-b)mod(m*m)
    public int subMod(int a,int b){
        int result=a-b;
        if(result<0){
            result+=m*m;
        }
        return result;
    }

    //a*x=b (mod m*m), task to find roots
    public ArrayList<Integer> solveLinearModEquation(int a,int b){
        ArrayList<Integer> roots=new ArrayList<>();
        int d=gcd(a,m*m);
        if(d==1){
            roots.add((b*inverse(a,m*m))%(m*m));
            return roots;
        }
        else{
            if(b%d==0){
                int a1=a/d;
                int b1=b/d;
                int n1=m*m/d;
                int x0=(b1*inverse(a1,n1))%n1;
                for(int i=0;i<d;i++) {
                    roots.add(x0 + i * n1);
                }
            }
        }
        return roots;
    }

    public ArrayList<Integer> breaking(String cipherText){
        BigramFrequency cipherTextBigramsFrequency=new BigramFrequency(cipherText);
        ArrayList<String> cipherTextFrequentBigrams=cipherTextBigramsFrequency.getListOfFrequentBigrams();
        ArrayList<Integer> codedFrequentBigram=new ArrayList<>();
        for(String bigram:mostFrequentBigrams){
            codedFrequentBigram.add(codeBigram(bigram));
        }
        String y1;//firs most frequent bigram in ciphertext
        String y2;//second most frequent bigram in ciphertext
        String deciphered;
        int y1coded;//coded first most frequent bigram in ciphertext
        int y2coded;//coded second most frequent bigram in ciphertext
        int Y;//difference of coded bigrams of ciphertext
        int X;//difference of coded bigrams of natural language
        int b;//second key
        ArrayList<Integer> A;
        for(int k=0;k<5;k++) {
            for(int l=0;l<5;l++){
                if(k==l){
                    continue;
                }
                y1=cipherTextFrequentBigrams.get(k);
                y2=cipherTextFrequentBigrams.get(l);
                y1coded=codeBigram(y1);
                y2coded=codeBigram(y2);
                Y=subMod(y1coded,y2coded);
                for (int i = 0; i < mostFrequentBigrams.size(); i++) {
                    for (int j = 0; j < mostFrequentBigrams.size(); j++) {
                        if (i == j) {
                            continue;
                        }
                        X = subMod(codedFrequentBigram.get(j), codedFrequentBigram.get(i));
                        A = solveLinearModEquation(X, Y);
                        if (A.isEmpty()) {
                            continue;
                        } else {
                            for (int a : A) {
                                b = subMod(y1coded, (a * codedFrequentBigram.get(j)) % (m * m));
                                deciphered=decipher(cipherText,a,b);
                                if (Math.abs(0.055-BigramFrequency.I(deciphered))<0.005){
                                    //System.out.println("a:" + a + ",b:" + b + " - " + deciphered.substring(0,50)+" "+BigramFrequency.I(deciphered));
                                    return new ArrayList<>(Arrays.asList(a,b));
                                }
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<>();
    }


}

