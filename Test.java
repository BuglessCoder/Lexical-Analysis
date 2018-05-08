package compiler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Test {

	public static void main(String[] args) {
		// TODO 自动生成的方法存根	
		File file = new File("/Users/lidawei/Desktop/CodeText.java");
		InputStream in = null;
        InputStreamReader ir = null;
        BufferedReader br = null;
       
        try {
        	in = new BufferedInputStream(new FileInputStream(file));
			ir = new InputStreamReader(in,"utf-8");
			br= new BufferedReader(ir);
			String line = "";
			List<String> list = new ArrayList<String>();
			 //一行一行读取
            while((line = br.readLine()) != null){
                list.add(line);
            }
            //将集合转换成数组
            String[] arr = list.toArray(new String[list.size()]);
            String text = "";
            for(String s : arr){
            	if(s != arr[arr.length-1]){
            		text = text + s + "\n";
            	}
            	else{
            		text = text + s;
            	}
            	//System.out.println(s);
            }
            //System.out.println(text);
            //System.out.println(text.length());
           
            TextLex textLex = new TextLex(text);
            textLex.scannerAll();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}finally{
			try {
				if(br!=null){
                    br.close();
                }
                if(ir!=null){
                    ir.close();
                }
                if(in!=null){
                    in.close();
                }
			}catch (Exception e2) {
           }
		}
	}

}
