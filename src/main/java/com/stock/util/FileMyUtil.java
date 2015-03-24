package com.stock.util;


import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class FileMyUtil {

	public  static void print(String content,String filepath) throws IOException{
		if(!new File(CommonUtil.outputpath).exists()){
			FileUtils.forceMkdir(new File(CommonUtil.outputpath));;
		}
		File file=new File(CommonUtil.outputpath+filepath+".txt");
		FileUtils.writeStringToFile(file, content, "utf-8");
	}
	
	public static void delete(File file) {
	      try {
	          if (file.exists()) {
	              if (file.isFile()) {
	                  file.delete();
	              } else if (file.isDirectory()) {
	                  File files[] = file.listFiles();
	                  for (int i = 0; i < files.length; i++) {
	                      delete(files[i]);
	                  }
	                  file.delete();
	              }
	          } else {
	              System.out.println("所删除的文件不存在！" + '\n');
	          }
	      } catch (Exception e) {
	          System.out.print("unable to delete the folder!");
	      }
	  }
	
	public static void main(String[] args) throws IOException {
	
	}

}
