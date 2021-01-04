package com.bancoazteca.bdm.cotizador.BDMCotizacion.dao;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class encryptJavaCode1 {

	protected String plainText = "";
	protected String llave = "";
	protected String outputString = "";
	public String getplainText() {
		return plainText;
	}
	public void setplainText(String val) {
		plainText = val;
	}
	public String getllave() {
		return llave;
	}
	public void setllave(String val) {
		llave = val;
	}
	public String getoutputString() {
		return outputString;
	}
	public void setoutputString(String val) {
		outputString = val;
	}
/****** END SET/GET METHOD, DO NOT MODIFY *****/
	public encryptJavaCode1() {
	}
	public String invoke(String llave, String plainText) throws Exception {
		/* Available Variables: DO NOT MODIFY
			In  : String plainText
			In  : String llave
			Out : String outputString
		* Available Variables: DO NOT MODIFY *****/

		byte[] bytes = Base64.getDecoder().decode(llave);  // Basic Base64 decoding
      
        SecretKeySpec clientKey=new SecretKeySpec(bytes,0,bytes.length,"AES"); 
        Cipher pwdcipher=Cipher.getInstance("AES/CBC/PKCS5Padding"); 
        pwdcipher.init(Cipher.ENCRYPT_MODE,clientKey); 

        byte[] ivBytes=pwdcipher.getIV(); 
        byte[] dataBytes= pwdcipher.doFinal(plainText.getBytes("UTF-8")); 
        byte[] concat=new byte[ivBytes.length + dataBytes.length]; 
        System.arraycopy(ivBytes, 0, concat, 0, ivBytes.length); 
        System.arraycopy( dataBytes, 0, concat, ivBytes.length,  dataBytes.length); 
        outputString=DatatypeConverter.printBase64Binary(concat); 
        return outputString;
	}
}
