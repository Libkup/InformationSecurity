import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 */

/**
 * @author Li Benknag
 *
 */
public class Combinations {
	private static int low = 4;
	private static int high = 4;
	/**
	 * 
	 */
	public Combinations() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		MD5 md5 = new MD5();
		FileWriter user = new FileWriter("user.txt");
		BufferedWriter userWriter = new BufferedWriter(user);
		FileWriter userHash = new FileWriter("userHash.txt");
		BufferedWriter userHashWriter = new BufferedWriter(userHash);
		FileWriter password = new FileWriter("password.txt");
		BufferedWriter passwordWriter = new BufferedWriter(password);
		FileWriter passwordHash = new FileWriter("passwordHash.txt");
		BufferedWriter passwordHashWriter = new BufferedWriter(passwordHash);
		String userStr = "";
		String passwordStr = "";
		for(int i = low; i <= high; i++){
			char[] w = new char[i];
			for(int j = 0; j < w.length; j++)
				w[j] = '0';
			int j = i - 1;
			int flag = 1;
			while(flag == 1){
				if (w[j] == '9')
					w[j] = 'a';
				else if (w[j] == 'z') {
					int count = 1;
					for (int k = i - 1; k >= 0; k--) {
						int l = 0;
						for(l = 0; l < i; l++){
							if(w[l] != 'z')
								break;
						}
						if(l == i)
							flag = 0;
						if (w[k] == '9') {
							w[k] = 'a';
							count = 0;
						} else if (w[k] == 'z' && count == 1){
							w[k] = '0';
							count = 1;
						}
						else {
							w[k] += count;
							count = 0;
						}
					}
					w[j] = '0';
				}
				else
					w[j]++;
				userStr = new String(w);
				passwordStr = userStr;
//				for(int l = 0; l < w.length; l++){
//					userStr += w[l];
//					passwordStr = userStr;
//				}
				userWriter.write(userStr + "\n");
				userWriter.flush();
				
				passwordWriter.write(passwordStr + "\n");
				passwordWriter.flush();
				
				userHashWriter.write(md5.digest(userStr) + "\n");
				userHashWriter.flush();
				
				passwordHashWriter.write(md5.digest(passwordStr) + "\n");
				passwordHashWriter.flush();
				
				System.out.println(userStr);
			}
		}
		userWriter.close();
		passwordWriter.close();
		userHashWriter.close();
		passwordHashWriter.close();
	}

}
