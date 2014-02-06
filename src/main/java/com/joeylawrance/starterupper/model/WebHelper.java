package com.joeylawrance.starterupper.model;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebWindowNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * A wrapper around HTMLUnit to fill and submit forms without needing to know the silly details.
 * 
 * @author Joey Lawrance
 *
 */
public class WebHelper {
	private final Logger logger = LoggerFactory.getLogger(WebHelper.class);
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
		getWebClient().getOptions().setJavaScriptEnabled(false);
	}
	
	/**
	 * Get the web page in the given browser window
	 * @param window The browser window
	 * @return an HtmlPage
	 */
	public HtmlPage getPageInWindow(String window) {
		return (HtmlPage) getWebClient().getWebWindowByName(window).getEnclosedPage();
	}

	/**
	 * Fill in a form sloppily in the given browser window with the supplied map.
	 * @param window The browser window
	 * @param map the form data to supply to the page.
	 */
	public void fillForm(String window, Map<String, String> map) {
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
					HtmlElement element = htmlLabel.getReferencedElement();
					if ("textarea".equalsIgnoreCase(element.getNodeName())) {
						element.setTextContent(map.get(key));
					} else if ("input".equalsIgnoreCase(element.getNodeName())) {
						HtmlInput htmlInput = (HtmlInput) element;
						htmlInput.setValueAttribute(map.get(key));
					}
				}
			}
		}
	}
	
	/**
	 * Submit the form in the given window by clicking on a button with the given label.
	 * @param window The browser window
	 * @param submitLabel The button label to click
	 */
	public void submitForm(String window, String submitLabel) {
		final HtmlPage page = getPageInWindow(window);
		List<?> inputs = page.getByXPath("//form//input[@type='submit']|//form//button[@type='submit']");
		
		Pattern pattern = Pattern.compile(submitLabel, Pattern.CASE_INSENSITIVE);
		for (Object input : inputs) {
			HtmlElement htmlElement = (HtmlElement) input;
			Matcher matcher = pattern.matcher(htmlElement.asText());
			if (matcher.find()) {
				try {
					htmlElement.click();
				} catch (IOException e) {
					logger.error("Unable to click on {} submit button for {}.", submitLabel, window, e);
				}
				break;
			}
		}
	}

	/**
	 * Open a new browser window with the given name
	 * @param name the name of the new browser window
	 */
	public void newWindow(String name) {
		try {
			getWebClient().getWebWindowByName(name);
		} catch (WebWindowNotFoundException e) {
			getWebClient().openWindow(null, name);
		}
	}
	
	/**
	 * Load a page in the given window.
	 * @param window The window to load a page in
	 * @param url The url of the page
	 * @return The page
	 * @throws FailingHttpStatusCodeException
	 * @throws IOException
	 */
	public Page load(String window, String url) throws FailingHttpStatusCodeException, IOException {
		WebRequest request;
		try {
			request = new WebRequest(new URL(url));
			return getWebClient().getPage(getWebClient().getWebWindowByName(window), request);
		} catch (MalformedURLException e) {
			logger.error("Malformed URL: {}", url, e);
		} catch (WebWindowNotFoundException e) {
			logger.error("Unable to find window for {}.", window, e);
		}
		return null;
	}
	
	/**
	 * Get the current URL of the given browser window.
	 * @param window the browser window
	 * @return the URL as a String
	 */
	public String getPageUrl(String window) {
		return getPageInWindow(window).getUrl().toString();
	}
}