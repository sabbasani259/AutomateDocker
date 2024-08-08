/**
 * 
 */
package remote.wise.service.webservice;

import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.ExcavatorVisualizationReportImpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author ROOPN5
 *
 */
@Path("StartAndEndEngineRunHrsRESTService")
public class StartAndEndEngineRunHrsRESTService {
	
	
	Gson gson = new Gson();

	@GET
	@Path("getStartAndEndEngineRunHrsData")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStartAndEndEngineRunHrsData(@QueryParam("assetID") String assetID, @QueryParam("start_date") String startDate, @QueryParam("end_date") String endDate){

		Logger iLogger = InfoLoggerClass.logger;
		HashMap<String,Object> response = null;
		String result=null;

		iLogger.info("StartAndEndEngineRunHrsRESTService:getStartAndEndEngineRunHrsData WebService Input-----> serial_number:"+assetID+" start_date:"+startDate+" end_date:"+endDate);
		long startTime = System.currentTimeMillis();

		response = new ExcavatorVisualizationReportImpl().getStartAndEndEngineRunHrsData(assetID,startDate,endDate);

		if(response!=null)
			result = gson.toJson(
					response,
					new TypeToken<HashMap<String,Object>>() {
					}.getType()).toString();

		long endTime=System.currentTimeMillis();
		iLogger.info("StartAndEndEngineRunHrsRESTService:getStartAndEndEngineRunHrsData WebService Output -----> response:"+response);
		iLogger.info("serviceName:StartAndEndEngineRunHrsRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~"+result);

		return result;


	}

}
