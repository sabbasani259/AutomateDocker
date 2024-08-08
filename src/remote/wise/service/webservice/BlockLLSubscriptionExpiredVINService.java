/**
 * 
 */
package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.BlockLLSubscriptionExpiredVINImpl;

/**
 * @author KO369761
 *
 */
@Path("/BlockVIN")
public class BlockLLSubscriptionExpiredVINService {
	
	@GET
	@Path("LLExpiredVIN")
	@Produces(MediaType.TEXT_HTML)
	public String blockLLExpiredVIN(){
		String response= null;
		Logger iLogger = InfoLoggerClass.logger;
		
		iLogger.info("BlockLLSubscriptionExpiredVIN:WebService");
		long startTime = System.currentTimeMillis();
		
		response = new BlockLLSubscriptionExpiredVINImpl().blockLLExpiredVIN();
		
		long endTime=System.currentTimeMillis();
		iLogger.info("BlockLLSubscriptionExpiredVIN:WebService Output -----> Response:"+response+"; Total Time taken in ms:"+(endTime - startTime));
		iLogger.info("serviceName:BlockLLSubscriptionExpiredVINService~executionTime:"+(endTime-startTime)+"~"+""+"~"+response);
		
		return response;
		
		
	}

}
