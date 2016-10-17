package com.zhongyi.lotusprize.service;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.shiro.codec.Base64;


public class Ciphers {

    private static final int iterationCount;

    private static final byte[] salt;

    public static final CipherObject confirmEmailCipher;

    public static final CipherObject resetPasswordCipher;
    

    static {
    	ApplicationProperty applicationProperty = ApplicationProperty.instance();
        iterationCount = applicationProperty.getAsInt("cipher.iteration");
        salt = applicationProperty.getAsString("cipher.salt").getBytes(applicationProperty.charset());
        confirmEmailCipher = new CipherObject(applicationProperty.getAsString("cipher.key.confirm_email"));
        resetPasswordCipher = new CipherObject(applicationProperty.getAsString("cipher.key.reset_password"));
    }


    public static class CipherObject {

        private final Cipher ecipher;

        private final Cipher dcipher;

        private CipherObject(String passPhrase) {
            try {
                KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
                SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES")
                        .generateSecret(keySpec);
                ecipher = Cipher.getInstance(key.getAlgorithm());
                dcipher = Cipher.getInstance(key.getAlgorithm());

                // Prepare the parameter to the ciphers
                AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt,iterationCount);

                // Create the ciphers
                ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
                dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String doEncrypt(String plainText) {
            byte[] plainBytes = plainText.getBytes(ApplicationProperty.instance().charset());
            try {
                byte[] encBytes = ecipher.doFinal(plainBytes);
                return Base64.encodeToString(encBytes);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

        public String doDecrypt(String base64EncryptedText) {
            byte[] encBytes = Base64.decode(base64EncryptedText);
            try {
                byte[] plainBytes = dcipher.doFinal(encBytes);
                return new String(plainBytes, ApplicationProperty.instance().charset());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String doEncryptUri(String uri) {
            String base64EncryptedUrl = doEncrypt(uri);
            return base64EncryptedUrl.replace("+", "*").replace("/", "-").replace("=", ".");
        }

        public String doDecryptUri(String uri) {
            return doDecrypt(uri.replace("*", "+").replace("-", "/").replace(".", "="));
        }

    }

}

