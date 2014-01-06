package com.joeylawrance.starterupper.model;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.LogFactory;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebWindowNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class WebHelper {
	static {
	    LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

	    java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
	    java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
	}
	
	private static class SingletonHolder { 
		public static final WebClient INSTANCE = new WebClient();
	}
	
	private static WebClient getWebClient() {
		return SingletonHolder.INSTANCE;
	}
	
	public WebHelper() {
		getWebClient().getOptions().setThrowExceptionOnScriptError(false);
	}
	
	public HtmlPage getPageInWindow(String window) {
		return (HtmlPage) getWebClient().getWebWindowByName(window).getEnclosedPage();
	}

	public void fillForm(String window, Map<String, String> map) throws Exception {
		final HtmlPage page = getPageInWindow(window);
		List<?> inputs = page.getByXPath("//form//input[@placeholder]");
		List<?> labels = page.getByXPath("//form//label[@for]");
		for (String key: map.keySet()) {
			Pattern pattern = Pattern.compile(key, Pattern.CASE_INSENSITIVE);
			for (Object input : inputs) {
				HtmlInput htmlInput = (HtmlInput) input;
				Matcher matcher = pattern.matcher(htmlInput.getAttribute("placeholder"));
				if (matcher.find()) {
					htmlInput.setValueAttribute(map.get(key));
				}
			}
			for (Object label : labels) {
				HtmlLabel htmlLabel = (HtmlLabel) label;
				Matcher matcher = pattern.matcher(htmlLabel.asText());
				if (matcher.find()) {
					HtmlInput htmlInput = (HtmlInput) htmlLabel.getReferencedElement();
					htmlInput.setValueAttribute(map.get(key));
				}
			}
		}
	}
	
	public void submitForm(String window, String submitLabel) throws IOException {
		final HtmlPage page = getPageInWindow(window);
		List<?> inputs = page.getByXPath("//form//input[@type='submit']|//form//button[@type='submit']");
		
		Pattern pattern = Pattern.compile(submitLabel, Pattern.CASE_INSENSITIVE);
		for (Object input : inputs) {
			HtmlElement htmlElement = (HtmlElement) input;
			Matcher matcher = pattern.matcher(htmlElement.asText());
			if (matcher.find()) {
				htmlElement.click();
				break;
			}
		}
	}

	public void newWindow(String name) {
		getWebClient().openWindow(null, name);
	}
	
	public Page load(String window, String url) throws FailingHttpStatusCodeException, WebWindowNotFoundException, IOException {
		WebRequest request = new WebRequest(new URL(url));
		return getWebClient().getPage(getWebClient().getWebWindowByName(window), request);
	}
	
	public String getPageUrl(String window) {
		return getPageInWindow(window).getUrl().toString();
	}
}