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
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
//xml的序列化
public class TestXMLSerialization {
	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		Person person = new Person();
		person.setAddress("hangzhou");
		person.setAge(30);
		person.setBirth(new Date());
		person.setName("zhangsan");
		
		//将person对象序列化成xml
		XStream xStream = new XStream( new DomDriver());
		//设置person类的别名
		xStream.alias("person", Person.class);
		String personXml = xStream.toXML(person);
		
		//将xml反序列化还原为person对象
		Person zhangsan = (Person)xStream.fromXML(personXml);
		
		System.out.println(personXml);
		System.out.println("name :"+ person.getName());
		
	}
	
}
