package com.zhongyi.lotusprize.auth;

import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.ByteSource;

public class PasswordSaltGenerator {

	private static RandomNumberGenerator generator = new SecureRandomNumberGenerator();
	
	private  static final PasswordSaltGenerator _instance = new PasswordSaltGenerator();
	
	private static final int hashIterations = 10;
	
	private PasswordSaltGenerator(){
		
	}
	
	public PasswordSalt generate(){
		return new PasswordSalt();
	}
	
	public PasswordSalt generate(String salt){
		return new PasswordSalt(salt);
	}
	
	public static PasswordSaltGenerator instance(){
		return _instance;
	}
	
	public static class PasswordSalt {
		private final ByteSource salt;
		
		private PasswordSalt(){
			this.salt = generator.nextBytes();
		}
		
		private PasswordSalt(String salt){
			this.salt = ByteSource.Util.bytes(Hex.decode(salt));
		}
		
		public String saltHex(){
			return salt.toHex();
		}
		
		public String passwordHashed(String passwordPlain){
			Hash hash = new Sha256Hash(passwordPlain, salt, hashIterations);
			return hash.toHex();
		}
	}
	
	
	public static void main(String[]args){
		PasswordSaltGenerator generator = PasswordSaltGenerator.instance();
		PasswordSalt paswordSalt = generator.generate();
		System.out.println(paswordSalt.saltHex());
		System.out.println(paswordSalt.passwordHashed("adminpwd"));
				
	}

}
