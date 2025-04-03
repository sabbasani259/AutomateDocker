package remote.wise.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import remote.wise.EAintegration.Qhandler.InterfaceDataProducer;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class NotificationSubscriberDealersTopic {

	public void sendDealerDetailsToKafka(HashMap<String, String> dealerDetails) {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		try {
			iLogger.info("Sending dealer details to Kafka: " + dealerDetails);

			// Create the payload for Kafka
			HashMap<String, String> payload = new HashMap<>(dealerDetails);
			// payload.put("TXN_KEY", "DealerDetails_" + dealerDetails.get("dealerCode"));
			// Print the payload
			System.out.println("Payload: " + payload);
			// Publish the data to Kafka
			new InterfaceDataProducer(payload.get("TXN_KEY"), payload);

			iLogger.info("Dealer details data sent to Kafka topic successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			fLogger.error("Exception caught while sending dealer details to Kafka: " + e.getMessage());
		}
	}

	public void sendPrincipleDetailsToKafka(HashMap<String, String> dealerDetails) {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		try {
			iLogger.info("Sending principle details to Kafka: " + dealerDetails);

			// Create the payload for Kafka
			HashMap<String, String> payload = new HashMap<>(dealerDetails);
			// payload.put("TXN_KEY", "DealerDetails_" + dealerDetails.get("dealerCode"));
			// Print the payload
			System.out.println("Payload: " + payload);
			// Publish the data to Kafka
			new InterfaceDataProducer(payload.get("TXN_KEY"), payload);

			iLogger.info("Principle details data sent to Kafka topic successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			fLogger.error("Exception caught while sending dealer details to Kafka: " + e.getMessage());
		}
	}

	public void sendSubscriberDetailsToKafka(String dealerCode, Map<String, Object> formattedData) {
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		try {
			// Convert formattedData to JSON string
			String payloadJson = new Gson().toJson(formattedData);
			// Remove backslashes from the JSON string
			payloadJson = payloadJson.replace("\\", "");
			iLogger.info("Final Kafka Payload JSON: " + payloadJson);

			// Convert JSON string into a HashMap<String, String> for Kafka
			HashMap<String, String> payloadMap = new HashMap<>();
			payloadMap.put("dealerCode", dealerCode);
			payloadMap.put("data", payloadJson); // Store full JSON as a single string

			// Send to Kafka
			new InterfaceDataProducer(payloadMap.get("dealerCode"), payloadMap);
			iLogger.info("Notification sent to Kafka successfully.");

		} catch (Exception e) {
			e.printStackTrace();
			fLogger.error("Exception while sending to Kafka: " + e.getMessage());
		}
	}

	public Map<String, Object> formatNotificationData(String dealerCode, Map<String, Object> subscriberDetails) {
		  Logger iLogger = InfoLoggerClass.logger;
			Map<String, Object> response = new HashMap<>(); ;
			
			String dealerType = null;
			if (subscriberDetails!=null) {
				dealerType = subscriberDetails.get("dealerType").toString();
			}
			String selectQuery = "SELECT  " + 
					"    nd.DealerCode, " + 
					"    dsd.CommMode,nd.DealerNotificationName, " + 
					"    nd.DealerEmail, " + 
					"    nd.DealerMobileNumber,(CASE " + 
					"    WHEN Subscriber1=NotificationDealerID THEN 'Subscriber1' " + 
					"    WHEN Subscriber2=NotificationDealerID THEN 'Subscriber2' " + 
					"    WHEN Subscriber3=NotificationDealerID THEN 'Subscriber3' END) as SubscriberType " + 
					"FROM " + 
					"    Notification_Dealers nd, " + 
					"    DealerSubscriberDetails dsd, " + 
					"    notification_dealer_types ndt " + 
					"WHERE " + 
					"    nd.NotificationDealerID IN (dsd.Subscriber1 , dsd.Subscriber2, dsd.Subscriber3) " + 
					"        AND dsd.DealerCode = nd.DealerCode " + 
					"        AND dsd.DealerCode = '"+dealerCode+"' " + 
					"        AND ndt.dealer_type_id=dsd.DealerType " + 
					"        and ndt.dealer_type_name='"+dealerType+"'";
			
			iLogger.info("selectQuery"+selectQuery);
			 if (dealerType != null) {
			        // Get data from DB for dealer with dealer type provided
			        ConnectMySQL pool = new ConnectMySQL();
			        try (Connection conn = pool.getConnection();
			             Statement st = conn.createStatement();
			             ResultSet rs = st.executeQuery(selectQuery)) {
			            while (rs.next()) {
			                String subscriberType = rs.getString("SubscriberType");
			                @SuppressWarnings("unchecked")
							Map<String, Object> subscriber = (Map<String, Object>) response.getOrDefault(subscriberType, new HashMap<>());
			                
			                String commMode = rs.getString("CommMode");
			                if (commMode.equals("SMS")) {
			                    Map<String, Object> sms = new HashMap<>();
			                    sms.put("dealerPrincipleName", rs.getString("DealerNotificationName"));
			                    sms.put("mobileNumber", rs.getString("DealerMobileNumber"));
			                    subscriber.put("sms", sms);
			                } else if (commMode.equals("Email")) {
			                    Map<String, Object> email = new HashMap<>();
			                    email.put("dealerPrincipleName", rs.getString("DealerNotificationName"));
			                    email.put("emailId", rs.getString("DealerEmail"));
			                    subscriber.put("email", email);
			                }
			                
			                response.put(subscriberType, subscriber);
			            }
			        } catch (SQLException e) {
			            e.printStackTrace();
			        }
			    }
			 response.put("dealerCode", dealerCode);
			    response.put("dealerType", subscriberDetails.get("dealerType"));

			    return response;
		}
}
