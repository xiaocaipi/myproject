package org.bigfenbushi.http.tesrtcprpc;

public class SayHelloServiceImpl implements SayHelloService {

	@Override
	public String sayHello(String helloArg) {
		if(helloArg.equals("hello")){
			return "hello";
		}else{
			return "bye";
		}
	}

}
