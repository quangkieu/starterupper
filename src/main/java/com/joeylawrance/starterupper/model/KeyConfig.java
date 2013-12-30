package com.joeylawrance.starterupper.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.jcraft.jsch.*;

public class KeyConfig {
	private String key;
	public KeyConfig() throws Exception {
		this(new File(new File(System.getProperty("user.home")), ".ssh"));
	}
	public KeyConfig(File folder) throws Exception {
		File privateKey = new File(folder, "id_rsa");
		File publicKey = new File(folder, "id_rsa.pub");
		JSch jsch=new JSch();
		KeyPair kpair;

		// If we don't have both the private and public key in the given folder, just make one.
		if (!(privateKey.exists() && publicKey.exists())) {
			kpair=KeyPair.genKeyPair(jsch, KeyPair.RSA);
			kpair.writePrivateKey(new FileOutputStream(privateKey));
			kpair.writePublicKey(new FileOutputStream(publicKey), System.getProperty("user.name"));
			kpair.dispose();
		}
		byte[] publicKeyByteArray = new byte[(int) publicKey.length()];
		FileInputStream inputStream = new FileInputStream(publicKey);
		inputStream.read(publicKeyByteArray);
		inputStream.close();
		key = new String(publicKeyByteArray);
	}
	public String getPublicKey() {
		return key;
	}
}