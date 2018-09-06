package com.truckapp.util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SHA_Hash {
	
    public static String getHashedPassword(String passwordToHash, String salt)
    {
        String generatedPassword = null;
        try {
        	
            MessageDigest md = MessageDigest.getInstance("SHA-1");
//          MessageDigest md = MessageDigest.getInstance("SHA-256");
//          MessageDigest md = MessageDigest.getInstance("SHA-384");
//          MessageDigest md = MessageDigest.getInstance("SHA-512");
          
            md.update(salt.getBytes());
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return generatedPassword;
    }
         
    public static String getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt.toString();
    }
}

