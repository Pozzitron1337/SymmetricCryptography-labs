#include <iostream>
#include <bitset>
#include <math.h>
using namespace std;

class LFSR
{
private:
    long polynom;
    long state;
    long stateReader;
    long lastPosition;
public:
    LFSR(){}
    LFSR(long p,long s){
        lastPosition=(int)(log2(p))-1;
        polynom=p;
        state=s;
        stateReader=p&(~(1<<lastPosition+1));
    }
    ~LFSR(){}
    void test(){
        cout<<bitset<26>(polynom)<<endl;
        cout<<bitset<26>(stateReader)<<endl;
    }
    long getPolyDeg(){
        return lastPosition+1;
    }
    void setState(long s){
        state=s;
    }
    int weight(long n){
        int wt=0;
        while(n!=0){
            n&=(n-1);
            wt++;
        }
        return wt;
    }
    long next(){
        int wt=weight(state & stateReader);
        long result=state&0b1;
        state>>=1;
        if((wt&0b1)==0b1){
            state|=(1<<(lastPosition));
        }
        return result;
    }

    long* generate2048bits(){
        int n=2048/64;
        long *sequence =new long[n];
        for(int i=0;i<n;i++){
            for(int j=63;j>-1;j--){
                long p=next();
                sequence[i]|=p<<j;
            }
        }
        return sequence;
    }

    long* transformStringBitsToLongArray(string sequence){
        int N=sequence.length();
        int n=N/64;
        long *r=new long[n];
        int j=0;
        for(int i=0;i<n;i++){
            //cout<<bitset<64>(sequence.substr(j,64)).to_ulong()<<endl;
            r[i]=(long)bitset<64>(sequence.substr(j,64)).to_ulong();
            cout<<r[i]<<endl;
            j+=64;
            
        }
        return r;
    }


    
};


