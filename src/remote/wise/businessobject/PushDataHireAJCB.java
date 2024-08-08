package remote.wise.businessobject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;

//import remote.wise.util.WiseLogger;

public class PushDataHireAJCB {
	//static WiseLogger infoLogger = WiseLogger.getLogger("PushDataHireAJCB:","info");
	
	public static void pushData(String url, String uname, String passwrd,  String data){
		 Logger infoLogger = InfoLoggerClass.logger;
		try {
			
//			URL pushdataurl = new URL("http://localhost:26000/SendSMSRS/sendSMSWS/SendSMS?phoneNumber=9620813413");
			URL pushdataurl = new URL(url+"?data="+data+"&uname="+uname+"&pwd="+passwrd);
			HttpURLConnection conn = (HttpURLConnection) pushdataurl.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			infoLogger.info("Exception while pushing data for service "+url+"\n"+e.getMessage());
		} catch (IOException e) {
			infoLogger.info("Exception while pushing data for service "+url+"\n"+e.getMessage());
		}
	}
}