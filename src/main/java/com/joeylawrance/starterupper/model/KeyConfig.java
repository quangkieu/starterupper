package com.joeylawrance.starterupper.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.*;

/*
 * Generate SSH keys.
 */
public class KeyConfig {
	private final Logger logger = LoggerFactory.getLogger(KeyConfig.class);

	private String key;
	private KeyConfig() {
		this(new File(new File(System.getProperty("user.home")), ".ssh"));
	}
	private static class SingletonHolder {
		public static final KeyConfig INSTANCE = new KeyConfig();
	}
	public static KeyConfig getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public KeyConfig(File folder) {
		File privateKey = new File(folder, "id_rsa");
		File publicKey = new File(folder, "id_rsa.pub");
		JSch jsch=new JSch();
		KeyPair kpair;

		// If we don't have both the private and public key in the given folder, just make one.
		if (!(privateKey.exists() && publicKey.exists())) {
			try {
				kpair=KeyPair.genKeyPair(jsch, KeyPair.RSA);
				kpair.writePrivateKey(new FileOutputStream(privateKey));
				kpair.writePublicKey(new FileOutputStream(publicKey), System.getProperty("user.name"));
				kpair.dispose();
			} catch (Exception e) {
				logger.error("Unable to generate a new public/private SSH keypair.");
			}
		}
		byte[] publicKeyByteArray = new byte[(int) publicKey.length()];
		try {
			FileInputStream inputStream = new FileInputStream(publicKey);
			inputStream.read(publicKeyByteArray);
			inputStream.close();
		} catch (Exception e) {
			logger.error("Unable to open public key.");
		}
		key = new String(publicKeyByteArray);
	}
	public String getPublicKey() {
		return key;
	}
}