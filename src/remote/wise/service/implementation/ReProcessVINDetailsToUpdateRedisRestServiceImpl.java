package remote.wise.service.implementation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class ReProcessVINDetailsToUpdateRedisRestServiceImpl {

	@SuppressWarnings("unchecked")
	public String moolDAUpdationForVinDetails() throws CustomFault {
		Logger fLogger = FatalLoggerClass.logger;
		Logger infoLogger = InfoLoggerClass.logger;
		String connIP, connPort, finalJsonString;
		connIP = connPort = finalJsonString = null;
		String result = "FAILURE";
		Connection connection = null;
		Statement statement = null;
		Statement statement2 = null;
		ResultSet rs = null;
		String selectQuery = null;
		String deleteQuery = null;
		String Serial_Number=null;
		/*Serial_Number = SIM_No = IMEI_Number = ICCID = null;*/
		selectQuery = "SELECT * FROM asset_reregistration_data ";
		infoLogger.info("ReProcessVINDetailsToUpdateRedisRestServiceImpl: moolDAUpdationForVinDetails : selectQuery "	+ selectQuery);
		try {
			HashMap<String, String> finalDetailsMap = new HashMap<String, String>();
			ConnectMySQL connMySql = new ConnectMySQL();
			connection = connMySql.getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery(selectQuery);
			while (rs.next()) {
				if (rs.getString("Serial_Number") != null
						&& !rs.getString("Serial_Number").equalsIgnoreCase(
								"null")) {
					Serial_Number= rs.getString("Serial_Number");
					finalDetailsMap.put("machineNum",Serial_Number);
				}
				if (rs.getString("IMEI_Number") != null
						&& !rs.getString("IMEI_Number")
								.equalsIgnoreCase("null")) {
					finalDetailsMap.put("imeinumber",
							rs.getString("IMEI_Number"));
				}
				if (rs.getString("SIM_No") != null
						&& !rs.getString("SIM_No").equalsIgnoreCase("null")) {
					finalDetailsMap.put("simnumber", rs.getString("SIM_No"));
				}
				if (rs.getString("ICCID") != null
						&& !rs.getString("ICCID").equalsIgnoreCase("null")) {
					finalDetailsMap.put("iccidnumber", rs.getString("ICCID"));
				}
				JSONObject jsonObj = new JSONObject();
				jsonObj.putAll(finalDetailsMap);
				finalJsonString = jsonObj.toString();
				infoLogger.info("ReProcessVINDetailsToUpdateRedisRestServiceImpl: moolDAUpdationForVinDetails : finalJsonString "	+ finalJsonString);
				try {
					Properties prop = new Properties();
					prop.load(getClass()
							.getClassLoader()
							.getResourceAsStream(
									"remote/wise/resource/properties/configuration.properties"));
					connIP = prop.getProperty("MDA_ServerIP");
					connPort = prop.getProperty("MDA_ServerPort");
				} catch (Exception e) {
					fLogger.fatal("ReProcessVINDetailsToUpdateRedisRestServiceImpl:moolDAUpdationForVinDetails: "
							+ "Exception in getting Server Details for MDA Layer from properties file: "
							+ e + " for VIN:" + Serial_Number);
					throw new Exception("Error reading from properties file");
				}
				try {
					// Invoking the REST URL from WISE to MOOL DA.
					infoLogger.info("Invoking the REST URL from WISE to MOOL DA. =====> START");
					URL url = new URL("http://" + connIP + ":" + connPort
							+ "/MoolDA/UpdateVINDetailsToRedisService/"
							+ "UpdateVINDetailsToRedis?inputPayloadMap="
							+ URLEncoder.encode(finalJsonString, "UTF-8"));
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					infoLogger.info("-------- Connection Opened--------- ");
					conn.setRequestProperty("Content-Type",
							"text/plain; charset=utf8");
					conn.setRequestProperty("Accept", "text/plain");
					conn.setRequestMethod("GET");
					infoLogger.info(" conn.getResponseCode() " + conn.getResponseCode());
					if (conn.getResponseCode() != 200
							&& conn.getResponseCode() != 204) {
						throw new Exception("Failed : HTTP error code : "
								+ conn.getResponseCode());
					}
					BufferedReader br = new BufferedReader(
							new InputStreamReader((conn.getInputStream())));
					String outputResponse = null;
					while ((outputResponse = br.readLine()) != null) {
						infoLogger
								.info("ReProcessVINDetailsToUpdateRedisRestServiceImpl:moolDAUpdationForVinDetails:"
										+ " Response from {/MoolDA/UpdateVINDetailsToRedisService/}is: "
										+ outputResponse
										+ " for VIN:"
										+ Serial_Number);
					}
					infoLogger.info("-------- outputResponse -------- "	+ outputResponse);
					if (conn.getResponseCode() == 200
							|| conn.getResponseCode() == 204) {
						statement2 = connection.createStatement();
						infoLogger
								.info("----------------- ReProcessVINDetailsToUpdateRedisRestServiceImpl:moolDAUpdationForVinDetails : Serial_number "
										+ Serial_Number
										+ " Delection from asset_reregistration_data table :: START");
						deleteQuery = "DELETE FROM asset_reregistration_data WHERE Serial_Number='"
								+ Serial_Number + "'";
						statement2.executeUpdate(deleteQuery);
						infoLogger
								.info("----------------- ReProcessVINDetailsToUpdateRedisRestServiceImpl:moolDAUpdationForVinDetails : Serial_number "
										+ Serial_Number
										+ " Successfully Deleted from asset_reregistration_data table :: END");
					}
				} catch (Exception e) {
					fLogger.fatal("ReProcessVINDetailsToUpdateRedisRestServiceImpl:moolDAUpdationForVinDetails: "
							+ " Exception in invoking the {/MoolDA/UpdateVINDetailsToRedisService/} URL: "
							+ e.getMessage() + " for VIN:" + Serial_Number);
					throw new Exception("REST URL not available");
				}
			}
			return "SUCCESS";
		} catch (Exception ea) {
			fLogger.fatal("ReProcessVINDetailsToUpdateRedisRestServiceImpl:moolDAUpdationForVinDetails: Connection not avaliable "
					+ ea.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				statement.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}
