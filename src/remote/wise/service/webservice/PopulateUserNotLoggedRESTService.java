/*
 * ME10008770 : Dhiraj Kumar : 20230809 : Not logged in report Issue - Query modifications
 */

package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.PopulateUserNotLoggedImpl;

@Path("/PopulateUserNotLoggedRESTService")
public class PopulateUserNotLoggedRESTService {

	@GET
	@Path("/populateUserNotLogged")
	@Produces(MediaType.TEXT_PLAIN)
	public String populateUserNotLogged() {
		
		//ME10008770.so
		/*PopulateUserNotLoggedImpl impl = new PopulateUserNotLoggedImpl();
		String result=impl.getUserNotLoggedData();
		if(result!=null && result.equalsIgnoreCase("Success")){
			return "Success";
		}else 
			return "Issue while processing UnderUtilizedmachines";
		*/	
		//ME10008770.eo
		//ME10008770.sn
		Logger iLogger = InfoLoggerClass.logger;
		String response = "FAILURE";
		long startTime = System.currentTimeMillis();
		iLogger.info("PopulateUserNotLoggedRESTService::WebService Start ");
		PopulateUserNotLoggedImpl impl = new PopulateUserNotLoggedImpl();
		response=impl.getUserNotLoggedData();
		
		long endTime=System.currentTimeMillis();
		iLogger.info("ServiceName:PopulateUserNotLoggedRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return response;
		//ME10008770.eo
	}
}

