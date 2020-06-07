import com.aparapi.Kernel;
import static java.lang.Math.*;
public class GeneratorGeffe {
    private LFSR L1;
    private LFSR L2;
    private LFSR L3;

    private final double p_1=1.0/4.0;
    private final double p_2=1.0/2.0;

    public void setT_H0(double t_H0) {
        this.t_H0 = t_H0;
    }

    public void setT_H1_for_L1(double t_H1_for_L1) {
        this.t_H1_for_L1 = t_H1_for_L1;
    }

    public void setT_H1_for_L2(double t_H1_for_L2) {
        this.t_H1_for_L2 = t_H1_for_L2;
    }

    private double t_H0;
    private double t_H1_for_L1;
    private double t_H1_for_L2;

    GeneratorGeffe(LFSR L1, LFSR L2, LFSR L3){
        this.L1=L1;
        this.L2=L2;
        this.L3=L3;
    }

    public String generateBitSequence(int size){
        String r="";
        for(int i=0;i<size;i++){
            r+=generateBit();
        }
        return r;
    }

    public void setStateL1(long stateL1) {
        L1.setState(stateL1);
    }

    public void setStateL2(long stateL2) {
        L2.setState(stateL2);
    }

    public void setStateL3(long stateL3){
        L3.setState(stateL3);
    }

    public LFSR getL1(){ return L1; }

    public LFSR getL2(){ return L2; }

    public LFSR getL3(){
        return L3;
    }



    public long generateBit(){
        long x=L1.next();
        long y=L2.next();
        long s=L3.next();
        return (s&x)^((1^s)&y);
    }

    private void calculateL_parallel_CPU(CommonResourceForParralelCalculationGeffeGenerator common,
                                         LFSR lfsr,
                                         Thread threadList[][],
                                         int registerNumber) {
        for(int i=0;i<threadList.length;i++){
            threadList[i][registerNumber]=new Thread(new Runnable() {
                @Override
                public void run() {
                    LFSR l=lfsr.clone();
                    long seq[]=l.transformStringBitsToLongArray(common.bitSequence);
                    long pow=(long)1<<l.getPolyDeg();
                    long k;
                    double C;
                    if(registerNumber==0){
                        C=common.bitSequence.length()*p_1+t_H1_for_L1*Math.sqrt(common.bitSequence.length()*p_1*(1.0-p_1));
                    }
                    else {
                        C=common.bitSequence.length()*p_1+t_H1_for_L2*Math.sqrt(common.bitSequence.length()*p_1*(1.0-p_1));
                    }
                    synchronized (common){
                        k=common.state[registerNumber];
                        common.state[registerNumber]++;
                    }
                    long R;
                    long xy[]=new long[32];
                    while (k<pow){
                        if(Thread.currentThread().isInterrupted()){
                            return;
                        }
                        l.setState(k);
                        R=0;
                        for(int i=0;i<32;i++){
                            for(int j=63;j>-1;j--){
                                xy[i]|=l.next()<<j;
                            }
                            R+=l.weight(seq[i]^xy[i]);
                            if(R>C){
                                synchronized (common){
                                    if(k==common.state[registerNumber]){
                                        common.state[registerNumber]++;
                                        k++;
                                    }else {
                                        k=common.state[registerNumber];
                                        common.state[registerNumber]++;
                                    }
                                }
                                continue;
                            }
                        }
                        if(R<(long)C){
                            System.out.println("AAAAAAAAA "+registerNumber+" "+k+" "+R);
                            synchronized (common){
                                common.keyState[registerNumber]=k;
                                if(common.keyState[0]!=0&&common.keyState[1]!=0){
                                    common.notify();
                                    Thread.yield();
                                }
                            }
                            for (int t=0;t<threadList.length;t++){
                                threadList[t][registerNumber].interrupt();
                            }
                        }
                        if(k%100000==0){
                            System.out.println(k+" "+registerNumber);
                        }
                        synchronized (common){
                            if(k==common.state[registerNumber]){
                                common.state[registerNumber]++;
                                k++;
                            }else {
                                k=common.state[registerNumber];
                                common.state[registerNumber]++;
                            }
                        }
                    }

                }
            });
            threadList[i][registerNumber].start();
        }

    }

