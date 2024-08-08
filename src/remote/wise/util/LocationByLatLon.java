package remote.wise.util;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import remote.wise.service.datacontract.LocationDetails;





public class LocationByLatLon {
	
	public static void registerProxyAuth() {
		
		final String authUser = "wipro/";
		final String authPassword = "";
		
		Authenticator.setDefault(new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(authUser, authPassword
						.toCharArray());
			}
		});

		System.setProperty("http.proxyUser", authUser);
		System.setProperty("http.proxyPassword", authPassword);
		System.setProperty("proxyHost", "proxy4.wipro.com");
		System.setProperty("proxyPort", "8080");
	}
	
	public static String getLocation(String longitude, String lattitude)
			throws ParserConfigurationException, SAXException, IOException,
			XPathExpressionException {
		try {
		//registerProxyAuth();
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		
		//Rajani Nagaraju - 20130930 - URL was wrong and hence location was not retrieved from Google Maps
		Document doc = builder
				.parse(new URL(UrlSigner.getSignedURL("http://maps.googleapis.com/maps/api/geocode/xml?latlng="
						+ (lattitude.length() > 9 ? lattitude.substring(0,
								9) : lattitude)
						+ ","
						+ (longitude.length() > 9 ? longitude.substring(0,
								9) : longitude)
						+ "&sensor=false&client=gme-jcb"))
						.openStream());
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xpath.compile("//formatted_address/text()");

		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		String temp = nodes.item(0).getNodeValue().substring(0, nodes.item(0).getNodeValue().lastIndexOf(","));
		//System.out.println(temp);
		//return temp.substring(0, temp.lastIndexOf(","));
		return temp;
		
		}catch (Exception e) {
			//e.printStackTrace();
			return "undefined";
		}
		
	}
	
	
	
	
	public LocationDetails getLocationDetails(String longitude, String lattitude)
	{
		LocationDetails locationObj = new LocationDetails();
		
		try 
		{
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(true);
			DocumentBuilder builder = domFactory.newDocumentBuilder();

			Document doc = builder.parse(new URL(UrlSigner.getSignedURL("http://maps.googleapis.com/maps/api/geocode/xml?latlng="
					+ (lattitude.length() > 9 ? lattitude.substring(0,9) : lattitude)
					+ ","
					+ (longitude.length() > 9 ? longitude.substring(0,9) : longitude)
					+ "&sensor=false&client=gme-jcb")).openStream());
			
			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xpath.compile("//address_component/long_name/text()");
			XPathExpression expr1 = xpath.compile("//address_component/type[1]/text()");
			XPathExpression expr2 = xpath.compile("//formatted_address/text()");

			Object result2 = expr2.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes2 = (NodeList) result2;
			String temp = nodes2.item(0).getNodeValue().substring(0, nodes2.item(0).getNodeValue().lastIndexOf(","));
			locationObj.setAddress(temp);
			
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result; // treated as value

			Object result1 = expr1.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes1 = (NodeList) result1; // treated as key
			boolean city = false, state = false;

			for (int i = 0; i < nodes1.getLength(); i++) 
			{
				if (!state && nodes1.item(i).getNodeValue().equalsIgnoreCase("administrative_area_level_1")) 
				{
					locationObj.setState(nodes.item(i).getNodeValue());
					state = true;
				}
				/*if (!country && nodes1.item(i).getNodeValue().equalsIgnoreCase("country")) {
					locationObj.setCountry(nodes.item(i).getNodeValue());
					country = true;
				}*/
				if (!city && nodes1.item(i).getNodeValue().equalsIgnoreCase("locality")) 
				{
					locationObj.setCity(nodes.item(i).getNodeValue());
					city = true;
				}
				/*if (!postalcode && nodes1.item(i).getNodeValue().equalsIgnoreCase("postal_code")) {
					locationDetails.setPin(nodes.item(i).getNodeValue());
					System.out.println("postal_code :"+ nodes.item(i).getNodeValue());
					postalcode = true;
				}*/
				
				if (city && state) 
				{
					break;
				}
			}

		} 
		
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return locationObj;
	}

}
