/**
 * 
 */
package remote.wise.service.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.TruncateTAssetMonDataJCBdefaultImpl;

/**
 * @author roopn5
 * 
 */
@Path("/TruncateTAssetMonDataJCBdefaultService")
public class TruncateTAssetMonDataJCBdefaultService {

	@GET()
	@Path("setTAssetMonDataJCBdefaultService")
	@Produces("text/plain")
	public String setTAssetMonDataJCBdefaultData() throws CustomFault {
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("---- Webservice Input ------");
		long startTime = System.currentTimeMillis();

		String response = new TruncateTAssetMonDataJCBdefaultImpl()
				.setTAssetMonDataJCBdefaultData();
		long endTime = System.currentTimeMillis();
		iLogger.info("Webservice Execution Time in ms:" + (endTime - startTime));
		iLogger.info("----- Webservice Output-----");
		return response;
	}

}
