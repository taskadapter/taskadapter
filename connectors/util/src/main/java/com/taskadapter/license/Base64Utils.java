package com.taskadapter.license;

import org.apache.commons.codec.binary.Base64;

public class Base64Utils {
	 public static void main(String[] args) {
		    try {
		      String clearText = "Hello world";
		      String encodedText;

		      // Base64
		      encodedText = new String(Base64.encodeBase64(clearText.getBytes()));
		      System.out.println("Encoded: " + encodedText);
		      System.out.println("Decoded:" 
		          + new String(Base64.decodeBase64(encodedText.getBytes())));
		      //    
		      // output :
		      //   Encoded: SGVsbG8gd29ybGQ=
		      //   Decoded:Hello world      
		      //
		    } 
		    catch (Exception e) {
		      e.printStackTrace();
		    }
		  }
}
