package remote.wise.service.webservice;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("ExcavatorData")
public class ExcavatorData {

	@GET
	@Path("getExcavatorData")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getExcavatorData(@QueryParam(value = "vin") String vin,
			@QueryParam(value = "year") int year) {
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fatalLogger = FatalLoggerClass.logger;
		long startTimeMillis = System.currentTimeMillis();
		String responseString = "Failure";
	
		infoLogger.info("Webservice Input : VIN :" + vin + "  :year: " + year );
	

		String  vinsquery = "select * from GoogleMarker_Data";
		ConnectMySQL factory = new ConnectMySQL();
		try (Connection conn = factory.getConnection();
				Statement statement = conn.createStatement();
				){
			
			if(null == vin || vin.isEmpty() ){
				responseString = "VIN not given: VIN and Year are mandatory";
				
				return	 Response.status(Status.BAD_REQUEST).entity(responseString).build();
			}
			if( null == String.valueOf(year) || 0 == year  ){
				responseString = "Year not given: VIN and Year are mandatory";
				return	 Response.status(Status.BAD_REQUEST).entity(responseString).build();
			}
			List<HashMap<String , String>> responseList = new ArrayList<HashMap<String, String>>();
			String  query = "select * from Excavator_Data where serial_number = '"+vin+"' and year = "+ year;
			
			List<String> vinsList = new ArrayList<>();
			ResultSet vinListRS = statement.executeQuery(vinsquery);
			
			
			while(vinListRS.next()){
				
				vinsList.add(vinListRS.getString("Serial_Number"));
			}
			
			if(vinsList.contains(vin)){
			
			ResultSet rs = statement.executeQuery(query);
			while (rs.next()) {
				
				HashMap<String , String> resp = new HashMap<String , String>();
				
				if(null != rs.getString("Serial_Number") ||  !rs.getString("Serial_Number").isEmpty())
					resp.put("vin", rs.getString("Serial_Number"));
				else
					resp.put("vin", "NA");
				
				if(null != rs.getString("Machine_Number") ||  !rs.getString("Machine_Number").isEmpty())
					resp.put("MachineNumber", rs.getString("Machine_Number"));
				else
					resp.put("MachineNumber", "NA"); 
				
				if(null != rs.getString("Latitude") ||  !rs.getString("Latitude").isEmpty())
					resp.put("Latitude", rs.getString("Latitude"));
				else
					resp.put("Latitude", "NA");
				
				if(null != rs.getString("Longitude") ||  !rs.getString("Longitude").isEmpty())
					resp.put("Longitude", rs.getString("Longitude"));
				else
					resp.put("Longitude", "NA");
				
				if(null != rs.getString("Date_of_Communication") ||  !rs.getString("Date_of_Communication").isEmpty())
					resp.put("Date of Communication", rs.getString("Date_of_Communication"));
				else
					resp.put("Date of Communication", "NA");
				
				if(null != rs.getString("HMR") ||  !rs.getString("HMR").isEmpty())
					resp.put("HMR", rs.getString("HMR"));
				else
					resp.put("HMR", "NA");
				
				responseList.add(resp);
			}
			
			if(responseList.size() == 0 || responseList.isEmpty() ){
					return	 Response.status(Status.BAD_REQUEST).entity("For given year and VIN, data not in table: VIN has no data for given year").build();
			
			}
			 responseString = new ObjectMapper().writeValueAsString(responseList);
			}
			else{
				responseString = "VIN not in given list";
				infoLogger.info("Exception occurred:  Data for selected vin is not available");
				return	 Response.status(Status.BAD_REQUEST).entity(responseString).build();
			}
		
			
			
		
	} catch (Exception e) {
		fatalLogger.fatal("Exception occurred:" + e.getMessage());
	}
		
		long endTimeMillis = System.currentTimeMillis();
		infoLogger
				.info("Webservice Output :" + responseString + ": Time taken:" + (endTimeMillis - startTimeMillis) + "ms~~");
		return	 Response.status(Status.OK).entity(responseString).build();
		
	}
}
