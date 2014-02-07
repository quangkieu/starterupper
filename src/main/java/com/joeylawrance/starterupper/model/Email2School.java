package com.joeylawrance.starterupper.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.whois.WhoisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.InternetDomainName;

/**
 * Convert a school-issued email address into a school name.
 * @author Joey Lawrance
 *
 */
public class Email2School {
	private Email2School() {

	}
	private static final Logger logger = LoggerFactory.getLogger(Email2School.class);
	// Drop the user from the email
	private static final Pattern emailPattern = Pattern.compile("^.*[@]");
	private static final WhoisClient whois = new WhoisClient();
	// Who's the whois provider for the relevant domain suffix?
	private static final Map<String, String> whoisProvider = new HashMap<String, String>();
	static {
		whoisProvider.put("edu", "whois.educause.net");
		whoisProvider.put("ac.uk", "whois.ja.net");
	}
	// The regex to extract relevant information for the relevant domain suffix
	private static final Map<String, Pattern> whoisPattern = new HashMap<String, Pattern>();
	static {
		whoisPattern.put("edu", Pattern.compile("(?m)^Registrant:$\\s+(.*)$"));
		whoisPattern.put("ac.uk", Pattern.compile("(?m)^Registered For:$\\s+(.*)$"));
	}

	/**
	 * Scrape the WHOIS record from the email address and return the registrant (the school name).
	 * @param email the email address
	 * @return the school name
	 */
	public static String schoolFromEmail(String email) {
		// Keep only the domain and nothing else.
		String domain = InternetDomainName.from(emailPattern.matcher(email).replaceAll("")).topPrivateDomain().toString();
		String tld = InternetDomainName.from(domain).publicSuffix().toString();
		try {
			if (!whoisProvider.containsKey(tld)) return null;
			whois.connect(whoisProvider.get(tld));
			String result = whois.query(domain);

			// Extract just the registrant
			Matcher matcher = whoisPattern.get(tld).matcher(result);
			if (matcher.find()) {
				result = matcher.group(1);
			} else {
				result = "";
			}
			whois.disconnect();
			return result;
		} catch(IOException e) {
			logger.error("Unable to get whois data for {}.", domain, e);
		}
		return null;
	}
}
