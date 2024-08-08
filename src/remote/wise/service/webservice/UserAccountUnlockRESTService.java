/**
 * 
 */
package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.UserAccountUnlockImpl;

/**
 * @author ROOPN5
 *
 */
@Path("/UserAccountUnlockRESTService")
public class UserAccountUnlockRESTService {
	
	@GET()
	@Path("unLockAccountDetails")
	@Produces("text/plain")
	public String unLockAccountDetails(){
		
	
		
		Logger iLogger = InfoLoggerClass.logger;
		long startTime = System.currentTimeMillis();
		
		String response_msg= new UserAccountUnlockImpl().unLockAccountDetails();
		long endTime = System.currentTimeMillis();;
		iLogger.info("UserAccountUnlockRESTService Webservice Execution Time in ms:"+(endTime-startTime));
		iLogger.info("UserAccountUnlockRESTService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response_msg);
		return response_msg;
		
	}

}
