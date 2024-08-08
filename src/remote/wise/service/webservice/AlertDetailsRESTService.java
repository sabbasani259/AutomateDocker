/**
 * 
 */
package remote.wise.service.webservice;

import java.util.LinkedHashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.annotate.JsonProperty;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.AlertDetailsRESTImpl;

/**
 * @author roopn5
 *
 */
@Path("/AlertDetailsRESTService")
public class AlertDetailsRESTService {
	
	@POST
	@Path("/getAlertDetails")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes({MediaType.APPLICATION_JSON})
	public String getAlertDetails(final @JsonProperty("reqObj")LinkedHashMap<String,Object> reqObj){
		List<Integer> loginTenancyIDList=null;
		if(reqObj.get("loginTenancyIDList")!=null)
			loginTenancyIDList=(List<Integer>) reqObj.get("loginTenancyIDList");
		List<Integer> customGroupIdList=null;
		//aj20119610 changes done as a part of group based view
		if(reqObj.get("machineGroupIdList")!=null)
			customGroupIdList=(List<Integer>) reqObj.get("machineGroupIdList");
		
		String alertJSONArray= null;
		Logger iLogger = InfoLoggerClass.logger;
		
		iLogger.info("AlertDetailsRESTService:WebService Input-----> loginTenancyID:"+loginTenancyIDList +" machineGroupIdList:"+customGroupIdList);
		long startTime = System.currentTimeMillis();
		
		alertJSONArray = new AlertDetailsRESTImpl().getAlertDetails(loginTenancyIDList,customGroupIdList);
		
		long endTime=System.currentTimeMillis();
		iLogger.info("AlertDetailsRESTService:WebService Output -----> alertJSONArray:"+alertJSONArray+"; Total Time taken in ms:"+(endTime - startTime));
		return alertJSONArray;
		
		
	}

}
