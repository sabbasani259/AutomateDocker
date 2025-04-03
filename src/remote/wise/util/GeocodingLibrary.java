//CR270-20220322-Balaji-Google Map API changes for  Address display
package remote.wise.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.logging.log4j.Logger;
import org.json.simple.JSONValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import eu.bitm.NominatimReverseGeocoding.Address;
import eu.bitm.NominatimReverseGeocoding.NominatimReverseGeocodingJAPI;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.LocationDetails;
import remote.wise.service.datacontract.LocationDetailsMMI;

public class GeocodingLibrary {
	
	private static final Logger fLogger = null;   

	/*public static void main(String[] args) {
		registerProxyAuth();
	}*/
	
	public static void registerProxyAuth() {
		final String authUser = "wipro/sh828875";
		final String authPassword = "Sharemarket@786";
		
		Authenticator.setDefault(new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(authUser, authPassword
						.toCharArray());
			}
		});

		System.setProperty("http.proxyUser", authUser);
		System.setProperty("http.proxyPassword", authPassword);
		System.setProperty("proxyHost", "proxy1.wipro.com");
		System.setProperty("proxyPort", "8080");
	}

	/**
	 * 
	 * @param lattitude
	 * @param longitude
	 * @return generic method to return location from lat/log
	 */
	
	public static LocationDetails getLocationDetails(String lattitude, String  longitude){
		return getLocationDetailsOSM(lattitude,longitude);
	}
	
	
	
	public static LocationDetailsMMI getLocationDetailsMMI(String lattitude, String  longitude){
		return getLocationDetailsFromOSMMMI(lattitude,longitude);
	}
	
	/**
	 * 
	 * @param lattitude
	 * @param longitude
	 * @return location from open street map API with fall back calling Google API to get the location
	 */	

	/**
	 * New Logic for retrieving location from OSM using built in API
	*/
	public static LocationDetails getLocationDetailsOSM1(String lattitude,String longitude) {
		Logger iLogger = InfoLoggerClass.logger;
		NominatimReverseGeocodingJAPI geocodingJAPI = new NominatimReverseGeocodingJAPI(); 
		LocationDetails locationObj = new LocationDetails();
		Double lat = Double.valueOf(lattitude);
		Double lon = Double.valueOf(longitude);
		Address address = null;

		try{
			address = geocodingJAPI.getAdress(lat, lon);
			if(address.getCountryCode().equalsIgnoreCase("in")){
				locationObj.setCity(address.getCity());
				locationObj.setState(address.getState());
				locationObj.setCountry(address.getCountry());
				locationObj.setAddress(address.toString());
			}else{
				locationObj.setCity("Outside India");
				locationObj.setState("Outside India");
				locationObj.setCountry("Outside India");
				locationObj.setAddress("Outside India");
			}
		}catch (Exception e) {
			locationObj = getAddressGoogleJSON(lattitude+","+longitude);
		}
		
		iLogger.info("Final Address"+locationObj);
		
		return locationObj;
	}
	
	// commented manual parsing of OSM json by Shrini - 02082016
	
	public static LocationDetails getLocationDetailsOSM(String lattitude,String longitude) {
		Logger iLogger = InfoLoggerClass.logger;
		
		long startTime = System.currentTimeMillis();
		// changed sleep time from 1000 to 750 to avoid the delay by Shrini 140720160520
		try{
			Thread.sleep(750);
		}catch (Exception e) {
			iLogger.info("Error pausing for second for OSM "+e.getMessage());
		}
		
		LocationDetails locationObj = new LocationDetails();
		try{
			//URL url = new URL("http://nominatim.openstreetmap.org/reverse?format=json&lat="+lattitude+"&lon="+longitude+"&zoom=7&addressdetails=1&email=shrinivas.kattimani@wipro.com");
			
			//2016-08-08 : Suresh : Enforcing the Lang settings to English for OSM
			URL url = new URL("http://nominatim.openstreetmap.org/reverse?accept-language=en&format=json&lat="+lattitude+"&lon="+longitude+"&zoom=18&addressdetails=1&email=shrinivas.kattimani@wipro.com");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "+ conn.getResponseCode());
			}
			// Reading data's from url
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			String out = "";
			while ((output = br.readLine()) != null) {
				out += output;
			}
			long endTime = System.currentTimeMillis();
			iLogger.info("Time taken Just to fetch location from OSM "+(endTime-startTime));
			try {
				org.json.simple.JSONObject obj=(org.json.simple.JSONObject)org.json.simple.JSONValue.parse(out); 
				try{
					locationObj.setAddress(obj.get("display_name").toString());
				}catch (Exception e) {
				}
				
				org.json.simple.JSONObject arr = (org.json.simple.JSONObject)obj.get("address"); 
				if(arr.get("city")!=null){
					locationObj.setCity(StringSimplifier.simplifiedString(arr.get("city").toString()));
				}else if(arr.get("state_district")!=null){
					locationObj.setCity(StringSimplifier.simplifiedString(arr.get("state_district").toString()));
				}else if(arr.get("village")!=null){
					locationObj.setCity(StringSimplifier.simplifiedString(arr.get("village").toString()));
				}else{
					locationObj.setCity(StringSimplifier.simplifiedString(arr.get("county").toString()));
				}
				
				locationObj.setState(StringSimplifier.simplifiedString(arr.get("state").toString()));
				
				locationObj.setCountry(arr.get("country").toString());
				if(locationObj.getCountry()==null || locationObj.getCountry().equalsIgnoreCase("undefined") ){
					locationObj = getAddressGoogleJSON(lattitude+","+longitude);
				}
				
				iLogger.info("Successfully fetched location from OSM for "+(lattitude+":"+longitude));
			} catch (Exception e) {
				try{
					locationObj = getAddressGoogleJSON(lattitude+","+longitude);
				}catch (Exception googleExc) {
					iLogger.fatal("Exception while fetching address from google API "+googleExc.getMessage());
				}
			}
			conn.disconnect();
		}catch (Exception e) {
			locationObj = getAddressGoogleJSON(lattitude+","+longitude);
			//locationObj=GoogleForIndia(lattitude,longitude);
		}
		iLogger.info("Time taken Just to fetch location from OSM and set in Location Object "+(System.currentTimeMillis()-startTime));
		return locationObj;
	}
	
	public static LocationDetails getAddressGoogleXML(String latlong) {
		//registerProxyAuth();
		LocationDetails locationDetails = new LocationDetails();
		String address = null;
		String gURL = "https://maps.google.com/maps/api/geocode/xml?latlng="+ latlong + "&sensor=false";
		try {
			DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = df.newDocumentBuilder();
			Document dom = db.parse(gURL);
			Element docEl = dom.getDocumentElement();
			NodeList nl = docEl.getElementsByTagName("result");
			if (nl != null && nl.getLength() > 0) {
				address = ((Element) nl.item(0)).getElementsByTagName("formatted_address").item(0).getTextContent();
				String addressSplit[] = address.split(",");
				locationDetails.setAddress(address);
				locationDetails.setCountry(addressSplit[addressSplit.length-1]);
				String state[] = addressSplit[addressSplit.length-2].split(" ");
				locationDetails.setState(state[0]);
				locationDetails.setCity(addressSplit[addressSplit.length-3]);
			}
		} catch (Exception ex) {
			locationDetails.setAddress("undefined");
			locationDetails.setCountry("undefined");
			locationDetails.setState("undefined");
			locationDetails.setCity("undefined");
		}
		return locationDetails;
	}
	
	//DF20180912 MANI ::commenting out because introducing Google Maps for India. MapmyIndia is not being used
	//With MapMyIndia implementation
