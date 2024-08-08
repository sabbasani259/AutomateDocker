//CR334 : 20220830 : Dhiraj K : Billability Module Integration changes
package remote.wise.util;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

public class BillingSubscriptionHistory {
	
	public String updateSubsHistory(String vin, String subscriptionStartDate, String subscriptionEndDate) {
		
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		
		String response = "SUCCESS";
		URL url = null;
		int responseCode = 0;
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("vin", vin);
		map.put("subscriptionStartDate", subscriptionStartDate);
		map.put("subscriptionEndDate", subscriptionEndDate);
		
		iLogger.info("Map : " + map);
		
		try {
			String jcbRolloffJson = new ObjectMapper().writeValueAsString(map);			
			String ip = null;
			String port = null;
			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			ip =  prop.getProperty("BillabilityIP");
			port = prop.getProperty("BillabilityPORT");
			url = new URL("http://" + ip + ":" + port + "/Billability/subscriptionHistoryService/setSubscriptionHistory");
			iLogger.info("URL : " + url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			
			OutputStream os = (OutputStream) conn.getOutputStream();
			os.write(jcbRolloffJson.getBytes());
			os.flush();

			responseCode = conn.getResponseCode();
			if (responseCode != 200) {
				response = "FAILURE";
				fLogger.fatal("Failed : HTTP error code : " + conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			conn.disconnect();
		} catch (Exception e) {
			response = "FAILURE";
			e.printStackTrace();
			fLogger.fatal("Exception in calling Billability setSubscriptionHistory service.", e);
		}
		return response;
	}
}
