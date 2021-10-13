package com.bsms.util;

import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;

public class MbLibRSA {

	KeyPair key_pair;
    KeyFactory key_fact;
    String instance = "";
    
    public MbLibRSA(String instance) {
        try {
            Security.addProvider(new BouncyCastleProvider());
            key_fact = KeyFactory.getInstance("RSA");
            this.instance = instance;
        } catch (NoSuchAlgorithmException Ex) {
            Ex.printStackTrace();
//            LibFunction.setLogMessage(Ex.getMessage());
        }
    }
    
    public void GenerateKeypair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            this.key_pair = keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException Ex) {
            Ex.printStackTrace();
//            LibFunction.setLogMessage(Ex.getMessage());
        }
    }
    
    public String GetPublicKeyPem() {
        Key pubKey = key_pair.getPublic();
        String result = Base64.encodeBase64String(pubKey.getEncoded());
        return result;
    }
    
    public String GetPrivateKeyPem() {
        Key privKey = key_pair.getPrivate();
        String result = Base64.encodeBase64String(privKey.getEncoded());
        return result;
    }
	
}
