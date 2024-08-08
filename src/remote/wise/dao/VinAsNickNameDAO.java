package remote.wise.dao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.json.simple.JSONObject;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.HibernateUtil;

public class VinAsNickNameDAO {
	Logger fLogger = FatalLoggerClass.logger;
	Logger iLogger = InfoLoggerClass.logger;
	
	public boolean searchVin(String vin) {
		String searchQuery = "select Serial_Number  from asset  where  Serial_Number="
				+ "'" + vin + "'";
		boolean found = false;
		iLogger.info("searchQuery : "+searchQuery);
		ConnectMySQL connFactory = new ConnectMySQL();
		try (Connection connection = connFactory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(searchQuery);) {

			while (rs.next()) {
				found = true;
			}

		} catch (SQLException se) {
			fLogger.fatal("issue with query " + searchQuery + se.getMessage());
		} catch (Exception e) {
			fLogger.fatal("searchVin() issue " + e.getMessage());
		}
		return found;
	}

	public String updateNicknameForVin(String vin, String nickName) {
		ConnectMySQL connFactory = new ConnectMySQL();
		String response = "Success";
		String updateQuery = "update asset set nickName="
				+ "'" + nickName + "'" + " where Serial_Number=" + "'"
				+ vin + "'";
		iLogger.info("updateQueryForMACustomer : "+updateQuery);
		try (Connection connection = connFactory.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(updateQuery);){
			statement.executeUpdate(updateQuery);			
		} catch (Exception e) {
			response = "failure";
			fLogger.fatal("updateNicknameForVin()::issue while updating DB "
					+ e.getMessage());
		}
		// Shajesh : 13-10-2021 : MA Report Changes
		try{
		response = updateNickNameForVinInMoolDA(vin,nickName);
		}catch (Exception e) {
			fLogger.fatal("Exception in updating  the {/MoolDA/assetProfile/} URL for NickName"+ e.getMessage()+" for VIN:"+vin);
			return "FAILURE";
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private String updateNickNameForVinInMoolDA(String vin, String nickName ) throws Exception {
		String connIP, connPort,finalJsonString ;
		connIP = connPort=finalJsonString =  null;
		String result = "FAILURE";
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		HashMap<String,String> finalNickNameDetailsMap = new HashMap<String,String>();
		finalNickNameDetailsMap.put("AssetID", vin);
		finalNickNameDetailsMap.put("NickName", nickName);
		JSONObject jsonObj = new JSONObject();
		jsonObj.putAll(finalNickNameDetailsMap);
		finalJsonString = jsonObj.toString();
		try{
			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream
					("remote/wise/resource/properties/configuration.properties"));
			connIP = prop.getProperty("MDA_ServerIP");
			connPort = prop.getProperty("MDA_ServerPort");		
			}catch (Exception e) {
				fLogger.fatal("VinAsNickNameDAO:updateNickNameForVinInMoolDA: " +
						"Exception in getting Server Details for MDA Layer from properties file: " +e+" for VIN:"+vin );
				throw new Exception("Error reading from properties file");
			}
			try{
				//Invoking the REST URL from WISE to MOOL DA.
				URL url = new URL("http://"+connIP+":"+connPort+"/MoolDA/NickNameUpdate/" +
						"inputPayloadDetails?inputPayloadMap="+URLEncoder.encode(finalJsonString, "UTF-8"));
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("Content-Type", "text/plain; charset=utf8");
				conn.setRequestProperty("Accept", "text/plain");
				conn.setRequestMethod("GET");
				if (conn.getResponseCode() != 200 && conn.getResponseCode() != 204) {
					throw new Exception("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(conn.getInputStream())));
				String outputResponse = null;
				while((outputResponse = br.readLine()) != null){
					iLogger.info("VinAsNickNameDAO:updateNickNameForVinInMoolDA:" +
							" Response from {/MoolDA/assetProfile/}is: "+outputResponse+" for VIN:"+vin);
				}
				iLogger.info("VinAsNickNameDAO:updateNickNameForVinInMoolDA: Mool DA updated succesfully" );
				return "SUCCESS";
			}catch (Exception e) {
				fLogger.fatal("VinAsNickNameDAO:updateNickNameForVinInMoolDA: " +
						" Exception in invoking the {/MoolDA/assetProfile/} URL: "+ e.getMessage()+" for VIN:"+vin);				
				
			}
		return result;
	}

	public boolean checkIfNickNameIsPresentInSystem(String nickName) {
		String searchQueryForNickName = "select nickName  from asset  where  nickName="
				+ "'" + nickName + "'";
		boolean found = false;
		iLogger.info("searchQueryForNickName : "+searchQueryForNickName);
		ConnectMySQL connFactory = new ConnectMySQL();
		try (Connection connection = connFactory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(searchQueryForNickName);) {

			while (rs.next()) {
				found = true;
			}

		} catch (SQLException se) {
			fLogger.fatal("issue with query " + searchQueryForNickName + se.getMessage());
		} catch (Exception e) {
			fLogger.fatal("checkIfNickNameIsPresentInSystem() issue " + e.getMessage());
		}
		return found;
	}
}
