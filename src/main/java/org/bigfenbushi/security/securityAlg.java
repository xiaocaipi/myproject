package org.bigfenbushi.security;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.thoughtworks.xstream.core.util.Base64Encoder;

public class securityAlg {

	
	public static void main(String[] args) throws Exception  {
		String testString ="i m menu,hi";
		//md5 base64算法编码后
		System.out.println("md5 base64算法编码后  "+byte2base64(testMd5(testString)));
		//AES 加密 base64算法编码后
		String base64AesKey = genkeyAES();
		String encoAes= byte2base64(encryAes(testString.getBytes("utf-8"),loadKeyAes(base64AesKey)));
		System.out.println("AES 加密 base64算法编码后  =  "+encoAes);
		//AES 解密
		byte[] decoAesByte = decryptAES(base642byte(encoAes), loadKeyAes(base64AesKey));
		System.out.println("AES 解密后  =  "+new String(decoAesByte));
		//rsa 生成公钥和私钥
		KeyPair keypair = getKeyPair();
		String publicKey = getPublicKey(keypair);
		String privateKey = getPrivate(keypair);
		System.out.println("publicKey = "+publicKey+"   privateKey ="+privateKey);
		//用私钥 加密数据
		byte [] privateKeyEncryptByte = privateEncrypt(testString.getBytes("utf-8"), string2Privatekey(privateKey));
		//用公钥 解密 并打印
		byte [] decryptStringByte = publicDecrypt(privateKeyEncryptByte, string2Publickey(publicKey));
		System.out.println("用公钥 解密   = "+new String(decryptStringByte));
		
	}
	
	public static byte[] testMd5(String content) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] bytes=md.digest(content.getBytes("utf-8"));
		return bytes;
	}
	
	private static String byte2base64(byte [] bytes){
		Base64Encoder base64Encoder = new Base64Encoder();
		return base64Encoder.encode(bytes);
	}
	
	private static byte [] base642byte(String base64){
		Base64Encoder base64Encoder = new Base64Encoder();
		return base64Encoder.decode(base64);
	}
	
	private static String genkeyAES() throws Exception{
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		keygen.init(128);
		SecretKey key = keygen.generateKey();
		String aa = byte2base64(key.getEncoded());
		return aa;
	}
	
	private static SecretKey loadKeyAes(String base64key){
		byte [] bytes= base642byte(base64key);
		SecretKey key = new SecretKeySpec(bytes, "AES");
		return key;
	}
	
	private static byte[] encryAes(byte [] source,SecretKey key) throws Exception{
		Cipher cipher =Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte [] bytes = cipher.doFinal(source);
		return bytes;
		
	}
	
	private static byte[] decryptAES(byte [] source,SecretKey key) throws Exception{
		Cipher cipher =Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte [] bytes = cipher.doFinal(source);
		return bytes;
	}
	
	public static KeyPair getKeyPair() throws NoSuchAlgorithmException{
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(512);
		KeyPair keyPair= keyPairGenerator.generateKeyPair();
		return keyPair;
	}
	
	public static String getPublicKey(KeyPair keypair){
		PublicKey publickey = keypair.getPublic();
		byte [] bytes = publickey.getEncoded();
		return byte2base64(bytes);
	}
	
	public static String getPrivate(KeyPair keypair){
		PrivateKey privateKey = keypair.getPrivate();
		byte [] bytes = privateKey.getEncoded();
		return byte2base64(bytes);
	}
	
	public static PrivateKey string2Privatekey(String privateStr) throws Exception{
		byte [] keypairByte  = base642byte(privateStr);
		PKCS8EncodedKeySpec keyspec = new PKCS8EncodedKeySpec(keypairByte);
		KeyFactory factory= KeyFactory.getInstance("RSA");
		return factory.generatePrivate(keyspec);
	}
	
	public static PublicKey string2Publickey(String pubstr) throws Exception{
		byte [] keypairByte  = base642byte(pubstr);
		X509EncodedKeySpec keyspec = new X509EncodedKeySpec(keypairByte);
		KeyFactory factory= KeyFactory.getInstance("RSA");
		return factory.generatePublic(keyspec);
	}
	
	public static byte[] publicDecrypt(byte [] content,PublicKey publicKey) throws Exception{
		Cipher cipher =Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, publicKey);
		byte [] bytes = cipher.doFinal(content);
		return bytes;
	}
	
	public static byte[] privateEncrypt(byte [] content,PrivateKey privateKey) throws Exception{
		Cipher cipher =Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		byte [] bytes = cipher.doFinal(content);
		return bytes;
	}
	
}