/*		public static LocationDetails getAddressGoogleJSON(String latlong){
			Logger iLogger = InfoLoggerClass.logger;
			long startTime = System.currentTimeMillis();
			LocationDetails locationDetails = new LocationDetails();
			try {
				URL url = null;
				try {
					url = new URL("http://apis.mapmyindia.com/advancedmaps/v1/jz16gmz74egysaeyw1mi2g7gvixycaz7/rev_geocode?lng="+latlong.split(",")[1]+"&lat="+latlong.split(",")[0]);
				} catch (Exception e1) {
					e1.printStackTrace();
					locationDetails.setAddress("undefined");
					locationDetails.setCountry("undefined");
					locationDetails.setState("undefined");
					locationDetails.setCity("undefined");
					iLogger.fatal(" InvalidKeyException for location "+latlong+e1.getMessage());
				} 
				System.out.println("MAPMYINDIA :: " + url);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.connect();
				//conn.setRequestProperty("Accept", "application/json");
				try{
					if (conn.getResponseCode() != 200) {
						throw new RuntimeException("Failed : HTTP error code : "+ conn.getResponseCode());
					}
				}catch (Exception e) {
					locationDetails.setAddress("undefined");
					locationDetails.setCountry("undefined");
					locationDetails.setState("undefined");
					locationDetails.setCity("undefined");
					iLogger.fatal(" Failed : HTTP error code :  "+latlong+e.getMessage());
				}
				

				// Reading data's from url
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String output;
				String out = "";
				while ((output = br.readLine()) != null) {
					out += output;
				}

				iLogger.info("Time taken Just to fetch location from Google  "+ (System.currentTimeMillis() - startTime));
				// Converting Json formatted string into JSON object
				
				try {
					JSONObject json = (JSONObject) JSONSerializer.toJSON(out);
					JSONArray results = json.getJSONArray("results");
					JSONObject rec = results.getJSONObject(0);
						
						System.out.println(rec);
						
						locationDetails.setAddress(rec.getString("formatted_address"));
						locationDetails.setCountry("India");
						locationDetails.setState(rec.getString("state"));
						locationDetails.setCity(rec.getString("city"));
						
						if(!(rec.getString("city").equalsIgnoreCase("")))
							locationDetails.setCity(rec.getString("city"));
						else
							locationDetails.setCity(rec.getString("district"));
							
						System.out.println("Address:"+locationDetails.getAddress()+"State:"+locationDetails.getState()+"City:"+locationDetails.getCity()+"Country:"+locationDetails.getCountry());
							
				}catch (Exception e) {
					e.printStackTrace();
					locationDetails.setAddress("undefined");
					locationDetails.setCountry("undefined");
					locationDetails.setState("undefined");
					locationDetails.setCity("undefined");
					iLogger.fatal(" Exception while reading JSON "+latlong+e.getMessage());
				}
				iLogger.info("Successfully fetched location from google for "+(latlong));
				conn.disconnect();
			} catch (MalformedURLException e) {
				locationDetails.setAddress("undefined");
				locationDetails.setCountry("undefined");
				locationDetails.setState("undefined");
				locationDetails.setCity("undefined");
				iLogger.fatal(" MalformedURLException for location "+latlong+e.getMessage());
			} catch (IOException e) {
				locationDetails.setAddress("undefined");
				locationDetails.setCountry("undefined");
				locationDetails.setState("undefined");
				locationDetails.setCity("undefined");
				iLogger.fatal(" IOException location "+latlong+e.getMessage());
			}
			iLogger.info("Time taken Just to fetch location from Google and set in Location Object "+(System.currentTimeMillis()-startTime));
			return locationDetails;
		}*/

	public LocationDetails getAddressGoogleXML(String lat, String lon) {
		return getAddressGoogleXML(lat + "," + lon);
	}

	public LocationDetails getAddressGoogleXML(double lat, double lon) {
		return getAddressGoogleXML("" + lat, "" + lon);
	}
	
	public LocationDetails getAddressGoogleJSON(String lat, String lon) {
		return getAddressGoogleJSON(lat + "," + lon);
	}
	
	public LocationDetails getAddressGoogleJSON(double lat, double lon) {
		return getAddressGoogleJSON("" + lat, "" + lon);
	}
	
	
	public LocationDetails getLocationDetailsSAARC(String lat, String lon) {

		  HttpURLConnection conn = null;
		  BufferedReader br = null;
		  InputStreamReader reader = null;
		  String output, out ="";
		  LocationDetails locationDetails = new LocationDetails();

		  try
		  {
			  URL url = new URL(UrlSigner.getSignedURL("http://maps.googleapis.com/maps/api/geocode/json?latlng="+lat+","+lon+"&sensor=false" +
					  "&client=gme-wipro3"));
			  System.setProperty("http.keepAlive", "false");
			  conn = (HttpURLConnection) url.openConnection();
			  conn.setRequestMethod("GET");
			  conn.setRequestProperty("Accept", "application/json");
			  conn.setRequestProperty("Connection", "close");
			  conn.setConnectTimeout(5000); //set timeout to 5 sec
			  conn.setReadTimeout(5000);
			  conn.connect();
			  if (conn.getResponseCode() != 200) 
			  {
				  throw new Exception("MDA:");
			  }

			  //Reading data from URL
			  reader = new InputStreamReader((conn.getInputStream()));
			  br = new BufferedReader(reader);
			  while ((output = br.readLine()) != null) 
			  {
				  out += output;
			  }

			  try
			  {
				  // Converting Json formatted string into JSON object
				  net.sf.json.JSONObject jsonObj= (net.sf.json.JSONObject) JSONSerializer.toJSON(out);
				  JSONArray results = jsonObj.getJSONArray("results");
				  net.sf.json.JSONObject addressObj = null;

				  if(results!=null && results.size()>0)
				  {
					 /* // Tarun:20211216: Googlemap Channges:Start
											  //Iterate through all the results addresses
											for (int i = 0; i < results.size(); i++) {
											
												//Get the location type
												String locationType = results.getJSONObject(i).getJSONObject("geometry").getString("location_type");
												
												//check if the plus code exists for the address
												boolean hasPlusCode = results.getJSONObject(i).has("plus_code");
									    
									fLogger.debug("JSONObject: " + results.getJSONObject(i).toString() +  " locationType:" + locationType + " hasPlusCode:" + hasPlusCode);
												
												//Select the address which does not have plus code an
												if ((locationType.equalsIgnoreCase("APPROXIMATE") || locationType.equalsIgnoreCase("ROOFTOP"))&& !hasPlusCode) {
													addressObj = results.getJSONObject(i);
												}
											}
						//Tarun:20211216: Googlemap Channges:End
						 */
					  // Shajesh : Reverting Tarun changes
					  addressObj = results.getJSONObject(0);
				  }
				  if(addressObj!=null)
				  {
					  String address = addressObj.getString("formatted_address");
					  locationDetails.setAddress(address);
					  JSONArray address_components = addressObj.getJSONArray("address_components");

					  for (int i = 0; i < address_components.size(); i++) 
					  {
						  net.sf.json.JSONObject addressCompObj = address_components.getJSONObject(i);
						  if(addressCompObj==null)
							  continue;

						  JSONArray types = addressCompObj.getJSONArray("types");
						  if(types==null)
							  continue;

						  String componenet = types.getString(0);
						  if(componenet==null)
							  continue;

						  if(locationDetails.getAddress()==null)
						  {
							  if(addressCompObj.getString("long_name")!=null &&  addressCompObj.getString("long_name").trim().length()>0)
								  locationDetails.setAddress(addressCompObj.getString("long_name"));
						  }

						  if(componenet.equalsIgnoreCase("administrative_area_level_1"))
						  {
							  if(addressCompObj.getString("long_name")!=null &&  addressCompObj.getString("long_name").trim().length()>0)
								  locationDetails.setState(addressCompObj.getString("long_name"));
						  }

						  if(componenet.equalsIgnoreCase("administrative_area_level_2"))
						  {
							  if(addressCompObj.getString("long_name")!=null &&  addressCompObj.getString("long_name").trim().length()>0)
								  locationDetails.setCity(addressCompObj.getString("long_name"));
						  }

						  if(componenet.equalsIgnoreCase("country"))
						  {
							  if(addressCompObj.getString("long_name")!=null &&  addressCompObj.getString("long_name").trim().length()>0)
							  		locationDetails.setCountry(addressCompObj.getString("long_name"));
						  }

						  if(componenet.equalsIgnoreCase("postal_code"))
						  {
							  if(addressCompObj.getString("long_name")!=null &&  addressCompObj.getString("long_name").trim().length()>0)
								  locationDetails.setPin(addressCompObj.getString("long_name"));
						  }

					  }              

				  }
				  
				  if(locationDetails.getAddress() == null)
					  locationDetails.setAddress("undefined");
				  
				  if(locationDetails.getCity() == null)
					  locationDetails.setCity("undefined");
				  
				  if(locationDetails.getCountry() == null)
					  locationDetails.setCountry("undefined");
				  
				  if(locationDetails.getPin() == null)
					  locationDetails.setPin("undefined");
				  
				  if(locationDetails.getState() == null)
					  locationDetails.setState("undefined");

				  }

			  catch(Exception e)
			  {
				e.printStackTrace();
			  }
		  }

		  catch(SocketTimeoutException e1)
		  {
			  e1.printStackTrace();
			  locationDetails.setAddress("undefined");
			  locationDetails.setCity("undefined");
			  locationDetails.setCountry("undefined");
			  locationDetails.setPin("undefined");
			  locationDetails.setState("undefined");
		  }

		  catch(Exception e)
		  {
			  e.printStackTrace();
			  locationDetails.setAddress("undefined");
			  locationDetails.setCity("undefined");
			  locationDetails.setCountry("undefined");
			  locationDetails.setPin("undefined");
			  locationDetails.setState("undefined");
		  }

		  finally
		  {
			  try
			  {
				  if(conn!=null)
					  conn.disconnect();

				  if(reader!=null)
					  reader.close();

				  if(br!=null)
					  br.close();
			  }

			  catch(IOException e1)
			  {
				  e1.printStackTrace();
			  }
		  }
		  return locationDetails;
	}
	//DF20180912 MA369757 ::GOOGLE implementation for india. Excluding the MapMyIndia.
	public static LocationDetails getAddressGoogleJSON(String latlon)
	{
		  Logger iLogger = InfoLoggerClass.logger;
		  Logger fLogger = FatalLoggerClass.logger;
		  HttpURLConnection conn = null;
		  BufferedReader br = null;
		  InputStreamReader reader = null;
		  String output, out ="";
		  LocationDetails locationDetails = new LocationDetails();

		  try
		  {
			  URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng="+latlon+"&client=gme-wiprolimited2&key=AIzaSyCRT3M5UrWqVlIOcn_U2dtmkh8-LJLMCYg");
			  System.setProperty("http.keepAlive", "false");
			  conn = (HttpURLConnection) url.openConnection();
			  conn.setRequestMethod("GET");
			  conn.setRequestProperty("Accept", "application/json");
			  conn.setRequestProperty("Connection", "close");
			  conn.setConnectTimeout(5000); //set timeout to 5 sec
			  conn.setReadTimeout(5000);
			  conn.connect();
			  if (conn.getResponseCode() != 200) 
			  {
				  fLogger.info("Failed : HTTP error code : "+conn.getResponseCode());
				  throw new Exception("getAddressFromGoogleMapsIndia for LatLong:"+latlon+" ;  From Google Maps India:" +

                                    "Failed : HTTP error code : "+ conn.getResponseCode());
			  }

			  //Reading data from URL
			  reader = new InputStreamReader((conn.getInputStream()));
			  br = new BufferedReader(reader);
			  while ((output = br.readLine()) != null) 
			  {
				  out += output;
			  }

			  try {
					//Shajesh : 20211229 : Google Map Address
					// Converting Json formatted string into JSON object
					net.sf.json.JSONObject jsonObj = (net.sf.json.JSONObject) JSONSerializer
							.toJSON(out);
					JSONArray results = jsonObj.getJSONArray("results");
					net.sf.json.JSONObject addressObj = null;
					String locationType=null;				
					if (results != null && results.size() > 0) {
						for (int i = 0; i < results.size(); i++) {
							System.out.println("-- results --"+results);
							// Get the location type
							//CR-270.o
							/* locationType = results.getJSONObject(i).getJSONObject("geometry").getString("location_type");
							 System.out.println("---- locationType -----"+ locationType);					
							//check if the plus code exists for the address
								boolean hasPlusCode = results.getJSONObject(i).has("plus_code");
								
								//String hasPlusCode = results.getJSONObject(i).getString("plus_code");
								//System.out.println("-- hasPlusCode --"+hasPlusCode);								
							if (locationType.equalsIgnoreCase("ROOFTOP") && hasPlusCode) {							
									addressObj =results.getJSONObject(i);
									break;
								}	
								else if (locationType.equalsIgnoreCase("GEOMETRIC_CENTER") && hasPlusCode) {							
								addressObj =results.getJSONObject(i);
								break;
								}	
								else if (locationType.equalsIgnoreCase("APPROXIMATE")&& hasPlusCode) {							
									addressObj =results.getJSONObject(i);
									break;
								}*/	
							//CR-270.n	
							    //CR-270-SN
								JSONArray types = results.getJSONObject(i).getJSONArray("types");
								boolean hasPlusCode = types.contains("plus_code");
								if(hasPlusCode == true)
								{
									continue;
								}
								else
								{
									addressObj =results.getJSONObject(i);
									break;
								}
								//CR-270-EN
						}
					}
					//System.out.println("*********** addressObj ***********" + addressObj);
				  if(addressObj!=null)
				  {
					  
					  String address = addressObj.getString("formatted_address");	
					  //System.out.println("--------- formatted_address ---------------"+addressObj.getString("formatted_address"));
					//CR-270.o
					  /*String plusCodeChar = addressObj.getJSONObject("plus_code").getString("compound_code").substring(0, 7);
					  System.out.println("--------- plusCodeChar ---------------"+plusCodeChar);
					  if(address.contains(plusCodeChar)){
						  address = addressObj.getString("formatted_address").substring(7);
					  }else{
						  address = addressObj.getString("formatted_address");
					  }*/
					//CR-270.n
					//CR-270-SN
					  if(address.contains("+"))
					  {
						  
						  address =  address.substring(address.indexOf(" ")+1);
					  }
					  else
					  {
						  address = addressObj.getString("formatted_address");
					  }
					  //CR-270-EN
					  //System.out.println("--------- address ---------------"+address);
					  locationDetails.setAddress(address);
					  JSONArray address_components = addressObj.getJSONArray("address_components");

					  for (int i = 0; i < address_components.size(); i++) 
					  {
						  net.sf.json.JSONObject addressCompObj = address_components.getJSONObject(i);
						  if(addressCompObj==null)
							  continue;

						  JSONArray types = addressCompObj.getJSONArray("types");
						  iLogger.info("JSONArray Types for latlong : "+latlon+" are : "+types);
						  if(types==null)
							  continue;

						  String componenet = types.getString(0);
						  if(componenet==null)
							  continue;

						  if(locationDetails.getAddress()==null)
						  {
							  if(addressCompObj.getString("long_name")!=null &&  addressCompObj.getString("long_name").trim().length()>0)
								  locationDetails.setAddress(addressCompObj.getString("long_name"));
							 
						  }

						  if(componenet.equalsIgnoreCase("administrative_area_level_1"))
						  {
							  if(addressCompObj.getString("long_name")!=null &&  addressCompObj.getString("long_name").trim().length()>0)
								  locationDetails.setState(addressCompObj.getString("long_name"));
							 
						  }

						  if(componenet.equalsIgnoreCase("administrative_area_level_2"))
						  {
							  if(addressCompObj.getString("long_name")!=null &&  addressCompObj.getString("long_name").trim().length()>0)
								  locationDetails.setCity(addressCompObj.getString("long_name"));
							  
						  }

						  if(componenet.equalsIgnoreCase("country"))
						  {
							  if(addressCompObj.getString("long_name")!=null &&  addressCompObj.getString("long_name").trim().length()>0)
							  		locationDetails.setCountry(addressCompObj.getString("long_name"));
							  
						  }

						  if(componenet.equalsIgnoreCase("postal_code"))
						  {
							  if(addressCompObj.getString("long_name")!=null &&  addressCompObj.getString("long_name").trim().length()>0)
								  locationDetails.setPin(addressCompObj.getString("long_name"));
							 
						  }

					  }              

				  }
				  
				  if(locationDetails.getAddress() == null)
					  locationDetails.setAddress("undefined");
				  
				  if(locationDetails.getCity() == null)
					  locationDetails.setCity("undefined");
				  
				  if(locationDetails.getCountry() == null)
					  locationDetails.setCountry("undefined");
				  
				  if(locationDetails.getPin() == null)
					  locationDetails.setPin("undefined");
				  
				  if(locationDetails.getState() == null)
					  locationDetails.setState("undefined");

			  }
			  catch(ArrayIndexOutOfBoundsException e){
				  fLogger.info("getAddressGoogleJSON() for latlong : "+latlon+" "+e.getMessage());
				  iLogger.info("getAddressGoogleJSON() for latlong : "+latlon+" "+e.getMessage());
			  }
			  catch(Exception e)
			  {
				  fLogger.info("getAddressGoogleJSON() for latlong : "+latlon+" "+e.getMessage());
				  iLogger.info("getAddressGoogleJSON() for latlong : "+latlon+" "+e.getMessage());
				//e.printStackTrace();
			  }
		  }

		  catch(SocketTimeoutException e1)
		  {
			  e1.printStackTrace();
			  locationDetails.setAddress("undefined");
			  locationDetails.setCity("undefined");
			  locationDetails.setCountry("undefined");
			  locationDetails.setPin("undefined");
			  locationDetails.setState("undefined");
		  }

		  catch(Exception e)
		  {
			  e.printStackTrace();
			  locationDetails.setAddress("undefined");
			  locationDetails.setCity("undefined");
			  locationDetails.setCountry("undefined");
			  locationDetails.setPin("undefined");
			  locationDetails.setState("undefined");
		  }

		  finally
		  {
			  try
			  {
				  if(conn!=null)
					  conn.disconnect();

				  if(reader!=null)
					  reader.close();

				  if(br!=null)
					  br.close();
			  }

			  catch(IOException e1)
			  {
				  e1.printStackTrace();
			  }
		  }
		  return locationDetails;
	
		
	}
	
	//MMI.sn changes done as part of MMI poc
	
	public static LocationDetailsMMI getAddressMMIJSON(String lattitude, String longitude){
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		LocationDetailsMMI locationDetails = new LocationDetailsMMI();
		String latlong = lattitude+","+longitude;

		HttpURLConnection conn = null;
		BufferedReader br = null;
		InputStreamReader reader = null;
		String output, out ="";

		try {
			//LLOPS-94 : password from configuration file.sn
			String sourceDir = null;
			Properties prop = new Properties();
			try {
				prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
				sourceDir= prop.getProperty("MMIKey");
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				fLogger.fatal("issue in while getting path from configuration path"
						+ e1.getMessage());
			}
			//LLOPS-94 : password from configuration file.en
			iLogger.info("sourceDir"+sourceDir);
			// Leela 	
			//	url = new URL("https://apis.mapmyindia.com/advancedmaps/v1/abcc5a513059e4737bd9a37a7fe616d4/rev_geocode?lng="+latlong.split(",")[1]+"&lat="+latlong.split(",")[0]);
			//URL url = new URL("https://apis.mappls.com/advancedmaps/v1/abcc5a513059e4737bd9a37a7fe616d4/rev_geocode?lat="+lattitude+"&lng="+longitude+"&identifier="+"JCB1234");
			//URL url = new URL("https://apis.mappls.com/advancedmaps/v1/d165e34a786426176ab5d2ab8ac80175/rev_geocode?lat="+lattitude+"&lng="+longitude);//LLOPS-94.o
			URL url = new URL("https://apis.mappls.com/advancedmaps/v1/"+sourceDir+"/rev_geocode?lat="+lattitude+"&lng="+longitude);//LLOPS-94.n

			System.setProperty("http.keepAlive", "false");
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Connection", "close");
			conn.setConnectTimeout(5000); //set timeout to 5 sec
			conn.setReadTimeout(5000);
			conn.connect();

			iLogger.info("conn.getResponseCode() : " + conn.getResponseCode());

			if (conn.getResponseCode() != 200) 
			{
				throw new Exception("MDA:DInt:SecondaryData:getAddressFromMMI for Lat:"+lattitude+" and Long:"+longitude+"; From MMI:" +
						"Failed : HTTP error code : "+ conn.getResponseCode());
			}

			//Reading data from URL
			reader = new InputStreamReader((conn.getInputStream()));
			br = new BufferedReader(reader);
			while ((output = br.readLine()) != null) 
			{
				out += output;
			}


			//iLogger.info("Time taken Just to fetch location from MMI for "+latlong+": "+ (System.currentTimeMillis() - startTime));
			// Converting Json formatted string into JSON object

			try {
				JSONObject json = (JSONObject) JSONSerializer.toJSON(out);
				iLogger.info("JSONObject json : " + json);
				JSONArray results = json.getJSONArray("results");
				iLogger.info("JSONArray results : " + results);
				JSONObject rec = results.getJSONObject(0);
				iLogger.info("JSONObject rec : " + rec);

				//System.out.println(rec);
				
				if(rec.getString("formatted_address")!=null && rec.getString("formatted_address").trim().length()>0)
					locationDetails.setAddress(rec.getString("formatted_address"));
				else
					locationDetails.setAddress("undefined");
				
				if(rec.getString("area")!=null && rec.getString("area").trim().length()>0)
					locationDetails.setCountry(rec.getString("area"));
				else
					locationDetails.setCountry("undefined");
				
				if(rec.getString("state")!=null && rec.getString("state").trim().length()>0)
					locationDetails.setState(rec.getString("state"));
				else
					locationDetails.setState("undefined");
				
				if(rec.getString("city")!=null && rec.getString("city").trim().length()>0)
					locationDetails.setCity(rec.getString("city"));
				else
					locationDetails.setCity("undefined");
				
				if(rec.getString("district")!=null && rec.getString("district").trim().length()>0)
					locationDetails.setDistrict(rec.getString("district"));
				else
					locationDetails.setDistrict("undefined");
				
				if(rec.getString("village")!=null && rec.getString("village").trim().length()>0)
					locationDetails.setVillage(rec.getString("village"));
				else
					locationDetails.setVillage("undefined");
				
//				locationDetails.setAddress(rec.getString("formatted_address"));
//				locationDetails.setCountry(rec.getString("area"));
//				locationDetails.setState(rec.getString("state"));
//				locationDetails.setCity(rec.getString("city"));
//				locationDetails.setDistrict(rec.getString("district"));
//
//				//					if(!(rec.getString("city").equalsIgnoreCase("")))
//				//						locationDetails.setCity(rec.getString("city"));
//				//					else
//				//						locationDetails.setCity(rec.getString("district"));
//
//				locationDetails.setVillage(rec.getString("village"));  //adding village as part of MMI poc changes
				//System.out.println("Address:"+locationDetails.getAddress()+"State:"+locationDetails.getState()+"City:"+locationDetails.getCity()+"Country:"+locationDetails.getCountry());

				iLogger.info("locationDetails  : " + locationDetails);
			}catch (Exception e) {

				e.printStackTrace();
				locationDetails.setAddress("undefined");
				locationDetails.setCountry("undefined");
				locationDetails.setState("undefined");
				locationDetails.setCity("undefined");
				locationDetails.setDistrict("undefined");
				locationDetails.setVillage("undefined");
				locationDetails.setPin("undefined");

				iLogger.fatal(" Exception while reading JSON "+latlong+e.getMessage());
				throw new Exception("MDA::DInt:SecondaryData:getAddressFrom MMI for Lat:"+lattitude+" and Long:"+longitude+"; From MMI: " +
						"Exception in getting address:"+e.getMessage());
			}
			iLogger.info("Successfully fetched location from mmi for "+(latlong));
			//conn.disconnect();
		} catch(SocketTimeoutException e) {
			locationDetails.setAddress("undefined");
			locationDetails.setCountry("undefined");
			locationDetails.setState("undefined");
			locationDetails.setCity("undefined");
			locationDetails.setVillage("undefined");
			locationDetails.setPin("undefined");
			fLogger.fatal("WISE:DInt:SecondaryData:getAddressFromMMI for Lat:"+lattitude+" and Long:"+longitude+"; From MMI: " +
					"Socket Timeout Exception in getting address:"+e.getMessage());
			iLogger.fatal(" MalformedURLException for location "+latlong+e.getMessage());
		} catch (Exception e) {
			locationDetails.setAddress("undefined");
			locationDetails.setCountry("undefined");
			locationDetails.setState("undefined");
			locationDetails.setCity("undefined");
			locationDetails.setDistrict("undefined");
			locationDetails.setVillage("undefined");
			locationDetails.setPin("undefined");
			iLogger.fatal(" IOException location "+latlong+e.getMessage());
		}
		finally
		{
			try
			{
				if(conn!=null)
					conn.disconnect();

				if(reader!=null)
					reader.close();

				if(br!=null)
					br.close();
			}

			catch(IOException e1)
			{
				fLogger.fatal("MDA:DInt:SecondaryData:getAddressFromMMI for Lat:"+lattitude+" and Long:"+longitude+"; From MMI: " +
						"Exception in closing buffered reader:"+e1.getMessage());
			}
		}
		iLogger.info("Time taken Just to fetch location from MMI and set in Location Object "+(System.currentTimeMillis()-startTime));
		return locationDetails;
	}
	
	//MMI.en
	
	//DF20190404:Abhishek::to get  location details from OSM only.
	public static LocationDetails getLocationDetailsFromOSM(String lattitude,String longitude) {
		Logger iLogger = InfoLoggerClass.logger;
		
		long startTime = System.currentTimeMillis();
		// changed sleep time from 1000 to 750 to avoid the delay by Shrini 140720160520
		try{
			Thread.sleep(750);
		}catch (Exception e) {
			iLogger.info("Error pausing for second for OSM "+e.getMessage());
		}
		
		LocationDetails locationObj = new LocationDetails();
		try{
			//URL url = new URL("http://nominatim.openstreetmap.org/reverse?format=json&lat="+lattitude+"&lon="+longitude+"&zoom=7&addressdetails=1&email=shrinivas.kattimani@wipro.com");
			
			//2016-08-08 : Suresh : Enforcing the Lang settings to English for OSM
			URL url = new URL("https://nominatim.openstreetmap.org/reverse?accept-language=en&format=json&lat="+lattitude+"&lon="+longitude+"&zoom=18&addressdetails=1&email=shrinivas.kattimani@wipro.com");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent", "SampleApp3");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "+ conn.getResponseCode());
			}
			// Reading data's from url
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			String out = "";
			while ((output = br.readLine()) != null) {
				out += output;
			}
			long endTime = System.currentTimeMillis();
			iLogger.info("Time taken Just to fetch location from OSM "+(endTime-startTime));
			try {
				org.json.simple.JSONObject obj=(org.json.simple.JSONObject)org.json.simple.JSONValue.parse(out); 
				try{
					locationObj.setAddress(obj.get("display_name").toString());
				}catch (Exception e) {
				}
				
				org.json.simple.JSONObject arr = (org.json.simple.JSONObject)obj.get("address"); 
				if(arr.get("city")!=null){
					locationObj.setCity(StringSimplifier.simplifiedString(arr.get("city").toString()));
				}else if(arr.get("state_district")!=null){
					locationObj.setCity(StringSimplifier.simplifiedString(arr.get("state_district").toString()));
				}else if(arr.get("village")!=null){
					locationObj.setCity(StringSimplifier.simplifiedString(arr.get("village").toString()));
				}else{
					locationObj.setCity(StringSimplifier.simplifiedString(arr.get("county").toString()));
				}
				
				locationObj.setState(StringSimplifier.simplifiedString(arr.get("state").toString()));
				
				locationObj.setCountry(arr.get("country").toString());
				if(locationObj.getCountry()==null || locationObj.getCountry().equalsIgnoreCase("undefined") ){
					locationObj = getAddressGoogleJSON(lattitude+","+longitude);
				}
				if(locationObj.getAddress() == null)
					locationObj.setAddress("undefined");
				  
				if(locationObj.getCity() == null)
					  locationObj.setCity("undefined");
				  
				if(locationObj.getCountry() == null)
					  locationObj.setCountry("undefined");
				  
				if(locationObj.getPin() == null)
					  locationObj.setPin("undefined");
				  
				if(locationObj.getState() == null)
					  locationObj.setState("undefined");
				else   
					iLogger.info("Successfully fetched location from OSM for "+(lattitude+":"+longitude));
			} catch (Exception e) {
				e.printStackTrace();
				locationObj.setAddress("undefined");
				locationObj.setCity("undefined");
				locationObj.setCountry("undefined");
				locationObj.setPin("undefined");
				locationObj.setState("undefined");
			}
			conn.disconnect();
		}catch (Exception e) {
			e.printStackTrace();
			locationObj.setAddress("undefined");
			locationObj.setCity("undefined");
			locationObj.setCountry("undefined");
			locationObj.setPin("undefined");
			locationObj.setState("undefined");
		}
		iLogger.info("Time taken Just to fetch location from OSM and set in Location Object "+(System.currentTimeMillis()-startTime));
		return locationObj;
	}
	
	//MMI.sn
	public static LocationDetailsMMI getLocationDetailsFromOSMMMI(String lattitude,String longitude) {
		Logger iLogger = InfoLoggerClass.logger;
		
		long startTime = System.currentTimeMillis();
		// changed sleep time from 1000 to 750 to avoid the delay by Shrini 140720160520
		try{
			Thread.sleep(750);
		}catch (Exception e) {
			iLogger.info("Error pausing for second for OSM "+e.getMessage());
		}
		
		LocationDetailsMMI locationObj = new LocationDetailsMMI();
		try{
			//URL url = new URL("http://nominatim.openstreetmap.org/reverse?format=json&lat="+lattitude+"&lon="+longitude+"&zoom=7&addressdetails=1&email=shrinivas.kattimani@wipro.com");
			
			//2016-08-08 : Suresh : Enforcing the Lang settings to English for OSM
			URL url = new URL("https://nominatim.openstreetmap.org/reverse?accept-language=en&format=json&lat="+lattitude+"&lon="+longitude+"&zoom=18&addressdetails=1&email=shrinivas.kattimani@wipro.com");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent", "SampleApp3");
			conn.setRequestProperty("Accept", "application/json");

			iLogger.info("OSM conn.getResponseCode() : " + conn.getResponseCode());
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "+ conn.getResponseCode());
			}
			// Reading data's from url
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			String out = "";
			while ((output = br.readLine()) != null) {
				out += output;
			}
			long endTime = System.currentTimeMillis();
			iLogger.info("Time taken Just to fetch location from OSM "+(endTime-startTime));
			try {
				org.json.simple.JSONObject obj=(org.json.simple.JSONObject)org.json.simple.JSONValue.parse(out); 
				try{
					locationObj.setAddress(obj.get("display_name").toString());
				}catch (Exception e) {
				}
				iLogger.info("OSM obj : " + obj);
				org.json.simple.JSONObject arr = (org.json.simple.JSONObject)obj.get("address"); 
				iLogger.info("OSM arr : " + arr);
				if(arr.get("city")!=null){
					locationObj.setCity(StringSimplifier.simplifiedString(arr.get("city").toString()));
				}else if(arr.get("state_district")!=null){
					locationObj.setCity(StringSimplifier.simplifiedString(arr.get("state_district").toString()));
				}else if(arr.get("village")!=null){
					locationObj.setCity(StringSimplifier.simplifiedString(arr.get("village").toString()));
				}else{
					locationObj.setCity(StringSimplifier.simplifiedString(arr.get("county").toString()));
				}
				
				locationObj.setState(StringSimplifier.simplifiedString(arr.get("state").toString()));
				
				locationObj.setCountry(arr.get("country").toString());
				
				iLogger.info("OSM locationObj : " + locationObj);
				if(locationObj.getCountry()==null || locationObj.getCountry().equalsIgnoreCase("undefined") ){
					locationObj = getAddressMMIJSON(lattitude,longitude);
				}
				if(locationObj.getAddress() == null)
					locationObj.setAddress("undefined");
				  
				if(locationObj.getCity() == null)
					  locationObj.setCity("undefined");
				  
				if(locationObj.getCountry() == null)
					  locationObj.setCountry("undefined");
				  
				if(locationObj.getPin() == null)
					  locationObj.setPin("undefined");
				  
				if(locationObj.getState() == null)
					  locationObj.setState("undefined");
				else   
					iLogger.info("Successfully fetched location from OSM for "+(lattitude+":"+longitude));
			} catch (Exception e) {
				e.printStackTrace();
				locationObj.setAddress("undefined");
				locationObj.setCity("undefined");
				locationObj.setCountry("undefined");
				locationObj.setPin("undefined");
				locationObj.setState("undefined");
			}
			conn.disconnect();
		}catch (Exception e) {
			e.printStackTrace();
			locationObj.setAddress("undefined");
			locationObj.setCity("undefined");
			locationObj.setCountry("undefined");
			locationObj.setPin("undefined");
			locationObj.setState("undefined");
		}
		iLogger.info("Time taken Just to fetch location from OSM and set in Location Object "+(System.currentTimeMillis()-startTime));
		return locationObj;
	}
	//MMI.en
	
	//DF20190404:Abhishek::to get  location details from redis only.
	public  LocationDetails getLocationDetailsFromRedis(String latitude,String longitude,Jedis jedis) {
		Logger iLogger = InfoLoggerClass.logger;	
		Logger fLogger = FatalLoggerClass.logger;
		LocationDetails locationObj = new LocationDetails();

		try
		{
			if(latitude==null || latitude.trim().length()==0 || longitude==null || longitude.trim().length()==0)
			{
				throw new Exception("Incomplete co-ordinates received");
			}
			
			if(latitude.length()>6)
				latitude=latitude.substring(0,7);
			if(longitude.length()>6)
				longitude=longitude.substring(0,7);
			
			String location = null;
			String latlongBucket = latitude.split("\\.")[0]+":"+longitude.split("\\.")[0];
			String key = latitude+"#"+longitude;
			try
			{
				
				if(jedis==null)
				{
				   throw new Exception("Exception in getting Jedis Connection");
				}
				
				if(jedis.hexists(latlongBucket, key))
				{
					location = jedis.hget(latlongBucket, key);
					//DF20180529 - Rajani Nagaraju - Adding NULL Pointer check
					if(location==null)
					{
						locationObj.setAddress("undefined");
						locationObj.setCity("undefined");
						locationObj.setCountry("undefined");
						locationObj.setPin("undefined");
						locationObj.setState("undefined");
						
						return locationObj;
					}
					String addressSplit[] = location.split("\\|");
						
					if(addressSplit[0]==null || addressSplit[0].equalsIgnoreCase("undefined")|| addressSplit[0].equalsIgnoreCase("NULL")
							||addressSplit[2]==null || addressSplit[2].equalsIgnoreCase("undefined")|| addressSplit[2].equalsIgnoreCase("NULL")	
								)
					{
							
						fLogger.info("WISE:DInt:SecondaryData:getAddressFromRedis for Lat:"+latitude+" and Long:"+longitude+"; From Redis: " +
						"Not Defined in Redis");
						locationObj.setAddress("undefined");
						locationObj.setCity("undefined");
						locationObj.setCountry("undefined");
						locationObj.setPin("undefined");
						locationObj.setState("undefined");
							
						return locationObj;
					}
					else{
						iLogger.info("WISE:DInt:SecondaryData:getAddressFromRedis for Lat:"+latitude+" and Long:"+longitude+"; From Redis: " +
						"Address Fetched from Redis");
						locationObj.setCity(addressSplit[0]);
						locationObj.setState(addressSplit[1]);
						locationObj.setCountry(addressSplit[2]);
						locationObj.setAddress(addressSplit[3]);
					}
				}
			}	
			catch(JedisConnectionException e1)
			{
				throw new Exception("Exception in getting Jedis Connection");
			}
			catch(Exception e)
			{
				throw new Exception(e.getMessage());
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("WISE:DInt:SecondaryData:getAddressFromRedis for Lat:"+latitude+" and Long:"+longitude+"; From Redis: " +
					"Exception in getting address:"+e.getMessage());
			
			locationObj.setAddress("undefined");
			locationObj.setCity("undefined");
			locationObj.setCountry("undefined");
			locationObj.setPin("undefined");
			locationObj.setState("undefined");
		}
		
		
		return locationObj;
	}
	
	//DF20190404:Abhishek::to set location details in redis.
	public  String setLocationDetailsToRedis(String latitude,String longitude,LocationDetails locationDetails, Jedis jedis) {
		String status="SUCCESS";
		
		Logger iLogger = InfoLoggerClass.logger;	
		Logger fLogger = FatalLoggerClass.logger;
		
		try
		{
			if(latitude==null || latitude.trim().length()==0 || longitude==null || longitude.trim().length()==0)
			{
				throw new Exception("Incomplete co-ordinates received");
			}
			
			if(latitude.length()>6)
				latitude=latitude.substring(0,7);
			if(longitude.length()>6)
				longitude=longitude.substring(0,7);
			
			String latlongBucket = latitude.split("\\.")[0]+":"+longitude.split("\\.")[0];
			String key = latitude+"#"+longitude;
			String locationDetailsInString= locationDetails.toString();
		
			
			try
			{
				
				
				if(jedis==null)
				{
				   throw new Exception("Exception in getting Jedis Connection");
				}
				
				jedis.hset(latlongBucket, key, locationDetailsInString);
				
				iLogger.info("WISE:SecondaryData:setAddressToRedis:HashKey:"+latlongBucket+"; member:"+key);
				
			}
			catch(JedisConnectionException e1)
			{
				throw new Exception("Exception in getting Jedis Connection");
			}
			catch(Exception e)
			{
				throw new Exception(e.getMessage());
			}
			
		}
		
		catch(Exception e)
		{
			fLogger.fatal("WISE:DInt:SecondaryData:getAddressFromRedis for Lat:"+latitude+" and Long:"+longitude+"; From Redis: " +
					"Exception in getting address:"+e.getMessage());
			return "FAILURE";
		}
		
		
		return status;
	}
}
