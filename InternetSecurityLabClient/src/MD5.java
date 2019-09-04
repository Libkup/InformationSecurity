

/**
 * @author dell
 *
 */
public class MD5 {

    static final String hexs[]={"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
    //��׼�Ļ���
    private static final long A=0x67452301L;
    private static final long B=0xefcdab89L;
    private static final long C=0x98badcfeL;
    private static final long D=0x10325476L;


    //
    static final int S11 = 7;
    static final int S12 = 12;
    static final int S13 = 17;
    static final int S14 = 22;

    static final int S21 = 5;
    static final int S22 = 9;
    static final int S23 = 14;
    static final int S24 = 20;

    static final int S31 = 4;
    static final int S32 = 11;
    static final int S33 = 16;
    static final int S34 = 23;

    static final int S41 = 6;
    static final int S42 = 10;
    static final int S43 = 15;
    static final int S44 = 21;

    //java��֧���޷��ŵĻ������ݣ�unsigned��
    private long [] result={A,B,C,D};//�洢hash�������4��32=128λ����ʼ��ֵΪ�������ļ�����

    static long blen;//���ڴ洢�����ܵĳ���
    /**
     * ���Ժ������趨
     * @param args
     */
    public static void main(String []args){
        MD5 md=new MD5();
        System.out.println("md5(abc)="+md.digest("abcdefshasdhsajdhasjdhkjwjnscsjaksjidljwik123"));
        System.out.println("md5(abc)="+md.digest("abcdefshasdhsaj456SSD"));
 
    }

 
    public String digest(String inputStr){
    	result[0] = A;
    	result[1] = B;
    	result[2] = C;
    	result[3] = D;
        byte [] inputBytes=inputStr.getBytes();
        int byteLen=inputBytes.length;//���ȣ��ֽڣ�
        int groupCount=0;//��������ĸ���
        groupCount=byteLen/64;//ÿ��512λ��64�ֽڣ�
        long []groups=null;//ÿ��С��(64�ֽ�)��ϸ�ֺ��16��С��(4�ֽ�)

        /*
         * ÿ���Ĵδ���
         * ������������ִ�У��������ִ�м���trans
         * �ڻ�����transִ����֮�������������trans�Ĵ���
         */
        //����ÿһ������ ����
        for(int step=0;step<groupCount;step++){
            groups=divGroup(inputBytes,step*64);
            trans(groups);//������飬�����㷨
        }

        //��������������β��
        int rest=byteLen%64;//512λ����������
        byte [] tempBytes=new byte[64];
        /*
         * ��ʣ������С��448bitsʱ
         */
        if(rest<=56){
            for(int i=0;i<rest;i++)
                tempBytes[i]=inputBytes[byteLen-rest+i];//�������
            if(rest<56){
                tempBytes[rest]=(byte)(1<<7);
                for(int i=1;i<56-rest;i++)
                    tempBytes[rest+i]=0;
            }
            long len=(long)(byteLen<<3);
            /*
             * ���8λ�Ĵ���
             */
            for(int i=0;i<8;i++){
                tempBytes[56+i]=(byte)(len&0xFFL);
                len=len>>8;
            }
            groups=divGroup(tempBytes,0);
            trans(groups);//�������
            }
        /*
         * ʣ������ݴ���448bits
         */
        else{
            for(int i=0;i<rest;i++)
                tempBytes[i]=inputBytes[byteLen-rest+i];//ʣ�����ݵĸ�ֵ
            tempBytes[rest]=(byte)(1<<7);//���Ʋ���
            for(int i=rest+1;i<64;i++)
                tempBytes[i]=0;//δ�����������0��������512bits
            //����448 С��512��һ������Ĵ���
            groups=divGroup(tempBytes,0);
            
            trans(groups);//�������

            //���һ��448+64�ķ��鴦��
            for(int i=0;i<56;i++)
                tempBytes[i]=0;//���Ӳ���448bits
            long len=(long)(byteLen<<3);
            for(int i=0;i<8;i++){
                tempBytes[56+i]=(byte)(len&0xFFL);
                len=len>>8;
            }
            groups=divGroup(tempBytes,0);
            trans(groups);//�������
        }

        //��Hashֵת����ʮ�����Ƶ��ַ���
        String resStr="";
        long temp=0;
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                temp=result[i]&0x0FL;
                String a=hexs[(int)(temp)];
                result[i]=result[i]>>4;
                temp=result[i]&0x0FL;
                resStr+=hexs[(int)(temp)]+a;
                result[i]=result[i]>>4;
            }
        }
        return resStr;
    }

    /**
     * ��inputBytes��index��ʼȡ512λ����Ϊ�µķ���
     * ��ÿһ��512λ�ķ�����ϸ�ֳ�16��С�飬ÿ��С��64λ��8���ֽڣ�
     * @param inputBytes
     * @param index
     * @return
     */
    private static long[] divGroup(byte[] inputBytes,int index){
        long [] temp=new long[16];
        for(int i=0;i<16;i++){
            temp[i]=b2iu(inputBytes[4*i+index])|
                (b2iu(inputBytes[4*i+1+index]))<<8|
                (b2iu(inputBytes[4*i+2+index]))<<16|
                (b2iu(inputBytes[4*i+3+index]))<<24;
        }
        return temp;
    }

    /**
     * ��ʱ�����ڷ���λ������λ�洢�����Ǵ�����������������Ҫ����һ��
     * @param b
     * @return
     */
    public static long b2iu(byte b){
        return b < 0 ? b & 0x7F + 128 : b;
     }

    /**
     * ��Ҫ�Ĳ���������ѭ��
     * @param groups[]--ÿһ������512λ��64�ֽڣ�
     */
    private void trans(long[] groups) {
        long a = result[0], b = result[1], c = result[2], d = result[3];
        /*��һ��*/
        a = FF(a, b, c, d, groups[0], S11, 0xd76aa478L); /* 1 */
        d = FF(d, a, b, c, groups[1], S12, 0xe8c7b756L); /* 2 */
        c = FF(c, d, a, b, groups[2], S13, 0x242070dbL); /* 3 */
        b = FF(b, c, d, a, groups[3], S14, 0xc1bdceeeL); /* 4 */
        a = FF(a, b, c, d, groups[4], S11, 0xf57c0fafL); /* 5 */
        d = FF(d, a, b, c, groups[5], S12, 0x4787c62aL); /* 6 */
        c = FF(c, d, a, b, groups[6], S13, 0xa8304613L); /* 7 */
        b = FF(b, c, d, a, groups[7], S14, 0xfd469501L); /* 8 */
        a = FF(a, b, c, d, groups[8], S11, 0x698098d8L); /* 9 */
        d = FF(d, a, b, c, groups[9], S12, 0x8b44f7afL); /* 10 */
        c = FF(c, d, a, b, groups[10], S13, 0xffff5bb1L); /* 11 */
        b = FF(b, c, d, a, groups[11], S14, 0x895cd7beL); /* 12 */
        a = FF(a, b, c, d, groups[12], S11, 0x6b901122L); /* 13 */
        d = FF(d, a, b, c, groups[13], S12, 0xfd987193L); /* 14 */
        c = FF(c, d, a, b, groups[14], S13, 0xa679438eL); /* 15 */
        b = FF(b, c, d, a, groups[15], S14, 0x49b40821L); /* 16 */

        /*�ڶ���*/
        a = GG(a, b, c, d, groups[1], S21, 0xf61e2562L); /* 17 */
        d = GG(d, a, b, c, groups[6], S22, 0xc040b340L); /* 18 */
        c = GG(c, d, a, b, groups[11], S23, 0x265e5a51L); /* 19 */
        b = GG(b, c, d, a, groups[0], S24, 0xe9b6c7aaL); /* 20 */
        a = GG(a, b, c, d, groups[5], S21, 0xd62f105dL); /* 21 */
        d = GG(d, a, b, c, groups[10], S22, 0x2441453L); /* 22 */
        c = GG(c, d, a, b, groups[15], S23, 0xd8a1e681L); /* 23 */
        b = GG(b, c, d, a, groups[4], S24, 0xe7d3fbc8L); /* 24 */
        a = GG(a, b, c, d, groups[9], S21, 0x21e1cde6L); /* 25 */
        d = GG(d, a, b, c, groups[14], S22, 0xc33707d6L); /* 26 */
        c = GG(c, d, a, b, groups[3], S23, 0xf4d50d87L); /* 27 */
        b = GG(b, c, d, a, groups[8], S24, 0x455a14edL); /* 28 */
        a = GG(a, b, c, d, groups[13], S21, 0xa9e3e905L); /* 29 */
        d = GG(d, a, b, c, groups[2], S22, 0xfcefa3f8L); /* 30 */
        c = GG(c, d, a, b, groups[7], S23, 0x676f02d9L); /* 31 */
        b = GG(b, c, d, a, groups[12], S24, 0x8d2a4c8aL); /* 32 */

        /*������*/
        a = HH(a, b, c, d, groups[5], S31, 0xfffa3942L); /* 33 */
        d = HH(d, a, b, c, groups[8], S32, 0x8771f681L); /* 34 */
        c = HH(c, d, a, b, groups[11], S33, 0x6d9d6122L); /* 35 */
        b = HH(b, c, d, a, groups[14], S34, 0xfde5380cL); /* 36 */
        a = HH(a, b, c, d, groups[1], S31, 0xa4beea44L); /* 37 */
        d = HH(d, a, b, c, groups[4], S32, 0x4bdecfa9L); /* 38 */
        c = HH(c, d, a, b, groups[7], S33, 0xf6bb4b60L); /* 39 */
        b = HH(b, c, d, a, groups[10], S34, 0xbebfbc70L); /* 40 */
        a = HH(a, b, c, d, groups[13], S31, 0x289b7ec6L); /* 41 */
        d = HH(d, a, b, c, groups[0], S32, 0xeaa127faL); /* 42 */
        c = HH(c, d, a, b, groups[3], S33, 0xd4ef3085L); /* 43 */
        b = HH(b, c, d, a, groups[6], S34, 0x4881d05L); /* 44 */
        a = HH(a, b, c, d, groups[9], S31, 0xd9d4d039L); /* 45 */
        d = HH(d, a, b, c, groups[12], S32, 0xe6db99e5L); /* 46 */
        c = HH(c, d, a, b, groups[15], S33, 0x1fa27cf8L); /* 47 */
        b = HH(b, c, d, a, groups[2], S34, 0xc4ac5665L); /* 48 */

        /*������*/
        a = II(a, b, c, d, groups[0], S41, 0xf4292244L); /* 49 */
        d = II(d, a, b, c, groups[7], S42, 0x432aff97L); /* 50 */
        c = II(c, d, a, b, groups[14], S43, 0xab9423a7L); /* 51 */
        b = II(b, c, d, a, groups[5], S44, 0xfc93a039L); /* 52 */
        a = II(a, b, c, d, groups[12], S41, 0x655b59c3L); /* 53 */
        d = II(d, a, b, c, groups[3], S42, 0x8f0ccc92L); /* 54 */
        c = II(c, d, a, b, groups[10], S43, 0xffeff47dL); /* 55 */
        b = II(b, c, d, a, groups[1], S44, 0x85845dd1L); /* 56 */
        a = II(a, b, c, d, groups[8], S41, 0x6fa87e4fL); /* 57 */
        d = II(d, a, b, c, groups[15], S42, 0xfe2ce6e0L); /* 58 */
        c = II(c, d, a, b, groups[6], S43, 0xa3014314L); /* 59 */
        b = II(b, c, d, a, groups[13], S44, 0x4e0811a1L); /* 60 */
        a = II(a, b, c, d, groups[4], S41, 0xf7537e82L); /* 61 */
        d = II(d, a, b, c, groups[11], S42, 0xbd3af235L); /* 62 */
        c = II(c, d, a, b, groups[2], S43, 0x2ad7d2bbL); /* 63 */
        b = II(b, c, d, a, groups[9], S44, 0xeb86d391L); /* 64 */

        /*���뵽֮ǰ����Ľ������*/
        result[0] += a;
        result[1] += b;
        result[2] += c;
        result[3] += d;
        result[0]=result[0]&0xFFFFFFFFL;
        result[1]=result[1]&0xFFFFFFFFL;
        result[2]=result[2]&0xFFFFFFFFL;
        result[3]=result[3]&0xFFFFFFFFL;
    }

    /**
     * �����Ǵ���Ҫ�õ������Ժ���
     */
    private static long F(long x, long y, long z) {
        return (x & y) | ((~x) & z);
    }

    private static long G(long x, long y, long z) {
        return (x & z) | (y & (~z));
    }

    private static long H(long x, long y, long z) {
        return x ^ y ^ z;
    }

    private static long I(long x, long y, long z) {
        return y ^ (x | (~z));
    }

    private static long FF(long a, long b, long c, long d, long x, long s,
            long ac) {
        a += (F(b, c, d)&0xFFFFFFFFL) + x + ac;
        a = ((a&0xFFFFFFFFL)<< s) | ((a&0xFFFFFFFFL) >>> (32 - s));
        a += b;
        return (a&0xFFFFFFFFL);
    }

    private static long GG(long a, long b, long c, long d, long x, long s,
            long ac) {
        a += (G(b, c, d)&0xFFFFFFFFL) + x + ac;
        a = ((a&0xFFFFFFFFL) << s) | ((a&0xFFFFFFFFL) >>> (32 - s));
        a += b;
        return (a&0xFFFFFFFFL);
    }

    private static long HH(long a, long b, long c, long d, long x, long s,
            long ac) {
        a += (H(b, c, d)&0xFFFFFFFFL) + x + ac;
        a = ((a&0xFFFFFFFFL) << s) | ((a&0xFFFFFFFFL) >>> (32 - s));
        a += b;
        return (a&0xFFFFFFFFL);
    }

    private static long II(long a, long b, long c, long d, long x, long s,
            long ac) {
        a += (I(b, c, d)&0xFFFFFFFFL) + x + ac;
        a = ((a&0xFFFFFFFFL) << s) | ((a&0xFFFFFFFFL) >>> (32 - s));
        a += b;
        return (a&0xFFFFFFFFL);
    }
}

