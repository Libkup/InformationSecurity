package client;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class RSA {

//	private int p = 61;//1366294046726635493399336680549214898319268492501
//	private int q = 53;//1449849246051449679173689466738734355356891578223
//	private int e = 17;//1248068881904942296572604080834739402838498312531
	private static BigInteger p;
	private static BigInteger q;
	private static BigInteger e;
	private int n;
	public RSA() {
		Random rnd = new Random(new Date().getTime());
//		this.p = new BigInteger("1366294046726635493399336680549214898319268492501");
//		this.q = new BigInteger("1449849246051449679173689466738734355356891578223");
//		this.e = new BigInteger("1248068881904942296572604080834739402838498312531");
		this.p = BigInteger.probablePrime(300, rnd);
		this.q = BigInteger.probablePrime(300, rnd);
		this.e = BigInteger.probablePrime(400, rnd);
	}

	public static void main(String[] args) throws Exception {
		RSA rsa = new RSA();
		String message = "this is RSA test";
		System.out.println(rsa.publicKey().toString());
//		System.out.println("sign " + rsa.signE(rsa.MD5(message)));
//		
//		message += "@MD5:" + rsa.signE(rsa.MD5(message));
		
//		rsa.RSAE('A');
//		rsa.RSAD(rsa.RSAE('A'));
		
//		String plainText = rsa.RSAEncryptionWords(message);
//		System.out.println(plainText);
//		System.out.println(rsa.verification(plainText));
//		String d = rsa.RSADecryptionWords(plainText);
//		System.out.println(d);
		System.out.println(rsa.RSADecryptionWords("859197407061759390973288607411124012981898773682497622353914552875826636161162655810366371,73139932274731159913888235192189630279391285117985218131807333808847873618507917233424134,396574916865666178785351414795373256492812692165848290727759158949884890022155867446248109,2317268664499486834590720582170076109374395897652453651254728836238521654422059320587683,1210417818284344513027941592278530275981484139630065895284018856990209922752972655181135896,396574916865666178785351414795373256492812692165848290727759158949884890022155867446248109,441079481651153409740236622116853847524547325548004691524405767667385958213623118478030240,1343016076856930145823774845704312906048859577806501756329689182966772464062981566390083427,394994812838966170725681522715937431552961460311812327839454825064222556827619819261524006,1695350067556751195688895390626208857681961206437425277243010817813515685217901563123863479,1801173077068836848755037729836924789020685866943271565364413113139119248810482210737249314,885156287388874186484479697442346954388858408892212327178644751775781773116324366847486952,1388707114711059327067086698260475463572228721262283202759422533390008462364853705872449462,168508635348480441519776397419784303160493860556297330223131118961031340627176292884354932,208189919833074981505232060648741672917602966150884542179157756386278809959898566518852805,1388707114711059327067086698260475463572228721262283202759422533390008462364853705872449462,1668995263083658544095983117873174176682904860989697046226693440118747654873958307563240734,1451364753528673676048572564031035018629018689223338079259164533836288045538885464497587057,1388707114711059327067086698260475463572228721262283202759422533390008462364853705872449462,1801173077068836848755037729836924789020685866943271565364413113139119248810482210737249314"
				,"1866311667207192066527575277324574480351357908844019833619754344235175398697965681518696811","1290805207608588847508348769871487891027741975250387049677136619474557091000938748218238081417083898860167524004182347130047917340296929639651216418613537995230285284801672102164613"));
////	
//		System.out.println(rsa.getPublicKey());
//		System.out.println(rsa.getPrivateKey());
//		System.out.println(rsa.RSADecryptionWords(rsa.RSAEncryptionWords(message,rsa.getPublicKey().split("-")[0],rsa.getPublicKey().split("-")[1]),rsa.getPrivateKey().split("-")[0],rsa.getPrivateKey().split("-")[1]));
	}

	
	public String getPublicKey(){
		return p.toString() + "-" + publicKey();
	}
	
	public String getPrivateKey(){
		return p.toString() + "-" + privateKey();
	}
	
	public BigInteger publicKey(){
		return e;
	}
	
	public static BigInteger privateKey(){
		BigInteger d;
		BigInteger bigDivisor = p.subtract(new BigInteger("1")).multiply(q.subtract(new BigInteger("1")));
		BigInteger samllDivisor = e;
		BigInteger temp = new BigInteger("0");
		ArrayList<BigInteger> result = new ArrayList<BigInteger>();
		boolean flag = true;
		while(flag){
			if(bigDivisor.remainder(samllDivisor).equals(new BigInteger("1")))
				flag = false;
			result.add(bigDivisor.divide(samllDivisor));
			temp = bigDivisor.remainder(samllDivisor);
			bigDivisor = samllDivisor;
			samllDivisor = temp;
		}
		BigInteger a = new BigInteger("1");
		BigInteger b = result.get(result.size()-1).negate();
		for(int i = result.size()-2; i >= 0; i--){
			BigInteger tempb = b;
			b = a.add(b.multiply(result.get(i).negate()));
			a = tempb;
		}
		if(b.compareTo(new BigInteger("0")) == -1) //b<0
			b = b.add(p.subtract(new BigInteger("1")).multiply(q.subtract(new BigInteger("1"))));
		d = b;
		return d;
	}

	public String RSAEncryptionWords(String words,String n, String e){
		String encryption = "";
		char[] wordschar = words.toCharArray();
		for(int i = 0; i < wordschar.length-1; i++){
			encryption = encryption + RSAE(wordschar[i],n,e) + ",";
		}
		encryption = encryption + RSAE(wordschar[wordschar.length-1],n,e);
		return encryption;
	}
	
	public String RSADecryptionWords(String words,String n, String d){
		String decryption = "";
		String[] wordschar = words.split(",");
		for(int i = 0; i < wordschar.length; i++){
			int temp = Integer.valueOf(RSAD(wordschar[i],n,d));
			decryption += (char)temp;
		}
		return decryption;
	}
	
	public String RSAE(char word,String n, String e){
		String wordstr = String.valueOf((int)word);
		BigInteger temp = new BigInteger(wordstr);
		return temp.modPow(new BigInteger(e), new BigInteger(n)).toString();
	}
	
	public String RSAD(String word,String n, String d){
		BigInteger temp = new BigInteger(word);
		return temp.modPow(new BigInteger(d), new BigInteger(n)).toString();
	}
	
	public String MD5(String message) throws Exception{
		try {
	        // MD5加密计算摘要
	        MessageDigest md = MessageDigest.getInstance("MD5");
	        // 计算md5函数
	        md.update(message.getBytes());
	        // 返回md5 hash值，md5 hash值是16位的hex值，返回值就是8位的字符
	        // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示
	        return new BigInteger(1, md.digest()).toString(16);
	    } catch (Exception e) {
	        throw new Exception("MD5 Encryption errors,"+e.toString());
	    }
	}
	
	public String signE(String sign){
		char[] signs = sign.toCharArray();
		String signedString = "";
		for(int i = 0; i < signs.length; i++){
			String signchar = String.valueOf((int)signs[i]);
			BigInteger temp = new BigInteger(signchar);
			signedString += temp.modPow(privateKey(), p.multiply(q)).toString() + ",";
		}
		return signedString;
	}
	
	public String verification(String recMessage,String n, String d) throws Exception{
		recMessage = RSADecryptionWords(recMessage,n,d);
		String realMessage = recMessage.substring(0,recMessage.indexOf("@MD5:"));
//		System.out.println(realMessage);
		String realMessageMD5 = MD5(realMessage);
//		System.out.println("realMD5 " + realMessageMD5);
		String recMD5 = recMessage.substring(recMessage.indexOf("@MD5:")+5,recMessage.length());
//		System.out.println("recMD5 " + recMD5);
		String md5 = "";
		String[] recMD5s = recMD5.split(",");
		for(int i = 0;i < recMD5s.length; i++){
			BigInteger temp = new BigInteger(recMD5s[i]);
			int c = Integer.valueOf(temp.modPow(e, p.multiply(q)).toString());
			md5 += (char)c;
		}
//		System.out.println("md5 " + md5);
		if(realMessageMD5.equals(md5))
			return realMessage + " true";
		else 
			return "not corrent";
	}
}