    private void calculateL3_parallel_CPU(CommonResourceForParralelCalculationGeffeGenerator common,
                                          GeneratorGeffe geffe,
                                          int threads){
        Thread threadList[]=new Thread[threads];
        for(int i=0;i<threads;i++) {
            threadList[i]=new Thread(new Runnable() {
                @Override
                public void run() {
                    GeneratorGeffe g=geffe.clone();
                    long seq[] = g.getL3().transformStringBitsToLongArray(common.bitSequence);
                    long pow = (1 << g.getL3().getPolyDeg());
                    long k;
                    synchronized (common) {
                        k = common.state[2];
                        common.state[2]++;
                    }
                    while (k < pow) {
                        if(Thread.currentThread().isInterrupted()){
                            return;
                        }
                        g.setStateL1(common.keyState[0]);
                        g.setStateL2(common.keyState[1]);
                        g.setStateL3(k);
                        //long zi[] = g.generate2048bit();
                        long zi[]=new long[32];
                        for(int j=0;j<32;j++){
                            zi[j]=0;
                            for(int i=63;i>-1;i--){
                                zi[j]|=g.generateBit()<<i;
                            }
                            if(zi[j]!=seq[j]){
                                break;
                            }
                            else {
                                if(j==31){
                                    System.out.println("AAAAAAAA " + k);
                                    synchronized (common){
                                        common.keyState[2]=k;
                                        common.notify();
                                        Thread.yield();
                                    }
                                    for(int t=0;t<threads;t++){
                                        threadList[t].interrupt();
                                    }
                                }
                            }
                        }
                        if (k % 100000 == 0) {
                            System.out.println(k+" "+2);
                        }
                        synchronized (common){
                            if(k==common.state[2]){
                                common.state[2]++;
                                k++;
                            }else {
                                k=common.state[2];
                                common.state[2]++;
                            }
                        }
                    }
                }
                });
                threadList[i].start();
            }
        }


