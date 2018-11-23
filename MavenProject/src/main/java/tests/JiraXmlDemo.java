package tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import com.gargoylesoftware.htmlunit.Page;
import com.sun.tools.javac.util.List;
import org.testng.AssertJUnit;
import java.util.Base64;
import java.util.UUID;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class JiraXmlDemo{
	
	public void runDemo(RemoteWebDriver driver){
		String xmlReport = null;
		try {
			xmlReport = getXmlReport(driver);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String scriptStartTime = null;
		try {
			scriptStartTime = parseXmlReport(xmlReport);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String jiraResult = null;
		
		jiraResult = createJiraIssue(scriptStartTime);
		
	}
	
	
	public String getXmlReport(RemoteWebDriver driver) throws IOException {
		// this function will retrieve and return the xml script execution report from the webdriver
		
		String report = null;
		try { 	
			String command = "mobile:report:download"; 
			Map<String, Object> params = new HashMap<>(); 
			params.put("type", "xml"); 
			report = (String)driver.executeScript(command, params);
		} 
		catch (Exception ex) { 
			System.out.println("Got exception " + ex); 
		}
		String result = new String(OutputType.BYTES.convertFromBase64Png(report), StandardCharsets.UTF_8);
		return result;
	}
	
	public String parseXmlReport(String xmlReport) throws IOException{
		// this function will demo a simple XML parsing by looking up the value for the 
		// first node called "server" which holds the script execution start time
		
	    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder = null;
		
	    try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
	    InputSource is = new InputSource(new StringReader(xmlReport));
	    Document doc = null;
		try {
			doc = dBuilder.parse(is);
		} catch (SAXException e) {
			e.printStackTrace();
		}
	    doc.getDocumentElement().normalize();
	    
	    // point to the root element of the XML structure
	    Element rootElement = doc.getDocumentElement();
	    
	    // search all nodes with the name "server"
	    NodeList list = rootElement.getElementsByTagName("server");
	    
	    //did we find any?
	    if (list != null && list.getLength() > 0) {
	        NodeList subList = list.item(0).getChildNodes();
	
	        if (subList != null && subList.getLength() > 0) {
	        	System.out.println(subList.item(0).getNodeValue());
	        	// return the value of the first occurrence
	        	return subList.item(0).getNodeValue();
	        }
	    }
	    return "Item not found";
	}
    
	public String createJiraIssue(String scriptStartTime){
		
		// Add you JIRA Project Name:
		String jiraProjectName = "MyBrokerBee";
		
		// Add your JIRA HOST here
		String host = new String("https://teneda.atlassian.net"); 
		
		// the URL for creating issues:
		String url = host + "/rest/api/2/issue";
		
		//Add your credentials here:
		String Username = "rutam.desai@mybrokerbee.com";
		String Password = "rutammybrokerbee";		
		
		Client client = Client.create();
		
		// Provide the credentials
		client.addFilter(new HTTPBasicAuthFilter(Username, Password));
		WebResource webResource = client.resource(url);			
		
		// this JSON string contains all values for the ticket
		String input="{\"fields\":{\"project\":{\"key\":\"" + jiraProjectName + "\"},\"summary\":\"Demo Ticket\",\"description\":\"Script was executed at " + scriptStartTime + "\", \"reporter\": {\"name\": \"" + Username + "\"},\"issuetype\":{\"name\":\"Bug\"}}}";
		ClientResponse response = webResource.type("application/json").post(ClientResponse.class, input);
		String output = response.getEntity(String.class);
		System.out.println("JIRA Server returns:\n" + output);
		return output;
	}
}