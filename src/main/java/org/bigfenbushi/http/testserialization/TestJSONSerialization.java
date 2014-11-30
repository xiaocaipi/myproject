package org.bigfenbushi.http.testserialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.util.Date;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
//json的序列化方式  需要依赖jackson 包
public class TestJSONSerialization {
	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		Person person = new Person();
		person.setAddress("hangzhou");
		person.setAge(30);
		person.setBirth(new Date());
		person.setName("zhangsan");
		
		String personJson = null;
		//使用jackson 的objecmapper  对象，来将对象序列化成 json串，并且输出
		ObjectMapper mapper = new ObjectMapper();
		StringWriter sw = new StringWriter();
		JsonGenerator gen = new JsonFactory().createJsonGenerator(sw);
		mapper.writeValue(gen,person);
		gen.close();
		personJson = sw.toString();
		
		//json对象反序列化  mapper通过readValue 方法 将personjson 串反序列化
		Person zhangsan= (Person)mapper.readValue(personJson, Person.class);
		
		System.out.println(personJson);
		System.out.println("name :"+ person.getName());
		
	}
	
}
