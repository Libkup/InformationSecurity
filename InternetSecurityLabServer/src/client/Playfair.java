package client;

public class Playfair {

	private char[][] matrix;
	private String key = "";
	
	public Playfair(String key) {
		this.key = key;
	}
	
	public static void main(String[] args){
		Playfair p = new Playfair("MYSECRETKEY");
		p.PlayfairEncryption("thank you");
		System.out.println(p.PlayfairDecryption(p.PlayfairEncryption("thank yous")));
	}
	
	public void Matrix(String str){
		matrix = new char[7][4];
		char[] strs = str.toUpperCase().toCharArray();
		int k = 0;
		for(int i = 0;i < strs.length; i++){
			for(int j = 0; j < i; j++)
				if(strs[j] == strs[i])
					strs[i] = ' ';
		}
		boolean flag = true;
		char letter = 'A';
		for(int i = 0;i < 7; i++){
			for(int j = 0; j < 4; j++){
				if(flag){
					if(strs[k] != ' '){
						matrix[i][j] = strs[k];
						k++;
					}
					else{
						j--;
						k++;
					}
					if(k == strs.length)
						flag = false;
				}
				else{
					boolean in = true;
					while(in){
						int m;
						for(m = 0;m < strs.length; m++)
							if(letter == strs[m]){
								in = true;
								letter ++;
								break;
							}
								
						if(m == strs.length)
							in = false;
					}
					
					matrix[i][j] = letter;
					letter ++;
					
				}
			}
		}
		matrix[6][3] = '-';
		matrix[6][2] = '>';
	}
	
	public String PlayfairEncryption(String line){
		if(matrix == null)
			Matrix(key);
		char[] chars = line.toUpperCase().toCharArray();
		char p1;
		char p2;
		String Estr = "";
		boolean p2Null = false;
		for(int i = 0; i <= chars.length-1; i = i + 2){
			p1 = chars[i];
			if(i+2>chars.length){
				p2 = 'X';
			}
				
			else
				p2 = chars[i+1];
			if(p1 == p2){
				p2 = 'X';
				i--;
			}
			if(p1 == ' '){
				Estr += ' ';
				i--;
				continue;
			}
			if(p2 == ' '){
//				Estr += ' ';
				p2Null = true;
				p2 = chars[i+2];
				i++;
			}
				
//			System.out.println(p1+" "+p2);
			int p1Row = whichRow(p1,matrix);
			int p1Col = whichCol(p1,matrix);
			int p2Row = whichRow(p2,matrix);
			int p2Col = whichCol(p2,matrix);
//			System.out.println(p1Row+" "+p1Col+" "+p2Row+" "+p2Col);
			char[] E = Echar(p1Row,p1Col,p2Row,p2Col, matrix);
//			System.out.println(E[0] + " " + E[1]);
			Estr += E[0];
			if(p2Null){
				Estr += " ";
				p2Null = false;
			}
			Estr += E[1];
			
			
		}
		return Estr;
	}
	
	public String PlayfairDecryption(String line){
		if(matrix == null)
			Matrix(key);
		char[] chars = line.toUpperCase().toCharArray();
		char p1;
		char p2;
		String Dstr = "";
		boolean p2Null = false;
		for(int i = 0; i <= chars.length-1; i = i + 2){
			p1 = chars[i];
			if(i+2>chars.length){
				p2 = 'X';
//				System.out.println(1111111);
			}
				
			else
				p2 = chars[i+1];
			if(p1 == p2){
				p2 = 'X';
				i--;
			}
			if(p1 == ' '){
				Dstr += ' ';
				i--;
				continue;
			}
			if(p2 == ' '){
//				Dstr += ' ';
				p2Null = true;
				p2 = chars[i+2];
				i++;
			}
				
//			System.out.println(p1+" "+p2);
			int p1Row = whichRow(p1,matrix);
			int p1Col = whichCol(p1,matrix);
			int p2Row = whichRow(p2,matrix);
			int p2Col = whichCol(p2,matrix);
//			System.out.println(p1Row+" "+p1Col+" "+p2Row+" "+p2Col);
			char[] E = Dchar(p1Row,p1Col,p2Row,p2Col, matrix);
//			System.out.println(E[0] + " " + E[1]);
			Dstr += E[0];
			if(p2Null){
				Dstr += " ";
				p2Null = false;
			}
			Dstr += E[1];
			
//			System.out.println(i);
		}
		return Dstr;
	}
	
	public char[] Dchar(int p1Row,int p1Col,int p2Row,int p2Col,char[][] matrix) {
		if(p1Row == p2Row){
			if(p1Col == 0)
				p1Col = 3;
			else
				p1Col -= 1;
			if(p2Col == 0)
				p2Col = 3;
			else
				p2Col -= 1;
		}else if(p1Col == p2Col){
			if(p1Row == 0)
				p1Row = 6;
			else
				p1Row -= 1;
			if(p2Row == 0)
				p2Row = 6;
			else
				p2Row -= 1;
		}else{
			int temp = p1Col;
			p1Col = p2Col;
			p2Col = temp;	
		}
		
		char[] D = new char[2];
//		System.out.println(matrix[p1Row][p1Col] + " " + matrix[p2Row][p2Col]);
		D[0] = matrix[p1Row][p1Col];
		D[1] = matrix[p2Row][p2Col];
//		System.out.println(E[0] + " " + matrix[p2Row][p2Col]);
		return D;
	}
	
	public char[] Echar(int p1Row,int p1Col,int p2Row,int p2Col,char[][] matrix) {
		if(p1Row == p2Row){
			if(p1Col == 3)
				p1Col = 0;
			else
				p1Col += 1;
			if(p2Col == 3)
				p2Col = 0;
			else
				p2Col += 1;
		}else if(p1Col == p2Col){
			if(p1Row == 6)
				p1Row = 0;
			else
				p1Row += 1;
			if(p2Row == 6)
				p2Row = 0;
			else
				p2Row += 1;
		}else{
			int temp = p1Col;
			p1Col = p2Col;
			p2Col = temp;	
		}
		
		char[] E = new char[2];
//		System.out.println(matrix[p1Row][p1Col] + " " + matrix[p2Row][p2Col]);
		E[0] = matrix[p1Row][p1Col];
		E[1] = matrix[p2Row][p2Col];
//		System.out.println(E[0] + " " + matrix[p2Row][p2Col]);
		return E;
	}
	
	public int whichRow(char c, char[][] matrix){
		
		for(int i = 0; i < 7; i++)
			for(int j = 0; j < 4; j++)
				if(matrix[i][j] == c)
					return i;
		return 0;
	}
	
	public int whichCol(char c, char[][] matrix){
		
		for(int i = 0; i < 7; i++)
			for(int j = 0; j < 4; j++)
				if(matrix[i][j] == c)
					return j;
		return 0;
	}
}
