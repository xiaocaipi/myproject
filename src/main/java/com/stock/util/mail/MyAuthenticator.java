package com.stock.util.mail;


import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MyAuthenticator extends Authenticator{
	 String userName=null;  
	    String password=null;  
	       
	    public MyAuthenticator(){  
	    }  
	    public MyAuthenticator(String username, String password) {   
	        this.userName = username;   
	        this.password = password;   
	    }   
	    protected PasswordAuthentication getPasswordAuthentication(){  
	        return new PasswordAuthentication(userName, password);  
	    }  
	    
	    
	    public static void main(String[] args){  
	    	MyAuthenticator aa=new MyAuthenticator();
	    	aa.sendmessage("test");
	    }  
	    
	    public void sendmessage(String message){
	    	
	    	MailSenderInfo mailInfo = new MailSenderInfo();   
		      mailInfo.setMailServerHost("smtp.qq.com");   
		      mailInfo.setMailServerPort("25");   
		      mailInfo.setValidate(true);   
		      mailInfo.setUserName("xiaocaipi@qq.com");   
		      mailInfo.setPassword("0410149253cai");//您的邮箱密码   
		      mailInfo.setFromAddress("xiaocaipi@qq.com");   
		      mailInfo.setToAddress("xiaocaipi@qq.com");
		      mailInfo.setSubject("test");   
		      mailInfo.setContent(message);   
		         //这个类主要来发送邮件  
		      SimpleMailSender sms = new SimpleMailSender();  
		          sms.sendTextMail(mailInfo);//发送文体格式   
	    }
}
