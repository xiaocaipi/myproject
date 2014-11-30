package org.bigfenbushi.http.testserialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
//java的序列化 和hessian 差不多
public class TestJavaSerialization {
	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		Person zhangsan = new Person();
		zhangsan.setAddress("hangzhou");
		zhangsan.setAge(30);
		zhangsan.setBirth(new Date());
		zhangsan.setName("zhangsan");
		//字节数组的输出流
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		//将字节数组输出流封装到对象输出流中
		ObjectOutputStream out = new ObjectOutputStream(os);
		//将zhangsan输出到字节数组
		out.writeObject(zhangsan);
		byte [] zhangsanByte = os.toByteArray();
		
		//字节数组输入流
		ByteArrayInputStream is = new ByteArrayInputStream(zhangsanByte);
		//hessian的反序列化读取对象
		ObjectInputStream in = new ObjectInputStream(is);
		Person person = (Person)in.readObject();
		System.out.println("name :"+ person.getName());
		
	}
	
}
