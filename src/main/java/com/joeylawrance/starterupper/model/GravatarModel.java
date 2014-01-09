package com.joeylawrance.starterupper.model;

import java.security.MessageDigest;
import java.util.Vector;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import com.joeylawrance.starterupper.Constants;

import helma.xmlrpc.secure.SecureXmlRpcClient;

public class GravatarModel extends GenericHostModel {
	public GravatarModel() {
		super("Gravatar", GravatarModel.class.getResource("/Gravatar.png"));
		setLoginURL("https://wordpress.com/wp-login.php");
		setSignupURL("https://signup.wordpress.com/signup/?user=1");
		setResetURL("http://wordpress.com/wp-login.php?action=lostpassword");
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void uploadPicture() throws Exception {
		// XML-RPC sucks
		// https://en.gravatar.com/site/implement/xmlrpc/
		MessageDigest md = MessageDigest.getInstance("MD5");
		String thedigest = new String(md.digest(this.getEmail().toLowerCase().getBytes()));
		SecureXmlRpcClient rpc = new SecureXmlRpcClient(String.format("https://secure.gravatar.com/xmlrpc?user=%s",thedigest));
		byte[] imageData = FileUtils.readFileToByteArray(Constants.PROFILE_PICTURE);
		Vector parameters = new Vector();
		parameters.add(new String(Base64.encodeBase64(imageData)));
		parameters.add(0);
		parameters.add(getPassword());
		Object result = rpc.execute("grav.saveData", parameters);
	}
}
