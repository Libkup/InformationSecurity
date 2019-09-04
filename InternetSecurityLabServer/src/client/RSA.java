package client;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;

public class RSA {

//	private int p = 61;//1366294046726635493399336680549214898319268492501
//	private int q = 53;//1449849246051449679173689466738734355356891578223
//	private int e = 17;//1248068881904942296572604080834739402838498312531
	private BigInteger p;
	private BigInteger q;
	private BigInteger e;
	private int n;
	public RSA() {
		this.p = new BigInteger("1366294046726635493399336680549214898319268492501");
		this.q = new BigInteger("1449849246051449679173689466738734355356891578223");
		this.e = new BigInteger("1248068881904942296572604080834739402838498312531");
	}

	public static void main(String[] args) throws Exception {
		RSA rsa = new RSA();
		String message = "this is RSA test";
		System.out.println(rsa.privateKey());
		System.out.println("sign " + rsa.signE(rsa.MD5(message)));
		
		message += "@MD5:" + rsa.signE(rsa.MD5(message));
//		rsa.RSAE('A');
//		rsa.RSAD(rsa.RSAE('A'));
		String plainText = rsa.RSAEncryptionWords(message);
//		System.out.println(plainText);
		System.out.println(rsa.verification(plainText));
		String d = rsa.RSADecryptionWords(plainText);
		System.out.println(d);
		
	}

	public BigInteger privateKey(){
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

	public String RSAEncryptionWords(String words){
		String encryption = "";
		char[] wordschar = words.toCharArray();
		for(int i = 0; i < wordschar.length-1; i++){
			encryption = encryption + RSAE(wordschar[i]) + ",";
		}
		encryption = encryption + RSAE(wordschar[wordschar.length-1]);
		return encryption;
	}
	
	public String RSADecryptionWords(String words){
		String decryption = "";
		String[] wordschar = words.split(",");
		for(int i = 0; i < wordschar.length; i++){
			int temp = Integer.valueOf(RSAD(wordschar[i]));
			decryption += (char)temp;
		}
		return decryption;
	}
	
	public String RSAE(char word){
		String wordstr = String.valueOf((int)word);
		BigInteger temp = new BigInteger(wordstr);
		return temp.modPow(e, p.multiply(q)).toString();
	}
	
	public String RSAD(String word){
		BigInteger temp = new BigInteger(word);
		return temp.modPow(privateKey(), p.multiply(q)).toString();
	}
	
	public String MD5(String message) throws Exception{
		try {
	        // MD5加密计算摘要
	        MessageDigest md = MessageDigest.getInstance("MD5");
	        // 计算md5函数
	        md.update(message.getBytes());
	        // 返回md5 hash值，md5 hash值是16位的hex值，返回值就是8位的字符
	        // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示
//	        System.out.println(new BigInteger(1, md.digest()).toString(16));
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
	
	public String verification(String recMessage) throws Exception{
		recMessage = RSADecryptionWords(recMessage);
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
