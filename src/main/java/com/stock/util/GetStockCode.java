package com.stock.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.stock.vo.StockCode_BaseVo;

public class GetStockCode {

	public static void main(String argv[]){
		String filePath = "G:\\project\\stock\\data\\SH.SNT";
//      "res/";
		System.out.println("abc".startsWith("b"));
        readTxtFile(filePath);
    }
	public static List<StockCode_BaseVo> readTxtFile(String filePath){
		List<StockCode_BaseVo> list=new ArrayList<StockCode_BaseVo>();
        try {
                String encoding="GBK";
                File file=new File(filePath);
                if(file.isFile() && file.exists()){ //判断文件是否存在
                    InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file),encoding);//考虑到编码格式
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String lineTxt = null;
                    while((lineTxt = bufferedReader.readLine()) != null){
                    	String tmp1[]=lineTxt.split("\t");
                    	if(StringUtils.isNotEmpty(tmp1[0])&& (tmp1[0].startsWith("00")||tmp1[0].startsWith("300")||tmp1[0].startsWith("60"))){
                    		StockCode_BaseVo vo=new StockCode_BaseVo(tmp1[0],tmp1[1]);
                    		if(tmp1[0].startsWith("00")||tmp1[0].startsWith("300")){
                    			vo.setPlace("sz");
                    		}if(tmp1[0].startsWith("60")){
                    			vo.setPlace("sh");
                    		}
                    		list.add(vo);
                    	}
                       
                    }
                    read.close();
        }else{
            System.out.println("找不到指定的文件");
        }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return list;
     
    }
}
