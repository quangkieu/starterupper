package com.joeylawrance.starterupper.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.*;

/**
 * Generate (or use existing) SSH keys, and test if they are configured for a host.
 * 
 * @author Joey Lawrance
 *
 */
public class KeyConfig {
	private final Logger logger = LoggerFactory.getLogger(KeyConfig.class);
	private JSch jsch=new JSch();

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
		// Can't assume the folder already exists
		folder.mkdir();
		File privateKey = new File(folder, "id_rsa");
		File publicKey = new File(folder, "id_rsa.pub");
		File knownHosts = new File(folder, "known_hosts");
		KeyPair kpair;

		// If we don't have both the private and public key in the given folder, just make one.
		if (!(privateKey.exists() && publicKey.exists())) {
			try {
				kpair=KeyPair.genKeyPair(jsch, KeyPair.RSA);
				kpair.writePrivateKey(new FileOutputStream(privateKey));
				kpair.writePublicKey(new FileOutputStream(publicKey), System.getProperty("user.name"));
				kpair.dispose();
			} catch (Exception e) {
				logger.error("Unable to generate a new public/private SSH keypair.", e);
			}
		}
		// Load public key into a string
		byte[] publicKeyByteArray = new byte[(int) publicKey.length()];
		try {
			FileInputStream inputStream = new FileInputStream(publicKey);
			inputStream.read(publicKeyByteArray);
			inputStream.close();
		} catch (Exception e) {
			logger.error("Unable to open public key.", e);
		}
		key = new String(publicKeyByteArray);
		// Configure jsch to use the private key.
		try {
			jsch.addIdentity(privateKey.getAbsolutePath());
		} catch (JSchException e) {
			logger.error("Unable to use private key.", e);
		}
		// Configure jsch to use the known_hosts file.
		try {
			knownHosts.createNewFile();
			jsch.setKnownHosts(knownHosts.getAbsolutePath());
		} catch (JSchException e) {
			logger.error("Unable to use known_hosts file.", e);
		} catch (IOException e1) {
			logger.error("Unable to create new known_hosts file.", e1);
		}
	}
	public String getPublicKey() {
		return key;
	}
	public boolean testLogin(String host) {
		try {
			Session session = jsch.getSession("git", host, 22);
			session.setUserInfo(new UserInfo() {
				@Override
				public String getPassphrase() {
					return null;
				}

				@Override
				public String getPassword() {
					return null;
				}

				@Override
				public boolean promptPassword(String message) {
					return false;
				}

				@Override
				public boolean promptPassphrase(String message) {
					return false;
				}

				@Override
				public boolean promptYesNo(String message) {
					return true;
				}

				@Override
				public void showMessage(String message) {
				}});
			session.connect();
			Channel channel=session.openChannel("shell");
			channel.setInputStream(System.in);
			channel.setOutputStream(System.out);
			channel.connect();
			boolean connected = channel.isConnected();
			channel.disconnect();
			return connected;
		} catch (JSchException e) {
			logger.error("Unable to test login.", e);
		}
		return false;
	}
}