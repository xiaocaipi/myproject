package org.bigfenbushi.http.testserialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

//hessian http://blog.csdn.net/zhtang0526/article/details/4788879
//Java远程通讯可选技术及原理  http://itlab.idcquan.com/Java/base/740383.html
public class TestHessianSerialization {
	
	
	public static void main(String[] args) throws IOException {
		Person zhangsan = new Person();
		zhangsan.setAddress("hangzhou");
		zhangsan.setAge(30);
		zhangsan.setBirth(new Date());
		zhangsan.setName("zhangsan");
		//字节数组的输出流
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		//使用hessian来封装
		HessianOutput ho = new HessianOutput(os);
		//通过writeobject 将其输出到数组
		ho.writeObject(zhangsan);
		byte [] zhangsanByte = os.toByteArray();
		//又从字节数组进行读取
		ByteArrayInputStream is = new ByteArrayInputStream(zhangsanByte);
		//hessian的反序列化读取对象
		HessianInput hi = new HessianInput(is);
		Person person = (Person)hi.readObject();
		System.out.println("name :"+ person.getName());
		
	}
	
}
