import java.math.BigInteger;

public class LFSR {
    private long lastPosition;
    private long polynom;
    private long stateReader;
    private long state;
    private final int longSize=Long.SIZE;
    private long lastPositionMask;

    public long getLastPosition() {
        return lastPosition;
    }

    public long getPolynom() {
        return polynom;
    }

    public long getStateReader() {
        return stateReader;
    }

    public int getLongSize() {
        return longSize;
    }

    public long getLastPositionMask() {
        return lastPositionMask;
    }

    LFSR(long polynom){
        //this.lastPosition =((long) (Math.log(Math.abs(polynom)) / Math.log(2)))-1;
        this.lastPosition=Long.toBinaryString(polynom).length()-2;
        this.polynom = polynom;
        this.stateReader = polynom &(~((long)1<<(lastPosition+(long)1)));
        this.lastPositionMask=((long)1)<<lastPosition;
        this.state=1;
    }
    LFSR(long polynom, long state){
        this.lastPosition =((int) (Math.log(polynom) / Math.log(2)))-1;
        this.polynom = polynom;
        this.stateReader = polynom &(~(1<<(lastPosition+1)));
        this.lastPositionMask =((long)1)<<lastPosition;
        this.state=state;
    }
    void setState(long state){
        this.state=state;
    }
    long getState(){
        return state;
    }
    long getPolyDeg(){ return lastPosition+1; }

    public int weight(long n){
        int wt=0;
        while(n!=0){
            n&=(n-1);
            wt++;
        }
        return wt;
    }

    public long next(){
        int wt=weight(state & stateReader);
        long result=state&0b1;
        state>>=1;
        if((wt&0b1)==0b1){
            state|= lastPositionMask;
        }
        return result;
    }

    public long[] generate2048bits(){
        long bitsAsLongArray[] =new long[2048/longSize];
        for(int i=0;i<bitsAsLongArray.length;i++){
            for(int j=longSize-1;j>-1;j--){
                long p=this.next();
                bitsAsLongArray[i]|=p<<j;
            }
        }
        return bitsAsLongArray;
    }

    public long[] transformStringBitsToLongArray(String sequence){
        int N=sequence.length();
        int n=N/longSize;
        long r[]=new long[n];
        int j=0;
        for(int i=0;i<n;i++){
            r[i]=(new BigInteger(sequence.substring(j,j+longSize),2)).longValue();
            j+=longSize;
        }
        return r;
    }

    public LFSR clone(){
        LFSR result=new LFSR(polynom,state);
        return result;
    }

}
