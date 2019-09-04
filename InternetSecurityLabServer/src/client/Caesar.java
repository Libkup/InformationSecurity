package client;

public class Caesar {

	private int k;
	
	public Caesar(int k) {
		this.k = k;
	}

	public String CaesarEncryption(String str){
		char[] chars = str.toCharArray();
		char[] charsE = new char[chars.length];
		String E = "";
		for(int i = 0;i < chars.length; i++){
			charsE[i] = encrypt(chars[i]);
		}
		for(int i = 0;i < chars.length; i++){
			E += charsE[i];
		}
		return E;
	}
	
	public String CaesarDecryption(String str) {
	      k = 0 - k;
	      return CaesarEncryption(str);
	}
	
	private char encrypt(char c) {
		// 如果字符串中的某个字符是小写字母
		if (c >= 'a' && c <= 'z'){
			c += k % 26; 
			if (c < 'a')
				c += 26;
			else if (c > 'z')
				c -= 26; 
		} 
		else if (c >= 'A' && c <= 'Z'){ 
			c += k % 26; 
			if (c < 'A')
				c += 26;
			else if (c > 'Z')
				c -= 26;
		} 
		
		return c; 
	}
	
	
}
