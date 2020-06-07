#include <iostream>
#include <string.h>
#include <bitset>
#include "LFSR.cpp"
class GeneratorGeffe
{
private:
    LFSR L1;
    LFSR L2;
    LFSR L3;    
public:
    GeneratorGeffe(LFSR l1,LFSR l2,LFSR l3);
    void breakingOnCPU_one_thread(string sequence);
    ~GeneratorGeffe();
};

GeneratorGeffe::GeneratorGeffe(LFSR l1,LFSR l2,LFSR l3)
{
    L1=l1;
    L2=l2;
    L3=l3;
}
void GeneratorGeffe::breakingOnCPU_one_thread(string sequence){
    long *seq=L1.transformStringBitsToLongArray(sequence);
    int N=sequence.length();
    long pow=1<<L1.getPolyDeg();
    for(long i=1;i<pow;i++){
        L1.setState(i);
        long *xi=L1.generate2048bits();
        long R=0;
        for(int j=0;j<32;j++){
            R+=L1.weight(seq[j]^xi[j]);
        }
        if(abs(R-N/4)<250){
            cout<<"AAAAAAA"<<i<<" "<<R<<endl;
        }
        if(i%100000==0){
            cout<<i<<endl;
        }
    }

}
GeneratorGeffe::~GeneratorGeffe()
{
}
