package remote.wise.service.webservice;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.ACUResponse;
import remote.wise.util.AirtelUtil;

//20231127 CR452-AirtelApiIntegration-prasad

@Path("AirtelSim")
public class AirtelApiIntegration {

	@GET
	@Path("airtelSimAccessToken")
	@Produces(MediaType.APPLICATION_JSON)
	public String airtelSimAccessToken() throws CustomFault {

		return AirtelUtil.genarateAccessToken();

	}

	@GET
	@Path("getBasketDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getBasketDetails(@QueryParam("accessId") String accessId) throws CustomFault {
		Logger iLogger = InfoLoggerClass.logger;
		List<String> basketIds = new ArrayList<>();
		basketIds = AirtelUtil.getBasketDetails(accessId);
		iLogger.info("basketIds         " + basketIds);
		return basketIds;

	}
	
	@GET
	@Path("updateSimDetailsIntoDb")
	@Produces(MediaType.APPLICATION_JSON)
	public String updateSimDetailsIntoDb(@QueryParam("accessId") String accessId) throws CustomFault {

		Logger iLogger = InfoLoggerClass.logger;
		Logger fatalLogger = FatalLoggerClass.logger;
		StringBuffer logBuffer = new StringBuffer("");
		String tokenDetails = null;
		String[] splittokenDetails = null;
		String tokenID = null;
		Map<String , Map<String ,String>> simDetails = new HashMap<String, Map<String,String>>();
		List<String> basketIds = new ArrayList<>();


			 tokenDetails = AirtelUtil.genarateAccessToken();
			 splittokenDetails = tokenDetails.split("\\|");
			 tokenID = splittokenDetails[0];
			if (tokenID != null || !tokenID.isEmpty()){
				basketIds = AirtelUtil.getBasketDetails(tokenID);
				iLogger.info("No Of basketIds ::"+basketIds.size() + " Total List OF BasketId :: "+basketIds);
				logBuffer.append("No Of basketIds ::"+basketIds.size() + " Total List OF BasketId :: "+basketIds);

				if(basketIds != null || !basketIds.isEmpty()){
					for(int i =0 ; i< basketIds.size() ; i++)
					{
						simDetails.putAll(AirtelUtil.getSimDetails(tokenID, basketIds.get(i) ));
				
					}
				
				}
				
				iLogger.info("Count Of SimDetails From Airtel Apis :: "+simDetails.size());
				logBuffer.append(" \n Count Of SimDetails From Airtel Apis :: "+simDetails.size());
				
			}
	
			List<ACUResponse> simAndVin = AirtelUtil.getSimAndVin();
			
			iLogger.info("Count Of simAndVin From DB :: "+simAndVin.size());
			logBuffer.append(" \n Count Of simAndVin From DB :: "+simAndVin.size());
			
			List<ACUResponse> finalObj = new ArrayList<>();
			
		     for( ACUResponse obj : simAndVin ){
		    	 ACUResponse  resp = new ACUResponse();
		    	 if(simDetails.containsKey(obj.getSimNo())){
		    		 resp.setSimNo(obj.getSimNo());
		    		 resp.setIMEINo(obj.getIMEINo());
		    		 resp.setVin(obj.getVin());
		    		 resp.setStatus(simDetails.get(obj.getSimNo()).get("status"));
		    		 resp.setICCID(simDetails.get(obj.getSimNo()).get("ICCID"));
		    		 finalObj.add(resp);
		    	 }
		     }
		     iLogger.info("Count Of Sim Matching From Airtel and DB :: "+finalObj.size());
		     logBuffer.append(" \n Count Of Sim Matching From Airtel and DB :: "+finalObj.size());
		     String truncateDataFromTable = AirtelUtil.truncateDataFromTable();
		     iLogger.info("Status of  truncate Data FromTable :: "+truncateDataFromTable);
		     logBuffer.append(" \n Status of  truncate Data FromTable :: "+truncateDataFromTable);
		     
		     int i =0;
		     for( ACUResponse obj : finalObj ){
		    	 
		    	 String insertDataIntoTable = AirtelUtil.insertDataIntoTable(obj);
		    	 if( insertDataIntoTable.equalsIgnoreCase("SUCCESS"))
		    	 i++;
		    	 
		     }
		     iLogger.info("Count of Total records intserted Into Airtel_SIM_Data_TEMP  :: "+i);
		     logBuffer.append(" \n Count of Total records intserted Into Airtel_SIM_Data_TEMP  :: "+i);
		     
		     
		     String swapDataFromTable = AirtelUtil.swapDataFromTable();
		     iLogger.info("Status of Swap table :: "+swapDataFromTable);
		     logBuffer.append(" \n Status of Swap table :: "+swapDataFromTable);

		return logBuffer.toString();

	}
	
