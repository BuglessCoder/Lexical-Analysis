package compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TextLex{
	
	/**
	 * 标识符的种别码为1，常数为2，保留字为3，运算符为4，界符为5。
	 */
	
	//待检测代码字符串
	private String text;
	
	//用于存放结果/错误/符号表的容器
	private Map<String, String> result = new HashMap<String, String>();
	private Map<String, String> error = new HashMap<String, String>();
	private ArrayList<String> symbolTable = new ArrayList<String>();
	
	private int text_length;			//单行文本长度
	private int row_number = 1;		//行数
	
	//关键字集合（只选取了一部分，是Java所有保留字的子集）
	String[] Key = {"public","private", "class", "static", "void", "main",
                "int", "float", "double", "char", "String","System",  "out",
                "print", "println", "Scanner", "in", "else", "if", "for", "while", "return" ,"true", "false"};
	
	public TextLex(String text){
		this.text = text;
		text_length = text.length();
	}

	//判断某字符是否是字母或下划线（即标识符的合法命名）
	public int isLetter(char c){
		if(((c<='z')&&(c>='a')) || ((c<='Z')&&(c>='A')) || (c=='_')){
			return 1;
		}
		else {
			return 0;
		}
	}
	
	//判断某字符是否是数字
	public int isNumber(char c){
		if((c>='0')&&(c<='9')){
			return 1;
		}
		else {
			return 0;
		}	
	}
	
	//判断某字符是否是保留字
	public int isKey(String t){
		for(int i=0;i<Key.length;i++){
			if (t.equals(Key[i])) {
				return 1;
			}
		}
		return 0;
	}
	
	// 扫描整个字符串
	public void scannerAll(){
		int i = 0;
		char c;
		while(i < text_length){
			c = text.charAt(i);
			if(c == ' ' || c == '\t')
				i++;
			else if (c == '\r' || c == '\n') {
				row_number++;
				i++;
			}
			else {
				i = scannerPart(i);
			}			
		}
	}
	
	//主要的逻辑判断函数
	public int scannerPart(int arg0){
		int i = arg0;
		char ch = text.charAt(i);
		String s = "";
		
		// 当前字符是字母
		if (isLetter(ch) == 1) {
			s = ch + "";
			int a = handleFirstLetter(i, s);
			return a;
		}		
		// 当前字符是数字
		else if (isNumber(ch) == 1) {
			s = ch + "";
			return handleFirstNum(i, s);	
		}		
		// 既不是数字也不是字母
		else {
			s = ch + "";
			switch (ch) {				
				case '[':
				case ']':
				case '(':
				case ')':
				case '{':
				case '}':
				case '\'':
				case '\"':
				case ':':
				case ',':
				case '.':
				case ';':
					printResult(s, "5");	//界符
					return ++i;	
				//判定运算符
				case '+':
					return handlePlus(i, s);
				case '-':
					return handleMinus(i, s);
				case '*':
				case '/':
					if(text.charAt(i+1)=='*'){
						return handleNote(i, s);
					}
					else if (text.charAt(i+1)=='/') {
						return handleSingleLineNote(i,s);
					}
				case '!':
				case '=':
					return handleEquals(i, s);
				case '>':
					return handleMore(i, s);
				case '<':
					return handleLess(i, s);
				default:
					// 输出暂时无法识别的字符
					printError(row_number, s, "暂时无法识别");
					return ++i;
			}
		}
	}
	
	//处理首字母
	public int handleFirstLetter(int arg, String arg0){
		int i = arg;
		String s = arg0;
		char ch = text.charAt(++i);
		while(isLetter(ch) == 1 || isNumber(ch) == 1){
			s = s+ch;
			ch = text.charAt(++i);
		}
		if(s.length() == 1){
			symbolTable.add(s);
			printResult(symbolTable.indexOf(s)+"", "1");	//字符常数
			return i;
		}
		
		// 到了结尾
		if(isKey(s) == 1){
			// 输出key
			printResult(s, "3");	//保留字
			return i;
		}
		else {
			// 输出普通的标识符
			symbolTable.add(s);
			printResult(symbolTable.indexOf(s)+"", "1");	//普通标识符
			return i;
		}
	}
	
	//处理数字
	public int handleFirstNum(int arg, String arg0){
		int i = arg;
		char ch = text.charAt(++i);
		String s = arg0;
		while(isNumber(ch)==1){
			s = s+ch;
			ch = text.charAt(++i);
		}
		if((text.charAt(i)==' ')||(text.charAt(i)=='\t')||(text.charAt(i)=='\n')||(text.charAt(i)=='\r')||ch==';'||ch==','){
			// 到了结尾，输出数字
			printResult(s, "2");
			return ++i;
		}
		else if (ch=='+'||ch=='-'||ch=='*'||ch=='/') {
			printResult(s, "2");
			return ++i;
		}
		else {
			do {
				ch = text.charAt(i++);
				s = s+ch;
			} while ((text.charAt(i)!=' ')&&(text.charAt(i)!='\t')&&(text.charAt(i)!='\n')&&(text.charAt(i)!='\r'));
			printError(row_number, s, "错误的标识符");
			return ++i;
		}
	}
	
	//处理加号(判断是+=/++/+)
	public int handlePlus(int arg, String arg0){
		int i = arg;
		char ch = text.charAt(++i);
		String s = arg0;
		if (ch == '+'){
			s = s+ch;
			printResult(s, "4");
			return ++i;
		}
		else if(ch == '='){
			s = s+ch;
			printResult(s, "4");
			return ++i;
		}	
		else{ 
			printResult(s, "4");
			return i;
		}
	}
	
	// 处理减号(判断是-=/--/-)
	public int handleMinus(int arg, String arg0){
		int i = arg;
		char ch = text.charAt(++i);
		String s = arg0;
		if (ch == '-'){
			s = s+ch;
			printResult(s, "4");
			return ++i;
		}		
		else if(ch=='='){
			s = s+ch;
			printResult(s, "4");
			return ++i;
		}	
		else{
			printResult(s, "4");
			return i;
		}
	}
	
	//处理=(判断是=/==)
	public int handleEquals(int arg, String arg0){
		int i = arg;
		char ch = text.charAt(++i);
		String s = arg0;
		if (ch == '=') {
			s = s+ch;
			printResult(s, "4");
			return ++i;
		}
		else{
			printResult(s, "4");
			return i;
		}
	}
	
	//处理>(判断是>/>=)
	public int handleMore(int arg, String arg0){
		int i=arg;
		char ch = text.charAt(++i);
		String s = arg0;
		if (ch == '='){
			s = s+ch;
			printResult(s, "4");
			return ++i;
		}	
		else{
			printResult(s, "4");
			return i;
		}
	}
	
	//处理<(判断是</<=)
	public int handleLess(int arg, String arg0){
		int i=arg;
		String s = arg0;
		char ch = text.charAt(++i);
		if (ch == '='){
			s = s+ch;
			printResult(s, "4");
			return ++i;
		}	
		else{
			printResult(s, "4");
			return i;
		}
	}
	
	//处理单行注释
	public int handleSingleLineNote(int arg, String arg0){
		String s = arg0;
		int i = arg;
		char ch = text.charAt(++i);
		while (ch!='\r' && ch!='\n') {
			s+=ch;
			ch = text.charAt(++i);			
		}
		printResult(s, "单行注释");
		return i;
	}
	
	// 处理块注释（没有考虑不闭合的情况）
	public int handleNote(int arg, String arg0){
		int i = arg;
		char ch = text.charAt(++i);
		String s = arg0+ch;
		ch = text.charAt(++i);
		while (ch != '*' || ((i+1)<text_length) && text.charAt(i+1)!='/') {
			s = s+ch;
			if (ch=='\r'||ch=='\n') {
				row_number++;
			}
			ch = text.charAt(++i);
		}
		s = s+"*/";
		printResult(s, "块注释");
		return i+2;
	}	
	
	//打印结果
	public void printResult(String rs_value, String rs_name){
		result.clear();
		result.put(rs_name, rs_value);
		for(Map.Entry<String, String> entry : result.entrySet()){  
		    System.out.println("("+entry.getKey()+", \'"+entry.getValue()+"\')");  
		}
	}
	
	//打印错误
	public void printError(int row_num, String rs_value, String rs_name) {
		error.put(rs_name, rs_value);
		for(Map.Entry<String, String> entry : error.entrySet()){  
			System.out.println("Error in line "+ row_num + ": " + entry.getKey() + " \'" + entry.getValue() + "\'");  
		}
	}
}