    public void breakingOnCPU_multi_threads(String sequence,int threads){
        CommonResourceForParralelCalculationGeffeGenerator common=new CommonResourceForParralelCalculationGeffeGenerator();
        common.bitSequence=sequence;
        common.state[0]=1;
        common.state[1]=1;
        common.state[2]=1;
        common.keyState[0]=0;
        common.keyState[1]=0;
        common.keyState[2]=0;
        Thread threadList[][]=new Thread[threads][2];
        calculateL_parallel_CPU(common,L1,threadList,0);
        calculateL_parallel_CPU(common,L2,threadList,1);
        synchronized (common){
            try {
                common.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        synchronized (common){
            calculateL3_parallel_CPU(common, this, threads);
            try {
                common.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("L1: "+Long.toBinaryString(common.keyState[0]));
        System.out.println("L2: "+Long.toBinaryString(common.keyState[1]));
        System.out.println("L3: "+Long.toBinaryString(common.keyState[2]));
    }

    class CommonResourceForParralelCalculationGeffeGenerator {
        public String bitSequence;
        public long[] state=new long[3];
        public long[] keyState=new long[3];
    }

    private long[] executeGPUKernelForL(LFSR L, String sequence,int N,int C){
        int sizeState=23;//my videocard can execute 2^23 states simultaneously
        int size=1<<sizeState;
        long result[]=new long[size];
        long codedSequence[]=L.transformStringBitsToLongArray(sequence);
        long lastPositionMask=L.getLastPositionMask();
        long polynomDeg=L.getPolyDeg();
        long stateReader=L.getStateReader();
        Kernel kernel=new Kernel() {
            int weight(long n){
                int wt=0;
                while (n!=0){
                    n&=(n-1);
                    wt++;
                }
                return wt;
            }
            long shift(long s){
                long state=s;
                int wt=weight(state&stateReader);
                state>>=1;
                if((wt&0b1)==0b1){
                    state|=lastPositionMask;
                }
                return state;
            }
            @PrivateMemorySpace(1) long stateAtBegin[]=new long[1];
            long loop(long stateAtBegin,long C){
                long state=stateAtBegin;
                long R=0;
                long xy[]=new long[32];
                int n=N/32;
                for (int i = 0; i <n /*32*/; i++) {
                    xy[i]=0;
                    for (int j = 63; j > -1; j--) {
                        xy[i] |= (state & (long)0b1) << j;
                        state = shift(state);
                    }
                    R += weight(codedSequence[i] ^ xy[i]);
                    if(R>C){
                        return 0;
                    }
                }
                return stateAtBegin;
            }
            @Override
            public void run() {
                int gid=getGlobalId();
                @Local int numberOfResults=0;
                @Local long w=(long)1<<(polynomDeg-sizeState);
                for(long k=0;k<=w;k++) {
                    stateAtBegin[0]=gid+k*size;
                    long res=loop(stateAtBegin[0],C);
                    if(res!=0){
                        numberOfResults++;
                        result[0]=numberOfResults;
                        result[numberOfResults]=res;//stateAtBegin[0];
                    }else{
                        continue;
                    }
                }
            }
        };
        kernel.execute(size);
        long res[]=new long[(int)result[0]];
        for(int i=0;i<res.length;i++){
            res[i]=result[i+1];
        }
        kernel.dispose();
        return res;
    }

    private long[] executeGPUKernelForL3(String sequence,long stateL1,long stateL2){
        int sizeState=23;//my videocard can execute 2^23 states simultaneously
        int size=1<<sizeState;//2^{23}
        long result[]=new long[size];
        long codedSequence[]=this.L1.transformStringBitsToLongArray(sequence);

        long lastPositionMask1=L1.getLastPositionMask();
        long stateReader1=L1.getStateReader();

        long lastPositionMask2=L2.getLastPositionMask();
        long stateReader2=L2.getStateReader();

        long lastPositionMask3=L3.getLastPositionMask();
        long polynomDeg3=L3.getPolyDeg();
        long stateReader3=L3.getStateReader();

        Kernel kernel=new Kernel() {
            int weight(long n){
                int wt=0;
                while (n!=0){
                    n&=(n-1);
                    wt++;
                }
                return wt;
            }
            long shiftL1(long stateL1){
                int wt=weight(stateL1&stateReader1);
                stateL1>>=1;
                if((wt&0b1)==0b1){
                    stateL1|=lastPositionMask1;
                }
                return stateL1;
            }
            long shiftL2(long stateL2){
                int wt=weight(stateL2&stateReader2);
                stateL2>>=1;
                if((wt&0b1)==0b1){
                    stateL2|=lastPositionMask2;
                }
                return stateL2;
            }
            long shiftL3(long stateL3){
                int wt=weight(stateL3&stateReader3);
                stateL3>>=1;
                if((wt&0b1)==0b1){
                    stateL3|=lastPositionMask3;
                }
                return stateL3;
            }
            boolean loop(long state3AtBegin){
                long state1=stateL1;
                long state2=stateL2;
                long state3=state3AtBegin;
                long x,y,s;
                for(int i=0;i<32;i++){
                    for (int j=63;j>-1;j--){
                        x=state1&0b1;
                        state1=shiftL1(state1);
                        y=state2&0b1;
                        state2=shiftL2(state2);
                        s=state3&0b1;
                        state3=shiftL3(state3);
                        if(((s&x)^((1^s)&y))!=((codedSequence[i]>>j)&(long)1)){
                            return false;
                        }else {
                            continue;
                        }
                    }
                }
                return true;
            }
            @Override
            public void run() {
                int gid=getGlobalId();
                @Local int numberOfResults=0;
                @Local long w=(long)1<<(polynomDeg3-sizeState);
                for(int k=0;k<=w;k++){
                    long state3AtBegin=gid+k*size;
                    if(loop(state3AtBegin)){
                        numberOfResults++;
                        result[0]=numberOfResults;
                        result[numberOfResults]=state3AtBegin;
                    }else {
                        continue;
                    }
                }
            }
        };
        kernel.execute(size);
        long res[]=new long[(int)result[0]];
        for(int i=0;i<res.length;i++){
            res[i]=result[i+1];
        }
        kernel.dispose();
        return res;
    }

    public void breakingOnGPU(String sequence){
        //int N=(int)pow(((t_H0*sqrt(p_1*(1.0-p_1))+t_H1_for_L1*sqrt(p_2*(1.0-p_2)))/(p_2-p_1)),2.0)+1;/**/
        int N=256;
        int bias=N/8+20;
        int C=(int)((double)N*p_1+t_H1_for_L1*sqrt((double)N*p_1*(1.0-p_1)))+bias;
        System.out.println("N: "+N);
        System.out.println("C:"+C);
        long start=System.currentTimeMillis();
        long[] stateL1=executeGPUKernelForL(L1,sequence,N,C);
        System.out.println("L1");
        for(var k:stateL1){
            System.out.println(k+" "+Long.toBinaryString(k));
        }
        System.out.println("Time:" + (System.currentTimeMillis() - start)+" miliseconds");
        N=256;
        C=(int) ((double)N*p_1+t_H1_for_L2*sqrt((double)N*p_1*(1.0-p_1)))+bias;
        System.out.println("N: "+N);
        System.out.println("C:"+C);
        System.out.println();
        long[] stateL2=executeGPUKernelForL(L2,sequence,N,C);
        System.out.println();
        System.out.println("L2:");
        for(var k:stateL2){
            System.out.println(k+" "+Long.toBinaryString(k));
        }
        System.out.println("Time:" + (System.currentTimeMillis() - start)+" miliseconds");
        System.out.println();

        long[] stateL3=new long[1];
        for (int i = 0; i <stateL1.length; i++) {
            for (int j = 0; j <stateL2.length ; j++) {
                stateL3 = executeGPUKernelForL3(sequence, stateL1[i], stateL2[j]);
            }
        }
        System.out.println("L3:");
        for (var k:stateL3){
            System.out.println(k+" "+Long.toBinaryString(k));
        }
        System.out.println("Time:" + (System.currentTimeMillis() - start)+" miliseconds");
    }

    public GeneratorGeffe clone(){
        LFSR L1clone=L1.clone();
        LFSR L2clone=L2.clone();
        LFSR L3clone=L3.clone();
        return new GeneratorGeffe(L1clone,L2clone,L3clone);
    }

}
