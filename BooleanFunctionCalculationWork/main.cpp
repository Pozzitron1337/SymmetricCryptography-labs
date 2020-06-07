#include <iostream>
#include <bitset>
using namespace std;
#define unsigned long long ulong

ulong f_16(ulong x){
    //x=(x_1,x_2,x_3,x_4,x_5)
    int x5=x&1;
    int x4=x>>1&1;
    int x3=x>>2&1;
    int x2=x>>3&1;
    int x1=x>>4&1;
    return x1==(x2==(x3==(x4==x5)));
}

void printValuesF(){
    ulong bit;
    for(int i=0;i<(1<<5);i++){
        bit=f_16(i);
        cout<<i+1<<")"<<bitset<5>(i).to_string()<<" "<<bit<<endl;
    }
    
}

void w(int i,int j,int k,long **c){
    if(k==6){
        return;
    }
    cout<<"i:"<<i<<" j:"<<j<<endl;
    for(int l=i;l<=(i+j)/2;l++){
        c[l][k]=c[l][k-1]+c[l+(1<<(5-k))][k-1];
        c[l+(1<<(5-k))][k]=c[l][k-1]-c[l+(1<<(5-k))][k-1];
        //cout<<"c["<<l<<"]["<<k<<"]="<<c[l][k]<<endl;
        //cout<<"c["<<l+(1<<(5-k))<<"]["<<k<<"]="<<c[l][k]<<endl;
    }
    
    w(i,(i+j)/2,k+1,c);
    w(((i+j)/2)+1,j,k+1,c);   
}

void calcWolsh(){
    ulong value=0;
    ulong bit;
    for(int i=0;i<(1<<5);i++){
        bit=f_16(i);
        value|=bit<<31-i;
    }
    long** c=new long*[32];
    for(int i=0;i<32;i++){
        c[i]=new long[5];
    }
    for(int i=31;i>-1;i--){
        if(value&(ulong)1<<i){
            c[31-i][0]=-1;
        }else{
            c[31-i][0]=1;  
        }
    }
    w(0,31,1,c);
    for(int i=0;i<32;i++){
        cout<<c[i][5]<<endl;;
    }
}


int main(){
    //printValuesF();
    calcWolsh();
    return 0;
}