	@GET()
	@Path("InsertDataIntoCSVFILE")
	@Produces(MediaType.APPLICATION_JSON)
	public String InsertDataIntoCSVFILE(){
		
	   
		Logger iLogger = InfoLoggerClass.logger;
		
		String response_msg= new AirtelUtil().insertIntoCSVFile();
		
		iLogger.info("Webservice Output: " + response_msg );
		return response_msg;
		
	}

	
	
	@GET
	@Path("getSimAndVindetails")
	@Produces(MediaType.APPLICATION_JSON)
	public 	List<ACUResponse> getSimAndVindetails() throws CustomFault {

		return AirtelUtil.getSimAndVin();

	}
	
	

//	@GET
//	@Path("airtelSimRefreshToken")
//	@Produces(MediaType.APPLICATION_JSON)
//	public String airtelSimRefreshToken() {
//
//		OutputStream os = null;
//		String output = "SUCCESS";
//		Logger iLogger = InfoLoggerClass.logger;
//		Logger fatalLogger = FatalLoggerClass.logger;
//		BufferedReader br = null;
//		String result = null;
//		ObjectMapper mapper = new ObjectMapper();
//
//		try {
//			String URL = "https://openapi.airtel.in/iot/api/developer/generate/authtoken";
//
//			System.out.println("airtelSimAccessToken UrL:" + URL);
//			iLogger.info("airtelSimAccessToken UrL:" + URL);
//
//			URL url = new URL(URL);
//			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//			connection.setDoOutput(true);
//			connection.setRequestMethod("POST");
//			connection.setRequestProperty("Content-Type", "application/json");
//			connection.setRequestProperty("Accept", "application/json");
//			connection.setRequestProperty("apikey", "SOkYF8XTG7OM6sMAqvT3VFdv3OQ0SQzL");
//			connection.setRequestProperty("client_secret", "epKwyVjCdtZMLhR5DjUJ7w9axoM0Zisj");
//			connection.setRequestProperty("client_id", "Fn7wFLcAruXuQLI0Q3Nt1Ue7WTbOP3my");
//			connection.setRequestProperty("iv-user", "developer.16170@jcb.com");
//
//			System.out.println("airtelSimAccessToken ::---> HTTP code from Wise :" + connection.getResponseCode());
//			iLogger.info("airtelSimAccessToken ::---> HTTP code from Wise :" + connection.getResponseCode());
//			if (connection.getResponseCode() != 200) {
//
//				output = "FAILURE";
//				iLogger.info(
//						"Wise report status: FAILURE for WrapAssetServiceScheduleImp Service Report ::Response Code:"
//								+ connection.getResponseCode());
//				System.out.println(
//						"Wise report status: FAILURE for WrapAssetServiceScheduleImp Service Report ::Response Code:"
//								+ connection.getResponseCode());
//
//				return output;
//			} else {
//
//				br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
//				while ((result = br.readLine()) != null) {
//					output = result;
//				}
//				System.out.println("airtelSimAccessToken ::---> output :" + output);
//				iLogger.info("airtelSimAccessToken ::---> output :" + output);
//				return output;
//
//			}
//		} catch (Exception e) {
//			output = "FAILURE";
//			System.out.println("airtelSimAccessToken :Exception" + e.getMessage());
//			fatalLogger.fatal("airtelSimAccessToken :Exception" + e.getMessage());
//			return output;
//		}
//
//	}

}
